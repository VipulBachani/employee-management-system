package com.workmotion.employeemanagementsystem;

import java.util.Map;

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
	
	@PostMapping(path = "/", consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> addEmployee(@RequestBody Employee employee) {
		employee.setState("ADDED");
		employeeService.save(employee);
		return new ResponseEntity<>(employee, HttpStatus.OK);
	}
	
	@PutMapping(path = "/", consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> updateEmployee(@RequestBody Employee employee) {
		Employee empDB = employeeService.findById(employee.getId());
		String currState = empDB.getState();
		String newState=employee.getState();
		ResponseEntity<Object> responseEntity=null;
		
		if ((null==newState)||(newState.isEmpty()) || !states.get(currState).equalsIgnoreCase(newState)) {
			responseEntity = new ResponseEntity<>("Invalid State Transition", HttpStatus.BAD_REQUEST);
		} else if (newState.equalsIgnoreCase(STATE.INCHECK.toString())) {
			empDB.setState(STATE.INCHECK.toString());
			empDB.setSecurityCheck(false);
			empDB.setWorkPermitCheck(false);
			employeeService.save(empDB);
			responseEntity = new ResponseEntity<>("State Succcesfully Updated", HttpStatus.OK);
		} else if (newState.equalsIgnoreCase(STATE.ACTIVE.toString())) {
			empDB.setState(STATE.ACTIVE.toString());
			employeeService.save(empDB);
			responseEntity = new ResponseEntity<>("State Succcesfully Updated", HttpStatus.OK);
		}
		if(responseEntity.getStatusCode().equals(HttpStatus.OK)) {
			return responseEntity;
		}
		String newSubstate=employee.getSubstate();
		if((null==newSubstate)|| newSubstate.isEmpty() || !currState.equalsIgnoreCase(STATE.INCHECK.toString()))
		{
			responseEntity = new ResponseEntity<>("Invalid Substate Transition", HttpStatus.BAD_REQUEST);
		} else if(currState.equalsIgnoreCase(STATE.INCHECK.toString())) {
			if(newSubstate.equalsIgnoreCase(SUBSTATE.SECURITY_CHECK_FINISHED.toString())) {
				empDB.setSecurityCheck(true);
				if(empDB.isWorkPermitCheck()) empDB.setState(STATE.APPROVED.toString());
				employeeService.save(empDB);
				responseEntity = new ResponseEntity<>("Substate Succcesfully Updated", HttpStatus.OK);
			}else if(newSubstate.equalsIgnoreCase(SUBSTATE.WORK_PERMIT_CHECK_FINISHED.toString())) {
				empDB.setWorkPermitCheck(true);
				if(empDB.isSecurityCheck()) empDB.setState(STATE.APPROVED.toString());
				employeeService.save(empDB);
				responseEntity = new ResponseEntity<>("Substate Succcesfully Updated", HttpStatus.OK);
			}else {
				responseEntity = new ResponseEntity<>("Invalid Substate Transition", HttpStatus.BAD_REQUEST);
			}
		}
		return responseEntity;
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<Object> getEmployee(@PathVariable Long id) {
		return new ResponseEntity<>(employeeService.findById(id), HttpStatus.OK);
	}	
}
