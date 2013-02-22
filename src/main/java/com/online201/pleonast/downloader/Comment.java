package com.online201.pleonast.downloader;

import org.joda.time.DateTime;

public class Comment {
	private String who;
	private DateTime date;
	private String comment;

	public String getComment() {
		return comment;
	}

	public DateTime getDate() {
		return date;
	}

	public String getWho() {
		return who;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public void setWho(String text) {
		this.who = text;
	}

}
