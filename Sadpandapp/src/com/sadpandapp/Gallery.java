package com.sadpandapp;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Gallery implements Parcelable{
	
	private String gid;				// Gallery id (part of the URL)
	private String name;			// Gallery name
	private String thumb;			// URL to this gallery's thumbnail
	private String token;			// Part of a gallery URL
	private String category;		// Gallery category
	private List<String> tags;		// Tags
	private int fileNumber;			// Number of files in gallery
	private double rating;			// Gallery rate
	
	
	public Gallery(String gid,String name, String thumb, String token, String category, int fileNumber, double rating, List<String> tags) {
		super();
		
		this.gid = gid;
		this.name = name;
		this.thumb = thumb;
		this.token = token;
		this.category = category;
		this.fileNumber = fileNumber;
		this.rating = rating;
		this.tags = tags;
	}
	
	public Gallery(Parcel in){
		this.tags = new ArrayList<String>();
		readFromParcel(in);
		

	}
	
	public String getGid() {
		return gid;
	}
	public void setGid(String gid) {
		this.gid = gid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getFileNumber() {
		return fileNumber;
	}
	public void setFileNumber(int fileNumber) {
		this.fileNumber = fileNumber;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	

    public static final Parcelable.Creator<Gallery> CREATOR
            = new Parcelable.Creator<Gallery>() {
        public Gallery createFromParcel(Parcel in) {
            return new Gallery(in);
        }

        public Gallery[] newArray(int size) {
            return new Gallery[size];
        }
    };
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(gid);
		dest.writeString(name);
		dest.writeString(thumb);
		dest.writeString(category);
		dest.writeStringList(tags);
		dest.writeString(token);
		dest.writeInt(fileNumber);
		dest.writeDouble(rating);
	}
	
	private void readFromParcel(Parcel in){
		this.gid = in.readString();
		this.name = in.readString();
		this.thumb = in.readString();
		this.category = in.readString();
		in.readStringList(tags);
		this.token = in.readString();
		this.fileNumber = in.readInt();
		this.rating = in.readDouble();
	}
	
}
