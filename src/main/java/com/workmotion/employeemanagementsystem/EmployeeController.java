package com.workmotion.employeemanagementsystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/employees")
public class EmployeeController {
	@Autowired
	EmployeeService employeeService;
	
	@Value("#{${states}}")
	private Map<String, String> states;

	@Value("#{${substates}}")
	private Map<String, String> substates;

	//can be replaced by memcache or redis
	private Map<Long, Set<String>> statesBucket=new HashMap<>();
	
	@PostMapping(path = "/", consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> addEmployee(@RequestBody Employee employee) {
		if((null==employee.getName())||(employee.getName().isBlank())) {
			return new ResponseEntity<>("Please provide employee name", HttpStatus.BAD_REQUEST);
		}
		employee.setState(STATE.ADDED.toString());
		Employee empDB = employeeService.save(employee);
		Set<String> completedStates=new HashSet<>();
		completedStates.add(STATE.ADDED.toString());
		statesBucket.put(empDB.getId(), completedStates);
		return new ResponseEntity<>(employee, HttpStatus.OK);
	}
	
	@PutMapping(path = "/", consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> updateEmployee(@RequestBody Employee employee) {
		
		Optional<Employee> optional = employeeService.findById(employee.getId());
		Employee empDB = null;
		if(optional.isPresent()) {
			empDB=optional.get();
		}else {
			return new ResponseEntity<>("Invalid Employee ID", HttpStatus.BAD_REQUEST);
		}		
		String requestedState=employee.getState();
		String requestedSubstate=employee.getSubstate();
		
		if((null==requestedState)&&(null==requestedSubstate)) {
			return new ResponseEntity<>("Please provide either state or substate in the request", HttpStatus.BAD_REQUEST);
		}
		if((null!=requestedState)&&(null!=requestedSubstate)) {
			return new ResponseEntity<>("Please provide either state or substate in the request, not both", HttpStatus.BAD_REQUEST);
		}
		Set<String> statesCompleted = statesBucket.get(employee.getId());
		if(requestedState!=null) {
			
			if(empDB.getState().equalsIgnoreCase(requestedState)) {
				return new ResponseEntity<>("Employee's current state is already "+requestedState, HttpStatus.BAD_REQUEST);
			}
			if(statesCompleted.contains(requestedState)) {
				return new ResponseEntity<>("State Already Completed", HttpStatus.BAD_REQUEST);
			}
			
			if(!states.containsKey(requestedState)) {
				return new ResponseEntity<>("Invalid Target State Requested", HttpStatus.BAD_REQUEST);
			}else {
				if(!statesCompleted.contains(states.get(requestedState))){
					return new ResponseEntity<>(states.get(requestedState)+" state needs to be completed before this target state", HttpStatus.BAD_REQUEST);
				}else {					
					if(requestedState.equalsIgnoreCase(STATE.INCHECK.toString())) {
						statesCompleted.add(SUBSTATE.SECURITY_CHECK_STARTED.toString());
						statesCompleted.add(SUBSTATE.WORK_PERMIT_CHECK_STARTED.toString());
						statesBucket.put(employee.getId(), statesCompleted);
					}else if(requestedState.equalsIgnoreCase(STATE.ACTIVE.toString())) {
						statesBucket.remove(employee.getId());
					}
					empDB.setState(requestedState);
					employeeService.save(empDB);			  
				}
			}
			
		}else {
			if(statesCompleted.contains(requestedSubstate)) {
				return new ResponseEntity<>("Substate Already Completed", HttpStatus.BAD_REQUEST);
			}
			
			if(!substates.containsKey(requestedSubstate)) {
				return new ResponseEntity<>("Invalid Target Substate Requested", HttpStatus.BAD_REQUEST);
			}else {
				if(!statesCompleted.contains(substates.get(requestedSubstate))) {
					return new ResponseEntity<>(substates.get(requestedSubstate)+" substate needs to be completed before this target substate", HttpStatus.BAD_REQUEST);
				}else {
					if(requestedSubstate.equalsIgnoreCase(SUBSTATE.SECURITY_CHECK_FINISHED.toString())) {
						statesCompleted.add(SUBSTATE.SECURITY_CHECK_FINISHED.toString());
						statesBucket.put(employee.getId(), statesCompleted);
						if(statesCompleted.contains(SUBSTATE.WORK_PERMIT_CHECK_FINISHED.toString())) {
							statesCompleted.add(STATE.APPROVED.toString());
							empDB.setState(STATE.APPROVED.toString());
							employeeService.save(empDB);
						}
					}else if(requestedSubstate.equalsIgnoreCase(SUBSTATE.WORK_PERMIT_CHECK_FINISHED.toString())) {
						statesCompleted.add(SUBSTATE.WORK_PERMIT_CHECK_FINISHED.toString());
						statesBucket.put(employee.getId(), statesCompleted);
						if(statesCompleted.contains(SUBSTATE.SECURITY_CHECK_FINISHED.toString())) {
							empDB.setState(STATE.APPROVED.toString());
							statesCompleted.add(STATE.APPROVED.toString());
							employeeService.save(empDB);
						}
					}
				}
			}
		}				
		return new ResponseEntity<>("Successful State Transition", HttpStatus.OK);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<Object> getEmployee(@PathVariable Long id) {
		Optional<Employee> optional = employeeService.findById(id);
		ResponseEntity<Object> response=null;
		if(optional.isPresent()) {
			response = new ResponseEntity<>(optional.get(), HttpStatus.OK);
		}else {
			response =  new ResponseEntity<>("Invalid Employee ID", HttpStatus.BAD_REQUEST);
		}
		return response;
	}	
}
