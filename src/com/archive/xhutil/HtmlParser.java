package com.archive.xhutil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.archive.xhentity.HtmlSaver;

public class HtmlParser {

	public HtmlSaver getTitleAndContent(String html) throws IOException {
		Document doc = Jsoup.parse(html);
		/**
		 * 过滤指定标签
		 */
		List<String> tags = new ArrayList<String>();
		tags.add("script");
		tags.add("style");
		tags.add("meta");
		tags.add("link");
		removeByTag(doc, tags);
		
		String docStr = doc.toString();
		/**
		 * 去除 HTML 标签，注释，空格，全角空格
		 */
		docStr = docStr.replaceAll("<!--.*-->", "").replaceAll("<[^>]*>", "").replaceAll("[ |　]", "");
		
		/**
		 * 计算连续5行的字符总数，总数大于 180 则认为是正文部分
		 */
		String [] strs = docStr.split("\n");
		int [] wordCount = calPerLineWordCount(docStr);
		int len = wordCount.length;
		int wordNum = 0;
		int threshold = 180;
		List<String> strList = new ArrayList<String>();
		for(int i = 0 ; i < len - 5; i ++) {
			wordNum = wordCount[i + 0] + wordCount[i + 1] + wordCount[i + 2] + wordCount[i + 3] + wordCount[i + 4];
			if(wordNum > threshold) {
				String s = strs[i].trim(); 
				if(s.length() != 0) {
					strList.add(s);
				}
			}
		}
		
		if(strList.size() == 0 ) {
			return null;
		}
		
		/**
		 * 找出父元素
		 */
		Map<Element, Integer> parentMap = new HashMap<Element, Integer>();
		Elements elements = doc.getAllElements();
		for(Element element : elements) {
			String str = element.ownText();
			for(String s : strList) {
				if(str.contains(s)) {
					Element parent = element.parent();
					if(parentMap.containsKey(parent)) {
						parentMap.put(parent, parentMap.get(parent) + 1);
					} else {
						parentMap.put(parent, 1);
					}
				}
			}
		}
		
		/**
		 * 找出出现次数最多的父元素作为正文的父元素
		 */
		int num = 0;
		Element parentElement = null;
		Set<Map.Entry<Element,Integer>> entrySet = parentMap.entrySet();
		for(Map.Entry<Element,Integer> entry : entrySet) {
			if(num < entry.getValue()) {
				num = entry.getValue();
				parentElement = entry.getKey();
			}
		}
		
		if(parentElement == null) {
			return null;
		}
		
		/**
		 * 找出标题
		 */
		Element titleElement =  getTitleElement(parentElement, 10, new String[]{"h1", "h2"});
		/**
		 * 如果上一步找不出标题，则以<title></title>中的文本作为标题
		 */
		if(titleElement == null) {
			Elements titles = doc.getElementsByTag("title");
			if(titles != null) {
				titleElement = titles.get(0);
			}
		}
		
		String title = titleElement == null ? "找不到标题" : titleElement.text();
		String content = getCleanContent(parentElement.toString().replaceAll("<!--.*-->", "").replaceAll("<[^>]*>", "").replaceAll("[ |　]", ""));
		HtmlSaver htmlSaver = new HtmlSaver(title, content);

		return htmlSaver;
	}
	
	/**
	 * 去除内容中多余的换行，空格
	 * 
	 * @param src
	 * @return
	 */
	private String getCleanContent(String src) {
		StringBuffer sb = new StringBuffer();
		
		String [] lines = src.split("\n");
		for(String line : lines) {
			if(null == line || line.length() == 0) {
				continue ;
			}
			sb.append(line.trim() + "\n");
		}
		
		return sb.toString();
	}
	
	/**
	 * 寻找标题，找不到tags[0]，则找tags[1]，找不到tags[1]，则找tags[2]，以此类推
	 * 
	 * @param parentElement
	 * @param counter 向上返回父元素的次数
	 * @param tags
	 * @return
	 */
	private Element getTitleElement(Element element, int counter, String ... tags) {
		Element titleElement = null;
		Element parent = element;
		int tagsLen = tags.length;
		for(int i = 0; i < tagsLen; i ++) {
			parent = element;
			String tag = tags[i];
			while(counter -- > 0) {
				parent = parent.parent();
				if(parent == null) {
					return null;
				}
				Elements subElements = parent.getAllElements();
				for(Element e : subElements) {
					String tagName = e.tagName();
					if(tagName.equalsIgnoreCase(tag)) {
						titleElement = e;
						return titleElement;
					}
				}
			}
		}
		return titleElement;
	}
	
	/**
	 * 根据 tag 删除节点
	 * 
	 * @param doc
	 * @param tags
	 */
	private void removeByTag(Document doc, List<String> tags) {
		for(String tag : tags) {
			for(Element element : doc.getElementsByTag(tag)) {
				element.remove();
			}
		}
	}
	
	/**
	 * 计算每行文字个数
	 * 
	 * @param wordCount
	 * @param docStr
	 */
	private int [] calPerLineWordCount(String docStr) {
		String [] strs = docStr.split("\n");
		int [] wordCount = new int[strs.length];
		for(int i = 0; i < strs.length; i ++) {
			wordCount[i] = strs[i].length();
		}
		return wordCount;
	}
	
}
