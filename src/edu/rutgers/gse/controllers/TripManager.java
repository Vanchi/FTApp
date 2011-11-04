package edu.rutgers.gse.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;

import edu.rutgers.gse.models.*;

public class TripManager {
	
	
	private static HashMap<Long, Trip> trips;
	public static final String CacheDirStr = Environment.getExternalStorageDirectory().getPath() 
													+ "/Android/data/edu.rutgers.gse.ftapp/cache/";
	private TripManager(){
		
	}
	private static TripManager instance;
	public static final synchronized TripManager getInstance(){
		
		if(instance == null){
			instance = new TripManager();
			trips = new HashMap<Long, Trip>();
		}
		return instance;
	}
	
	private Trip curTrip;
	
	public synchronized boolean addTrip(DBAdapter dba, Trip t) throws IOException{
		if(t == null || t.getName() == null || t.getName().equals("") || t.getUserId() == -1){
			return false;
		}
		
		
		dba.saveTrip(t);
		trips.put(t.getId(),new Trip(t));
		return true;
	}
	
	public synchronized boolean updateTrip(Trip t){
		Trip newTrip = trips.get(t.getId());
		if(newTrip == null){
			return false;
		}
		newTrip.update(t);
		trips.put(newTrip.getId(),newTrip);
		//XXX save this trip to DB
		return true;
	}
	
	public synchronized void deleteTrip(Trip t){
		
	}
	
	public synchronized void loadTrips(DBAdapter dba, User u) throws IOException{
		dba.fetchTrips(u, trips);
	}
	
	public Trip getCurTrip(Context cxt, DBAdapter dba) throws IOException {
		if(curTrip == null){
			AccountManager accountMgr = AccountManager.getInstance();
			
			User curUser = accountMgr.getCurrentUser(cxt, dba);
			SharedPreferences settings = cxt.getSharedPreferences(AppPreferences.PREF, 0);
	        long id  = settings.getLong(AppPreferences.KEY_TRIPID + "/" + curUser.getId(), -1);
	        
	        curTrip = dba.fetchTrip(id);
	        if(curTrip == null)
	        	return null;
		}
		return new Trip(curTrip);
	}
	
	public void setCurTrip(Context cxt, DBAdapter dba, Trip curTrip) throws IOException {
		
		AccountManager accountMgr = AccountManager.getInstance();
		
		User curUser = accountMgr.getCurrentUser(cxt, dba);
				
		Editor settingsEditor = cxt.getSharedPreferences(AppPreferences.PREF,0).edit();
		settingsEditor.putLong(AppPreferences.KEY_TRIPID+"/"+curUser.getId(),curTrip.getId());
		settingsEditor.commit();
		this.curTrip = new Trip(curTrip);
	}
	
	public synchronized void copyTripsTo(ArrayList<Trip> tripList){
		if(tripList == null){
			return;
		}
		tripList.clear();
		for(Trip t:trips.values()){
			tripList.add(new Trip(t));
		}
	}
	
}
