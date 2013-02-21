package com.online201.pleonast.downloader;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

public class Entry {
	private String title;
	private DateTime date;
	private String body;
	private List<Comment> comments = new ArrayList<Comment>();

	public String getBody() {
		return body;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public DateTime getDate() {
		return date;
	}

	public String getTitle() {
		return title;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
