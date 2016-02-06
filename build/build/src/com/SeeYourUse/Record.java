package com.SeeYourUse;


public class Record {
	private String content;
	private String date;
	private int points;
	private int id;


	public Record() {}
	
	public Record(String content, String date, int points, int id) 
	{
		this.content = content;
		this.date = date;
		this.points = points;
		this.id = id;
	}
	
	public Record(String content, String date, int points) 
	{
		this.content = content;
		this.date = date;
		this.points = points;
	}
	
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
	
	public String getTimestamp() {
		return  date + " < " + Integer.toString(points) + " Points >";
	}
	
	
}
