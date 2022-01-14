package com.workmotion.employeemanagementsystem;

public enum STATE {
	ADDED("ADDED"), INCHECK("IN_CHECK"), APPROVED("APPROVED"), ACTIVE("ACTIVE");
	private final String text;
	STATE(String val){
		text=val;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return text;
	}
}
