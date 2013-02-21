package com.online201.pleonast.downloader;

import org.joda.time.DateTime;

public class Comment {
	private String text;
	private DateTime date;
	private String comment;

	public String getComment() {
		return comment;
	}

	public DateTime getDate() {
		return date;
	}

	public String getText() {
		return text;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public void setText(String text) {
		this.text = text;
	}

}
