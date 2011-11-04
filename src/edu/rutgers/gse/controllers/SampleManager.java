package edu.rutgers.gse.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;

import edu.rutgers.gse.models.AppPreferences;
import edu.rutgers.gse.models.Sample;
import edu.rutgers.gse.models.Trip;



public class SampleManager {
	private static HashMap<Long, Sample> samples;
	public static final String CacheDirStr = Environment.getExternalStorageDirectory().getPath() 
													+ "/Android/data/edu.rutgers.gse.ftapp/cache/";
	private SampleManager(){
		
	}
	private static SampleManager instance;
	public static final synchronized SampleManager getInstance(){
		
		if(instance == null){
			instance = new SampleManager();
			samples = new HashMap<Long, Sample>();
		}
		return instance;
	}
	
//	private Sample curSample;
	
	public synchronized boolean saveSample(DBAdapter dba, Sample s) throws IOException{
		if(s == null || s.getName() == null || s.getName().equals("") || s.getTripId() == -1){
			return false;
		}
		
		dba.saveSample(s);
		samples.put(s.getId(), new Sample(s));
		
		return true;
	}
	
	public synchronized void loadSamples(DBAdapter dba, Trip t) throws IOException{
		dba.fetchSamples(t, samples);
	}
	
	public Sample fetchSample(DBAdapter dba, long id) throws IOException {
        return dba.fetchSample(id);
	}
	
/*	public void setCurSample(Context cxt, DBAdapter dba, Sample curSample) throws IOException {
		
		TripManager tripMgr = TripManager.getInstance();
		
		Trip curTrip = tripMgr.getCurTrip(cxt, dba);
		Editor settingsEditor = cxt.getSharedPreferences(AppPreferences.PREF,0).edit();
		settingsEditor.putLong(AppPreferences.KEY_SAMPLEID+"/"+curTrip.getId(),curSample.getId());
		settingsEditor.commit();
		this.curSample = new Sample(curSample);
	}*/
	
	public synchronized void copySamplesTo(ArrayList<Sample> sampleList){
		if(sampleList == null){
			return;
		}
		sampleList.clear();
		for(Sample s:samples.values()){
			sampleList.add(new Sample(s));
		}
	}
}
