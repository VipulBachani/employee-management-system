package com.workmotion.employeemanagementsystem;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;


@Entity
public class Employee {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "EMPLOYEE_NAME")
    private String name;
    
    @Column(name="STATE")
    private String state;
    
    @Column(name="SECURITY_CHECK_FINISHED")
    private boolean securityCheck;
    
    @Column(name="WORK_PERMIT_CHECK_FINISHED")
    private boolean workPermitCheck;
    
    @Transient
    private String substate;
    

	public String getSubstate() {
		return substate;
	}

	public void setSubstate(String substate) {
		this.substate = substate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public boolean isSecurityCheck() {
		return securityCheck;
	}

	public void setSecurityCheck(boolean securityCheck) {
		this.securityCheck = securityCheck;
	}

	public boolean isWorkPermitCheck() {
		return workPermitCheck;
	}

	public void setWorkPermitCheck(boolean workPermitCheck) {
		this.workPermitCheck = workPermitCheck;
	}

	
	

}
