package edu.rutgers.gse.models;

public class Reading {
	String attr;
	String value;

	public Reading(Reading r){
		attr = new String(r.attr);
		value = new String(r.value);
	}
	public String getAttr() {
		return attr;
	}
	public void setAttr(String attr) {
		this.attr = attr;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Reading(String a, String v){
		attr = new String(a);
		value = new String(v);
		
	}
	public Reading() {
		// TODO Auto-generated constructor stub
	}

}
