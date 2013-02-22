package com.online201.pleonast.downloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;

public class JekyllZipOutput {

	public void writeEntries(List<Entry> entries, OutputStream os) throws IOException {
		ZipOutputStream zos = new ZipOutputStream(os);
		zos.setLevel(9);
		for (Entry e : entries) {
			String fileTitle = e.getTitle().replaceAll("[^A-Za-z0-9 -]", "").replaceAll("\\s+", "-");
			String name = e.getDate().toString("yyyy-MM-dd") + "-"+fileTitle.toLowerCase()+".markdown";
			zos.putNextEntry(new ZipEntry(name));
			List<String> lines = new ArrayList<String>();
			lines.add("---");
			lines.add("layout: post");
			lines.add("title: \"" + e.getTitle()+"\"");
			lines.add("date: "+e.getDate().toString("yyyy-MM-dd HH:mm"));
			lines.add("comments: true");
			lines.add("categories: ");
			for (Comment c: e.getComments()) {
				lines.add("# Comment: " + c.getWho()+" ("+c.getDate().toString("yyyy-MM-dd HH:mm")+")");
				lines.add("# " + c.getComment());
			}
			lines.add("---");
			lines.add("");
			String document = StringUtils.join(lines,"\n") + e.getBody();
			zos.write(document.getBytes("UTF-8"));
			zos.closeEntry();
		}
		zos.finish();
	}
}
/*---
layout: post
title: "These Things are Addictive"
date: 2006-09-21 23:37
comments: true
categories: 
---*/
