package edu.rutgers.gse.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Sample {
	Map<String, Reading> readings;
	
	String name;
	long id;
	long tripid;
	String type;
	String subtype;
	Date date;
	String location;
	
	public Sample(){
		id = -1;
		tripid = -1;
		readings = new HashMap<String, Reading>();
		
	}
	
	public Sample(String name){
		this.name = new String(name);
		this.id = -1;
		this.tripid = -1;
		readings = new HashMap<String, Reading>();
	}
	
	public Sample(Sample s) {
		id = s.id;
		tripid = s.tripid;
		
		readings = new HashMap<String, Reading>();
		for(Reading r: s.readings.values()){
			readings.put(r.getAttr(), new Reading(r));
		}
		
		if(s.name != null){
			name = new String(s.name);
		}else
			name = null;
		if(s.type != null){
			type = new String(s.type);
		}else
			type = null;
		if(s.subtype != null){
			subtype = new String(s.subtype);
		}else{
			subtype = null;
		}
		if(s.date != null){
			date = (Date)s.date.clone();
		}else
			date = null;
		if(s.location != null){
			location = new String(s.location);
		}else
			location = null;
	}
	
	public void copy(Sample s){
		id = s.id;
		tripid = s.tripid;
		//XXX
		readings.clear();
		
		for(Reading r: s.readings.values()){
			readings.put(r.getAttr(), new Reading(r));
		}
		
		if(s.name != null){
			name = new String(s.name);
		}else
			name = null;
		if(s.type != null){
			type = new String(s.type);
		}else
			type = null;
		if(s.subtype != null){
			subtype = new String(s.subtype);
		}else{
			subtype = null;
		}
		if(s.date != null){
			date = (Date)s.date.clone();
		}else
			date = null;
		if(s.location != null){
			location = new String(s.location);
		}else
			location = null;
	}
	
	public void copyReadings(ArrayList<Reading> r){
		r.clear();
		for(Reading reading: readings.values()){
			r.add(new Reading(reading));
		}
	}
	public void saveReadings(ArrayList<Reading> rlist){
		readings.clear();
		for(Reading r: rlist){
			readings.put(r.getAttr(), new Reading(r));
		}
	}
	
	public void addAttr(String attr, String value){
		readings.put(attr, new Reading(attr, value));
	}
	
	public Reading getAttr(String attr){
		return readings.get(attr);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = new String(name);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTripId() {
		return tripid;
	}

	public void setTripId(long tripid) {
		this.tripid = tripid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = new String(type);
	}

	public String getSubType() {
		return subtype;
	}

	public void setSubType(String subtype) {
		this.subtype = new String(subtype);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = (Date)date.clone();
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	
}
