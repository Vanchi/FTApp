package edu.rutgers.gse.models;

import java.util.Date;

public class Trip {

	String name;
	String thumbURL;
	Date date;
	String locationName;
	String tripPic;
	//XXX GPS
	long id;
	long userid;
	
	public Trip(String name, String thumbUrl){
		this.name = name;
		this.thumbURL = thumbUrl;
		this.id = -1;
	}

	public Trip(Trip t) {
		if(t.name != null){
			name = new String(t.name);
		}else{
			name = null;
		}
		if(t.thumbURL != null){
			thumbURL= new String(t.thumbURL);
		}else{
			thumbURL = null;
		}
		if(t.tripPic != null){
			tripPic = new String(t.tripPic);
		}
		
		date = new Date();//(Date) t.date.clone();
		if(t.locationName != null){
			locationName = new String(t.locationName);
		}else{
			locationName = null;
		}
		id = t.id;
	}

	public Trip() {
		id = -1;
	}

	public void update(Trip t){
		name = new String(t.name);
		thumbURL= new String(t.thumbURL);
		date = (Date) t.date.clone();
		locationName = new String(t.locationName);
		userid = t.userid;
		id = t.id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getThumbURL() {
		return thumbURL;
	}
	public void setThumbURL(String thumbURL) {
		this.thumbURL = thumbURL;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public long getUserId() {
		return userid;
	}

	public void setUserId(long userid) {
		this.userid = userid;
	}


	public String getTripPic() {
		return tripPic;
	}

	public void setTripPic(String tripPic) {
		this.tripPic = tripPic;
	}
	
}
