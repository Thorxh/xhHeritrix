package com.archive.xhentity;

public class HtmlSaver {

	private String title;

	private String content;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public HtmlSaver(String title, String content) {
		super();
		this.title = title;
		this.content = content;
	}

	@Override
	public String toString() {
		return "HtmlSaver [title=" + title + ", content=" + content + "]";
	}

	public HtmlSaver() {
		super();
	}

}
