package com.archive.xhutil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.archive.xhentity.Paragraph;
import com.archive.xhentity.WenkuCatalog;
import com.archive.xhentity.WenkuEntity;


public class BaiduBaikeHtmlParser {

	/**
	 * 解析 HTML 并保存
	 */
	public static WenkuEntity parse(String html, String uri) {
		Document doc = Jsoup.parse(html);
		String wenkeTitle;
		WenkuEntity wenkuEntity = new WenkuEntity();
		wenkuEntity.setUri(uri);
		
		// 获取主要内容
		Elements elements = doc.getElementsByClass("main-content");
		Element mainContent = elements.get(0);
		
		// 获取标题及拼音(在<h1>标签内)
		Elements level1TitleElements = mainContent.getElementsByClass("lemmaWgt-lemmaTitle");
		Element level1Title = level1TitleElements.get(0).getElementsByTag("h1").get(0);
		wenkeTitle = level1Title.text();
		wenkuEntity.setTitle(wenkeTitle);
		Elements pinyins = level1TitleElements.get(0).getElementsByClass("lemma-pinyin");
		if(pinyins != null && "".equals(pinyins)) {
			Element pinyin = pinyins.get(0).getElementsByClass("text").get(0);
			wenkuEntity.setPinyin(pinyin.text());
		}
		
		try {
			// 获取概要
			Element summary = mainContent.getElementsByClass("lemma-summary").get(0);
			Elements summaryPara = summary.getElementsByClass("para");
			for(Element sum : summaryPara) {
				wenkuEntity.addSummary(sum.text());
			}
		} catch (Exception e4) {
			System.out.println("no summary ..");
		}
		
		try {
			// 获取基本信息
			Element basicInfo = mainContent.getElementsByClass("basic-info").get(0);
			Element basicInfoLeft = basicInfo.getElementsByClass("basicInfo-left").get(0);
			Elements basicInfoLeftItems = basicInfoLeft.getElementsByClass("basicInfo-item");
			for(int i = 0; i < basicInfoLeftItems.size(); i = i + 2) {
				wenkuEntity.addBasicInfo(basicInfoLeftItems.get(i).text(), basicInfoLeftItems.get(i + 1).text());
			}
			
			Element basicInfoRight = basicInfo.getElementsByClass("basicInfo-right").get(0);
			Elements basicInfoRightItems = basicInfoRight.getElementsByClass("basicInfo-item");
			for(int i = 0; i < basicInfoRightItems.size(); i = i + 2) {
				wenkuEntity.addBasicInfo(basicInfoRightItems.get(i).text().replace(" ", ""), basicInfoRightItems.get(i + 1).text().replace(" ", ""));
			}
		} catch (Exception e3) {
			System.out.println("no basicInfo ..");
		}
		
		try {
			// 获取目录
			Element catalog = mainContent.getElementsByClass("lemmaWgt-lemmaCatalog").get(0);
			Elements catalogs = catalog.getElementsByClass("catalog-list").get(0).getElementsByTag("ol");
			int index = -1;
			for(Element catalogList : catalogs) {
				Elements li = catalogList.getElementsByTag("li");
				for(Element catalogItem : li) {
					String catalogName = catalogItem.getElementsByClass("text").get(0).text();
					String classValue = catalogItem.attr("class");
					if("level1".equals(classValue)) {
						index ++;
						wenkuEntity.addCatalog(new WenkuCatalog(0, catalogName));
					}
					if("level2".equals(classValue)) {
						wenkuEntity.getCatalog(index).addSubCatalog(catalogName);
					}
				}
			}
		} catch (Exception e2) {
			System.out.println("no catalog ..");
		}
		
		// 获取所有正文
		Elements level2TitleElements = mainContent.getElementsByClass("level-2");
		for(Element level2Title : level2TitleElements) {
			Paragraph paragraph = new Paragraph();
			Element title = level2Title.getElementsByClass("title-text").get(0);
			paragraph.setTitle(title.text().replace(title.getElementsByClass("title-prefix").get(0).text(), ""));
			Element nextEle = level2Title.nextElementSibling();
			String cont = "";
			while("para".equals(nextEle.attr("class")) || 
					nextEle.attr("class").contains("level-3") || 
					nextEle.attr("class").contains("custom_dot") ||
					nextEle.attr("class").contains("table-view") || 
					"anchor-list".equals(nextEle.attr("class"))) {
				
				if(nextEle.attr("class").contains("level-3")) {
					cont = nextEle.text().replaceFirst(wenkeTitle, "");
				} else {
					cont = nextEle.text();
				}
				
				if(!"".equals(cont)) {
					paragraph.addContent(cont);
				}
				
				nextEle = nextEle.nextElementSibling();
			}
			wenkuEntity.addParagraph(paragraph);
		}
		
		try {
			// 获取参考资料
			Elements referenceList = mainContent.getElementsByClass("lemma-reference").get(0)
					.getElementsByClass("reference-list").get(0)
					.getElementsByClass("reference-item");
			for(Element referenceItem : referenceList) {
				wenkuEntity.addWenkuReference(referenceItem.text());
			}
		} catch (Exception e) {
			System.out.println("no reference ..");
		}
		
		try {
			// 获取词条标签
			Element openTag = mainContent.getElementById("open-tag-item");
			Elements tagList = openTag.getElementsByClass("taglist");
			for(Element tag : tagList) {
				wenkuEntity.addOpenTag(tag.text().trim());
			}
		} catch (Exception e1) {
			System.out.println("no openTag ..");
		}
		
		return wenkuEntity;
	}
	
}
