package com.archive.xhentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WenkuEntity {

	private String uri;
	// 标题
	private String title;
	// 拼音
	private String pinyin;
	// 概要
	private List<String> summaryList;
	// 基本信息
	private Map<String, String> basicInfo;
	// 目录
	private List<WenkuCatalog> catalog;
	// 正文段落
	private List<Paragraph> paragraphList;
	// 参考资料
	private List<String> wenkuReference;
	// 词条标签
	private List<String> wenkuOpenTag;

	public WenkuCatalog getCatalog(int index) {
		if (catalog == null) {
			return null;
		}
		return catalog.get(index);
	}

	public void addCatalog(WenkuCatalog wenkuCatalog) {
		if (catalog == null) {
			catalog = new ArrayList<WenkuCatalog>();
		}
		catalog.add(wenkuCatalog);
	}

	public void addOpenTag(String tag) {
		if (wenkuOpenTag == null) {
			wenkuOpenTag = new ArrayList<String>();
		}
		wenkuOpenTag.add(tag);
	}

	public void addWenkuReference(String reference) {
		if (wenkuReference == null) {
			wenkuReference = new ArrayList<String>();
		}
		wenkuReference.add(reference);
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public List<String> getSummaryList() {
		return summaryList;
	}

	public void setSummaryList(List<String> summaryList) {
		this.summaryList = summaryList;
	}

	public Map<String, String> getBasicInfo() {
		return basicInfo;
	}

	public void setBasicInfo(Map<String, String> basicInfo) {
		this.basicInfo = basicInfo;
	}

	public List<WenkuCatalog> getCatalog() {
		if (catalog == null) {
			catalog = new ArrayList<WenkuCatalog>();
		}
		return catalog;
	}

	public void setCatalog(List<WenkuCatalog> catalog) {
		this.catalog = catalog;
	}

	public List<Paragraph> getParagraphList() {
		return paragraphList;
	}

	public void setParagraphList(List<Paragraph> paragraphList) {
		this.paragraphList = paragraphList;
	}

	public List<String> getWenkuReference() {
		return wenkuReference;
	}

	public void setWenkuReference(List<String> wenkuReference) {
		this.wenkuReference = wenkuReference;
	}

	public List<String> getWenkuOpenTag() {
		return wenkuOpenTag;
	}

	public void setWenkuOpenTag(List<String> wenkuOpenTag) {
		this.wenkuOpenTag = wenkuOpenTag;
	}

	public WenkuEntity(String title, String pinyin, List<String> summaryList,
			Map<String, String> basicInfo, List<WenkuCatalog> catalog,
			List<Paragraph> paragraphList, List<String> wenkuReference,
			List<String> wenkuOpenTag) {
		super();
		this.title = title;
		this.pinyin = pinyin;
		this.summaryList = summaryList;
		this.basicInfo = basicInfo;
		this.catalog = catalog;
		this.paragraphList = paragraphList;
		this.wenkuReference = wenkuReference;
		this.wenkuOpenTag = wenkuOpenTag;
	}

	public WenkuEntity() {
		super();
	}

	public void addParagraph(Paragraph paragraph) {
		if (paragraphList == null) {
			paragraphList = new ArrayList<Paragraph>();
		}
		paragraphList.add(paragraph);
	}

	public void addSummary(String summary) {
		if (summaryList == null) {
			summaryList = new ArrayList<String>();
		}
		summaryList.add(summary);
	}

	public void addBasicInfo(String key, String value) {
		if (basicInfo == null) {
			basicInfo = new HashMap<String, String>();
		}
		basicInfo.put(key, value);
	}

	@Override
	public String toString() {
		return "WenkuEntity [\n uri=" + uri
				+ ",\n title=" + title
				+ ",\n pinyin=" + pinyin 
				+ ",\n summaryList=" + summaryList
				+ ",\n basicInfo=" + basicInfo 
				+ ",\n catalog=" + catalog
				+ ",\n paragraphList=" + paragraphList 
				+ ",\n wenkuReference=" + wenkuReference 
				+ ",\n wenkuOpenTag=" + wenkuOpenTag + "]";
	}

}
