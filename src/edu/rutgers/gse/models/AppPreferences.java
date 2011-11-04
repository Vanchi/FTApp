package edu.rutgers.gse.models;

public class AppPreferences {
	boolean externalStorageAvailable;
	boolean externalStorageWritable;
	public final static String PREF = "AppPreferences";
	public final static String KEY_WISEUSER = "KEY_WISEUSER";
	public final static String KEY_TRIPID	= "KEY_TRIPID";
	public static final String KEY_SAMPLEID = "KEY_SAMPLEID";
	public AppPreferences(){
		
	}
	public boolean isExternalStorageAvailable() {
		return externalStorageAvailable;
	}
	public void setExternalStorageAvailable(boolean externalStorageAvailable) {
		this.externalStorageAvailable = externalStorageAvailable;
	}
	public boolean isExternalStorageWritable() {
		return externalStorageWritable;
	}
	public void setExternalStorageWritable(boolean externalStorageWritable) {
		this.externalStorageWritable = externalStorageWritable;
	}
	
}
