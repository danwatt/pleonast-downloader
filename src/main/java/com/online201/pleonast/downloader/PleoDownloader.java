package com.online201.pleonast.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

public class PleoDownloader {

	private static final DateTimeFormatter dateParser = DateTimeFormat.forPattern("MM/dd/yy HH:mmaa");
	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		WebClient client = new WebClient(BrowserVersion.FIREFOX_3_6);
		client.getOptions().setJavaScriptEnabled(false);
		client.getOptions().setCssEnabled(false);
		logIn(args, client);
		
		HtmlPage currentPage = client.getPage("http://pleonast.com/users/"+args[0]);

		List<HtmlElement> pagination = (List<HtmlElement>) currentPage.getByXPath("//div[@class='pagination']/span[@class='current']");
		int numPages = Integer.parseInt(pagination.get(0).getTextContent().trim());
		System.out.println("There are " + numPages +" pages worth of entries to parse");
		
		List<Entry> entries = new ArrayList<Entry>();
		
		
		entries.addAll(parseEntries(currentPage));
		for (int i = 2; i <= numPages; i++) {
			currentPage = client.getPage("http://pleonast.com/users/"+args[0]+"?page="+i);
			entries.addAll(parseEntries(currentPage));
		}
		FileOutputStream fos = new FileOutputStream(new File("/tmp/pleo.zip"));
		new JekyllZipOutput().writeEntries(entries, fos);
		fos.close();
	}

	private static List<Entry> parseEntries(HtmlPage currentPage) {
		currentPage.normalize();
		List<Entry> entries = new ArrayList<Entry>();
		List<HtmlElement> pageEntries = (List<HtmlElement>) currentPage.getByXPath("//div[@class='entry']");
		for (HtmlElement pageEntry : pageEntries) {
			Entry entry = new Entry();
			entry.setTitle(pageEntry.getElementsByTagName("h1").get(0).getTextContent().trim());
			String body = pageEntry.getElementsByAttribute("div", "class", "body").get(0).asXml();
			body = StringUtils.substringAfter(body, ">");
			body = StringUtils.substringBeforeLast(body, "<");
			body = StringUtils.replace(body, "  <br/>\n", "\n");
			body = body.replaceAll("[\n\r]+","\n");
			body = body.trim();
			entry.setBody(body);
			String date = pageEntry.getElementsByAttribute("div", "class", "byline").get(0).getElementsByTagName("span").get(0).getTextContent().trim();
			entry.setDate(dateParser.parseDateTime(date.replace("  ", " ")));
			entry.setComments(parseComments(pageEntry));
			System.out.println(entry.getDate().toString() +" : " + entry.getTitle()+", " + entry.getComments().size() +" comments");
			entries.add(entry);
		}
		return entries;
	}

	private static void logIn(String[] args, WebClient client)
			throws IOException, MalformedURLException {
		HtmlPage page = client.getPage("http://pleonast.com/login");
		HtmlInput username = page.getElementByName("user_session[username]");
		HtmlInput password = page.getElementByName("user_session[password]");
		
		username.setValueAttribute(args[0]);
		password.setValueAttribute(args[1]);
		HtmlSubmitInput loginButton = (HtmlSubmitInput) page.getElementByName("commit");
		loginButton.click();
	}
	
	private static List<Comment> parseComments(HtmlElement pageEntry) {
		List<Comment> comments = new ArrayList<Comment>();
		List<HtmlElement> pageComments = pageEntry.getElementsByAttribute("li", "class", "comment");
		for (HtmlElement pageComment : pageComments) {
			Comment c = new Comment();
			HtmlElement right = pageComment.getElementsByAttribute("div", "class", "right").get(0);
			HtmlElement body = right.getElementsByAttribute("div", "class", "body").get(0);
			c.setComment(cleanupCommentBody(body.asXml()));
			HtmlElement meta = right.getElementsByAttribute("div", "class", "meta").get(0);
			String who = StringUtils.substringAfterLast(meta.getElementsByTagName("a").get(0).getAttribute("href"),"/");
			for (DomNode node : meta.getChildren()) {
				String text = node.getTextContent().trim();
				if (node instanceof DomText && text.startsWith("at ") && text.endsWith("M")) {
					c.setDate(dateParser.parseDateTime(StringUtils.substringAfter(text," ").replace("  ", " ")));
				}
			}
			c.setWho(who);
			comments.add(c);
		}
		return comments;
	}

	private static String cleanupCommentBody(String body) {
		String b = StringUtils.substringAfter(body.replaceAll("<\\/?font.*?>", ""),">");
		b = StringUtils.substringBeforeLast(b, "<");
		return b.trim().replaceAll("[\n\r]", "\n# ");
	}
}
