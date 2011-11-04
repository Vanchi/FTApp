package edu.rutgers.gse.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import edu.rutgers.gse.models.Reading;
import edu.rutgers.gse.models.Sample;
import edu.rutgers.gse.models.Trip;
import edu.rutgers.gse.models.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.DateFormat;
import java.text.ParseException;

public class DBAdapter {
	SQLiteDatabase db;
	FTDBHelper ftdbHelper;
	
	public DBAdapter (){
	}
	
	
	
	public void open(Context cxt){
		ftdbHelper = new FTDBHelper(cxt);
		db = ftdbHelper.getWritableDatabase(); 
	}
	
	public void close(){
		if(db != null && db.isOpen()){
			db.close();
		}
		if(ftdbHelper != null)
			ftdbHelper.close();
		ftdbHelper = null;
		db = null;
	}
	
	public User fetchUser(String wiseUser) throws IOException{
		User u = null;
		if(db == null){
			throw new IOException("DB not opened");
		}
		Cursor c = db.query(FTDBHelper.USER_TABLE, new String[] {"id","name","wiseuser","wiseid","pic"}, "wiseuser = ?", new String[] { wiseUser},  null,null,null);
		if(c.moveToFirst() == true){
			u = new User();
			u.setId(c.getLong(0));
			u.setName(c.getString(1));
			u.setWiseUser(c.getString(2));
			u.setWiseId(c.getLong(3));
			u.setPicRes(null);
		}
		c.close();
		return u;
	}
	public void saveUser(User u) throws IOException {
		if(db == null){
			throw new IOException("DB not opened");
		}
		
		Cursor c = db.query(FTDBHelper.USER_TABLE, new String[] {"id","name","wiseuser","wiseid","pic"}, "wiseuser = ?", new String[] {u.getWiseUser()}, null,null,null);
		
		ContentValues cv = new ContentValues();
		cv.put("name", u.getName());
		cv.put("wiseuser", u.getWiseUser());
		cv.put("wiseid", u.getWiseId());
		
		if(c.moveToFirst() == true){
			db.update(FTDBHelper.USER_TABLE, cv, "id = "+u.getId(), null);
		}else{
			long id = db.insert(FTDBHelper.USER_TABLE, null, cv);
			if(id != -1){
				u.setId(id);
			}
		}
		c.close();
	}
	
	public void fetchTrips(User u, HashMap<Long, Trip> trips) throws IOException{
		trips.clear();
		
		if(db == null){
			throw new IOException("DB not opened");
		}
		
		Cursor c = db.query(FTDBHelper.TRIPS_TABLE, new String[] {"id","userid","name","location", " date", "pic"}, "userid = ?", new String[] {Long.toString(u.getId())}, null,null,null);
		//Cursor c = db.query(FTDBHelper.TRIPS_TABLE, new String[] {"id","userid","name","location", " date", "pic"}, null,null, null,null,null);
		if(c.moveToFirst()){
			do{
				Trip t = new Trip();
				t.setId(c.getLong(0));
				t.setUserId(c.getLong(1));
				t.setName(c.getString(2));
				t.setLocationName(c.getString(3));
				try {
					String dateStr = c.getString(4);
					
					if(dateStr != null){
						DateFormat df = DateFormat.getDateTimeInstance();
						df.setTimeZone(TimeZone.getTimeZone("UTC"));
						Date d = df.parse(dateStr);
						t.setDate(d);
					}
				} catch (ParseException e) {
					t.setDate(new Date());
				}
				t.setTripPic(c.getString(5));
				
				trips.put(t.getId(), t);
			}while(c.moveToNext());
		}
		c.close();
	}
	
	public Trip fetchTrip(long id) throws IOException{
		Trip t = null;
		
		if(db == null){
			throw new IOException("DB not opened");
		}
		
		Cursor c = db.query(FTDBHelper.TRIPS_TABLE, new String[] {"id","userid","name","location", " date", "pic"}, "id = ?", new String[] {Long.toString(id)}, null,null,null);
		
		if(c.moveToFirst()){
				t = new Trip();
				t.setId(c.getLong(0));
				t.setUserId(c.getLong(1));
				t.setName(c.getString(2));
				t.setLocationName(c.getString(3));
				try {
					String dateStr = c.getString(4);
					
					if(dateStr != null){
						DateFormat df = DateFormat.getDateTimeInstance();
						df.setTimeZone(TimeZone.getTimeZone("UTC"));
						Date d = df.parse(dateStr);
						t.setDate(d);
					}
				} catch (ParseException e) {
					t.setDate(new Date());
				}
				t.setTripPic(c.getString(5));
		}
		c.close();
		return t;
	}
	
	public void saveTrip(Trip t) throws IOException{
		if(db == null){
			throw new IOException("DB not opened");
		}
		
		Cursor c = db.query(FTDBHelper.TRIPS_TABLE, new String[] {"id","userid","name","location", " date", "pic"}, "id = ?", new String[] {Long.toString(t.getId())}, null,null,null);
		
		ContentValues cv = new ContentValues();
		cv.put("userid", t.getUserId());
		cv.put("name", t.getName());
		cv.put("location", t.getLocationName());
		if(t.getDate() != null)
		{
			DateFormat df = DateFormat.getDateTimeInstance();
			df.setTimeZone(TimeZone.getTimeZone("UTC"));
			String dateStr = df.format(t.getDate());
			cv.put("date", dateStr);
		}
		
		
		if(t.getTripPic()!=null){
			cv.put("pic", t.getTripPic());
		}
		
		if(c.moveToFirst() == true){
			db.update(FTDBHelper.TRIPS_TABLE, cv, "id = "+t.getId(), null);
		}else{
			long id = db.insert(FTDBHelper.TRIPS_TABLE, null, cv);
			if(id != -1){
				t.setId(id);
			}
		}
		c.close();
	}
	
	public void fetchSamples(Trip t, HashMap<Long, Sample> samples) throws IOException{
		samples.clear();
		
		if(db == null){
			throw new IOException("DB not opened");
		}
		
		Cursor c = db.query(FTDBHelper.SAMPLES_TABLE, new String[] {"id","tripid","name","type", "subtype", "location", " date"}, "tripid = ?", new String[] {Long.toString(t.getId())}, null,null,null);
		if(c.moveToFirst()){
			do{
				Sample s = new Sample();
				s.setId(c.getLong(0));
				s.setTripId(c.getLong(1));
				s.setName(c.getString(2));
				s.setType(c.getString(3));
				s.setSubType(c.getString(4));
				s.setLocation(c.getString(5));
				try {
					String dateStr = c.getString(6);
					
					if(dateStr != null){
						DateFormat df = DateFormat.getDateTimeInstance();
						df.setTimeZone(TimeZone.getTimeZone("UTC"));
						Date d = df.parse(dateStr);
						s.setDate(d);
					}
				} catch (ParseException e) {
					s.setDate(new Date());
				}
				
				fetchReadings(s);
				samples.put(s.getId(), s);
				
			}while(c.moveToNext());
		}
		c.close();
	}
	
	public Sample fetchSample(long id) throws IOException{
		Sample s = null;
		
		if(db == null){
			throw new IOException("DB not opened");
		}
		
		Cursor c = db.query(FTDBHelper.SAMPLES_TABLE, new String[] {"id","tripid","name","type", "subtype","location", " date"}, "id = ?", new String[] {Long.toString(id)}, null,null,null);
		
		if(c.moveToFirst()){
			s = new Sample();
			s.setId(c.getLong(0));
			s.setTripId(c.getLong(1));
			s.setName(c.getString(2));
			s.setType(c.getString(3));
			s.setSubType(c.getString(4));
			s.setLocation(c.getString(5));
			try {
				String dateStr = c.getString(6);
				
				if(dateStr != null){
					DateFormat df = DateFormat.getDateTimeInstance();
					df.setTimeZone(TimeZone.getTimeZone("UTC"));
					Date d = df.parse(dateStr);
					s.setDate(d);
				}
			} catch (ParseException e) {
				s.setDate(new Date());
			}
			fetchReadings(s);
		}
		c.close();
		return s;
	}
	
	public void saveSample(Sample s) throws IOException{
		if(db == null){
			throw new IOException("DB not opened");
		}
		
		Cursor c = db.query(FTDBHelper.SAMPLES_TABLE, new String[] {"id","tripid","name","type","subtype","location"," date"}, "id = ?", new String[] {Long.toString(s.getId())}, null,null,null);
		
		ContentValues cv = new ContentValues();
		cv.put("tripid", s.getTripId());
		cv.put("name", s.getName());
		cv.put("type", s.getType());
		cv.put("subtype",s.getSubType());
		cv.put("location", s.getLocation());
		if(s.getDate() != null)
		{
			String dateStr;
			DateFormat df = DateFormat.getDateTimeInstance();
			df.setTimeZone(TimeZone.getTimeZone("UTC"));
			dateStr = df.format(s.getDate());
			cv.put("date", dateStr);
		}
		
		
		if(c.moveToFirst() == true){
			db.update(FTDBHelper.SAMPLES_TABLE, cv, "id = "+s.getId(), null);
		}else{
			long id = db.insert(FTDBHelper.SAMPLES_TABLE, null, cv);
			if(id != -1){
				s.setId(id);
			}
		}
		
		c.close();
		saveReadings(s);
	}
	
	public void fetchReadings(Sample s) throws IOException{
		ArrayList<Reading> readings = new ArrayList<Reading>();
		readings.clear();
		
		if(db == null){
			throw new IOException("DB not opened");
		}
		
		Cursor c = db.query(FTDBHelper.READINGS_TABLE, 
				new String[] {"attr","value"}, 
				"sampleid = ? AND meta = ?", new String[] {Long.toString(s.getId()), "data"}, null,null,null);
		
		if(c.moveToFirst()){
			do{
				Reading r = new Reading();
				r.setAttr(c.getString(0));
				r.setValue(c.getString(1));
				
				readings.add(r);
			}while(c.moveToNext());
		}
		c.close();
		s.saveReadings(readings);
	}
	
	public Reading fetchReading(String attr, Sample s) throws IOException{
		Reading r = null;
		if(db == null){
			throw new IOException("DB not opened");
		}
		
		Cursor c = db.query(FTDBHelper.READINGS_TABLE, 
				new String[] {"attr","value"}, "sampleid = ? AND attr = ? AND meta = ?", 
				new String[] {Long.toString(s.getId()), attr, "data"}, null,null,null);
		
		if(c.moveToFirst()){
			r = new Reading();
			r.setAttr(c.getString(0));
			r.setValue(c.getString(1));
			
		}
		c.close();
		return r;
	}
	
	public void saveReadings(Sample s) throws IOException{
		ArrayList<Reading> readings = new ArrayList<Reading>();

		s.copyReadings(readings);
		
		for(Reading r: readings){
			saveReading(r, s);
		}
	}
	public void fetchSamplePics(Sample s, ArrayList<String> pics) throws IOException{
		pics.clear();
		
		if(db == null){
			throw new IOException("DB not opened");
		}
		
		Cursor c = db.query(FTDBHelper.READINGS_TABLE, 
				new String[] {"value"}, 
				"sampleid = ? AND attr = ? AND meta = ?", 
				new String[] {Long.toString(s.getId()), "url", "pic"}, null,null,null);
		
		if(c.moveToFirst()){
			do{
				String pic = c.getString(0);
				
				pics.add(pic);
				
			}while(c.moveToNext());
		}
		c.close();
	}
	public void saveSamplePic(String pic, Sample s) throws IOException{
		if(db == null){
			throw new IOException("DB not opened");
		}
		
		Cursor c = db.query(FTDBHelper.READINGS_TABLE, 
				new String[] {"attr","value"}, "sampleid = ? AND attr = ? AND value = ? AND meta = ?", 
				new String[] {Long.toString(s.getId()), "url", pic, "pic"}, null,null,null);
		
		ContentValues cv = new ContentValues();
		cv.put("attr", "url");
		cv.put("value", pic);
		cv.put("sampleid", s.getId());
		cv.put("meta", "pic");
		cv.put("idx", 1);
		
		if(c.moveToFirst() == true){
			db.update(FTDBHelper.READINGS_TABLE, cv, "sampleid = ? AND attr = ? AND value = ? AND meta = ?", 
					new String[] {Long.toString(s.getId()), "url", pic, "pic"});
		}else{
			db.insert(FTDBHelper.READINGS_TABLE, null, cv);
		}
		c.close();
	}
	public void saveReading(Reading r, Sample s) throws IOException{
		if(db == null){
			throw new IOException("DB not opened");
		}
		
		Cursor c = db.query(FTDBHelper.READINGS_TABLE, new String[] {"attr","value"}, "sampleid = ? AND attr = ? AND meta = ?", new String[] {Long.toString(s.getId()), r.getAttr(), "data"}, null,null,null);
		
		ContentValues cv = new ContentValues();
		cv.put("attr", r.getAttr());
		cv.put("value", r.getValue());
		cv.put("sampleid", s.getId());
		cv.put("meta", "data");
		cv.put("idx", -1);
		
		if(c.moveToFirst() == true){
			db.update(FTDBHelper.READINGS_TABLE, cv, "sampleid = ? AND attr = ?", new String[] {Long.toString(s.getId()), r.getAttr()});
		}else{
			db.insert(FTDBHelper.READINGS_TABLE, null, cv);
		}
		c.close();
	}
	
	public void loadAttributes(ArrayList<String> attrs) throws IOException{
		if(db == null){
			throw new IOException("DB not opened");
		}
		
		
		//Order by frequency of occurrence, desc.
		//Cursor c = db.query(FTDBHelper.READINGS_TABLE, new String[] {"attr", "COUNT(*) AS freq"}, null, null, null,null,"COUNT(*) DESC", null);
		Cursor c = db.query(true, FTDBHelper.READINGS_TABLE, new String[] {"attr"}, "meta = ?", new String[]{"data"}, null,null,null, null);
		if(c.moveToFirst()){
			do{
				String attr = new String(c.getString(0));
				
				attrs.add(attr);
			}while(c.moveToNext());
		}
		c.close();
	}
	public void loadTypes(ArrayList<String> types) throws IOException{
		if(db == null){
			throw new IOException("DB not opened");
		}
		
		types.clear();
		//Order by frequency of occurrence, desc.
		//Cursor c = db.query(FTDBHelper.READINGS_TABLE, new String[] {"attr", "COUNT(*) AS freq"}, null, null, null,null,"COUNT(*) DESC", null);
		Cursor c = db.query(true, FTDBHelper.SAMPLES_TABLE, new String[] {"type"}, null, null, null,null,null, null);
		if(c.moveToFirst()){
			do{
				String type = new String(c.getString(0));
				
				types.add(type);
			}while(c.moveToNext());
		}
		c.close();
	}
	public void loadSubTypes(ArrayList<String> subtypes, String type) throws IOException{
		if(db == null){
			throw new IOException("DB not opened");
		}
		
		subtypes.clear();
		//Order by frequency of occurrence, desc.
		//Cursor c = db.query(FTDBHelper.READINGS_TABLE, new String[] {"attr", "COUNT(*) AS freq"}, null, null, null,null,"COUNT(*) DESC", null);
		Cursor c = db.query(true, FTDBHelper.SAMPLES_TABLE, new String[] {"subtype"}, "type = ?", new String[]{type}, null,null,null, null);
		if(c.moveToFirst()){
			do{
				String subtype = new String(c.getString(0));
				
				subtypes.add(subtype);
			}while(c.moveToNext());
		}
		c.close();
	}
	public long getNewPicId() throws IOException{
		if(db == null){
			throw new IOException("DB not opened");
		}
		long id;
		ContentValues cv = new ContentValues();
		cv.put("dummy", "dummy");
		id = db.insert("pics", null, cv);
		return id;
		
	}
	private class FTDBHelper extends SQLiteOpenHelper {
		
		private static final int DATABASE_VERSION = 11;
		
		private static final String DATABASE_NAME = "ftdb";
		private static final String USER_TABLE = "users";
		private static final String TRIPS_TABLE = "trips";
		private static final String SAMPLES_TABLE = "samples";
		private static final String READINGS_TABLE = "readings";
		
		
		private static final String USER_TABLE_CREATE = "create table users (id integer primary key autoincrement, "
			+ "name text not null, wiseuser text not null, wiseid integer, pic text);";
		private static final String TRIPS_TABLE_CREATE = "create table trips (id integer primary key autoincrement, " 
				+ "userid integer not null, " 
				+ "name text not null, location text not null, date text, pic text);";
		
		private static final String SAMPLES_TABLE_CREATE =  "create table samples (id integer primary key autoincrement, " +
					"tripid integer not null, " +
					"name text not null, type text not null, subtype text not null, location text, date text);" ;
					
		private static final String READINGS_TABLE_CREATE ="create table readings (id integer primary key autoincrement, " +
					"sampleid integer not null,attr text not null, value text not null, meta text, idx integer);";//, "+ 
					//"type text, date text, location text, meta text, idx integer);";
		private static final String PICS_TABLE_CREATE = "create table pics (id integer primary key autoincrement, dummy text);";
		

		

		public FTDBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(USER_TABLE_CREATE);
			db.execSQL(TRIPS_TABLE_CREATE);
			db.execSQL(SAMPLES_TABLE_CREATE);
			db.execSQL(READINGS_TABLE_CREATE);
			db.execSQL(PICS_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS users");
			db.execSQL("DROP TABLE IF EXISTS trips");
			db.execSQL("DROP TABLE IF EXISTS samples");
			db.execSQL("DROP TABLE IF EXISTS readings");
			db.execSQL("DROP TABLE IF EXISTS pics");
			db.execSQL(USER_TABLE_CREATE);
			db.execSQL(TRIPS_TABLE_CREATE);
			db.execSQL(SAMPLES_TABLE_CREATE);
			db.execSQL(READINGS_TABLE_CREATE);
			db.execSQL(PICS_TABLE_CREATE);
		}

	}
}
