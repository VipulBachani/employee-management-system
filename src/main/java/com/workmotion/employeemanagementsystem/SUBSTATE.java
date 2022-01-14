package com.workmotion.employeemanagementsystem;

public enum SUBSTATE {
	SECURITY_CHECK_STARTED("SECURITY_CHECK_STARTED"), SECURITY_CHECK_FINISHED("SECURITY_CHECK_FINISHED"), 
	WORK_PERMIT_CHECK_STARTED("WORK_PERMIT_CHECK_STARTED"), WORK_PERMIT_CHECK_FINISHED("WORK_PERMIT_CHECK_FINISHED");
	
	private final String text;
	
	SUBSTATE(String val) {
		// TODO Auto-generated constructor stub
		text=val;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return text;
	}
}
