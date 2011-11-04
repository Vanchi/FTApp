package edu.rutgers.gse.models;

import java.util.Date;

public class User {

	private long id;
	private String name;
	private String wiseUser;
	private long wiseId;
	private PicResource picRes;
	
	public User(String name, String wiseUser, PicResource pic){
		this.name = name;
		this.wiseUser = wiseUser;
		this.picRes = pic;
		this.id = -1;
		this.wiseId = -1;
	}

	public User(User curUser) {
		this.id = curUser.id;
		this.name = curUser.name;
		this.wiseUser = curUser.wiseUser;
		this.wiseId = curUser.wiseId;
		this.picRes = curUser.picRes;
	}

	public User() {
		this.id = -1;
		this.wiseId = -1;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWiseUser() {
		return wiseUser;
	}
	public void setWiseUser(String wiseUser) {
		this.wiseUser = wiseUser;
	}
	
	public PicResource getPicRes() {
		return picRes;
	}

	public void setPicRes(PicResource picRes) {
		this.picRes = picRes;
	}

	public long getWiseId() {
		return wiseId;
	}

	public void setWiseId(long wiseId) {
		this.wiseId = wiseId;
	}

	public void write(){
		//XXX write to the DB.
	}
	public void load(){
		//XXX load from DB
	}
}
