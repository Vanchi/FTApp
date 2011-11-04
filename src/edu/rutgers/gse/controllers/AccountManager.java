package edu.rutgers.gse.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import edu.rutgers.gse.models.AppPreferences;
import edu.rutgers.gse.models.PicResource;
import edu.rutgers.gse.models.User;

public class AccountManager {
	
	private static User curUser; //Current User
	
	private AccountManager(){
	}

	private static AccountManager instance;
	
	public static AccountManager getInstance(){
		if(instance == null){
			instance = new AccountManager();
		}
		return instance;
	}
	
	public boolean loginUser(User u, String wisePasswd){

		String wiseUser = u.getWiseUser();
	
		//Contact server DB and figure out wise Id. Check for name matches etc.
		int retId = authenticate(wiseUser, wisePasswd);
		u.setWiseId(retId);

		//XXX to do: Write proper authenticate and determine if login was success.
		boolean loginSuccess = true;
		
		return loginSuccess;
	}
	
	public void logoutUser(User u){
		curUser = null;
	}
	
	public void setCurrentUser(User u, Context cxt){
		
		curUser = new User(u);
		
		Editor settingsEditor = cxt.getSharedPreferences(AppPreferences.PREF,0).edit();
		settingsEditor.putString(AppPreferences.KEY_WISEUSER, u.getWiseUser());
		settingsEditor.commit();
		
	}

	public User getCurrentUser(Context cxt, DBAdapter dba) throws IOException {
		if(curUser == null){
			SharedPreferences settings = cxt.getSharedPreferences(AppPreferences.PREF, 0);
	        String curWiseUser = settings.getString(AppPreferences.KEY_WISEUSER, null);
	        
	        if(curWiseUser != null){
				curUser = dba.fetchUser(curWiseUser);
	        }
	        if(curUser == null)
	        	return null;
		}
		
		return new User(curUser);
	}
	public User fetchUser(String wiseUser, DBAdapter dba) throws IOException{
		return dba.fetchUser(wiseUser);
	}
	
	public void saveUser(User u, DBAdapter dba) throws IOException{
		dba.saveUser(u);
	}
	
	private int authenticate(String wiseUser, String wisePass){
		//Return wiseID;
		return -1;
	}
	
	
}
