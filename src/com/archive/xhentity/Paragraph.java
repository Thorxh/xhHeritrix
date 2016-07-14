package com.archive.xhentity;

import java.util.ArrayList;
import java.util.List;

public class Paragraph {

	// 标题
	private String title;
	// 段落内容
	private List<String> content;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getContent() {
		return content;
	}

	public void setContent(List<String> content) {
		this.content = content;
	}

	public Paragraph(String title, List<String> content) {
		super();
		this.title = title;
		this.content = content;
	}

	public Paragraph() {
		super();
	}
	
	public void addContent(String content) {
		if(this.content == null) {
			this.content = new ArrayList<String>();
		}
		this.content.add(content);
	}

	@Override
	public String toString() {
		return "Paragraph [title=" + title + ", content=" + content + "]";
	}
	
}
