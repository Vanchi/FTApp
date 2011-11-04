package edu.rutgers.gse.controllers;

import edu.rutgers.gse.models.AppPreferences;

public class PreferencesManager {
	
	private PreferencesManager(){
		
	}
	private static PreferencesManager instance;
	public static PreferencesManager getInstance(){
		if(instance == null){
			instance = new PreferencesManager();
			p = new AppPreferences();
		}
		return instance;
	}
	
	static AppPreferences p;
	
}
