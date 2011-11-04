package edu.rutgers.gse.models;

import android.graphics.drawable.Drawable;

public class PicResource {
	private int id;
	private String path;
	private String webpath;
	Drawable drawable;
	
	public PicResource(int id, String path){
		this.id = id;
		this.path = path;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getWebpath() {
		return webpath;
	}

	public void setWebpath(String webpath) {
		this.webpath = webpath;
	}

	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}
	
	
}
