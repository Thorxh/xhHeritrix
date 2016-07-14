package com.archive.xhentity;

import java.util.ArrayList;
import java.util.List;

/**
 * 记录目录
 * 
 * @author thor
 *
 */
public class WenkuCatalog {

	// 目录级数
	private int level = 0;
	// 目录名称
	private String catalogName;
	// 下级目录
	private List<WenkuCatalog> subCatalogs;

	public void addSubCatalog(String catalogName) {
		if (subCatalogs == null) {
			subCatalogs = new ArrayList<WenkuCatalog>();
		}
		subCatalogs.add(new WenkuCatalog(this.level + 1, catalogName));
	}

	public WenkuCatalog(int level, String catalogName) {
		this.level = level;
		this.catalogName = catalogName;
	}

	public WenkuCatalog() {
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}

	public List<WenkuCatalog> getSubCatalogs() {
		return subCatalogs;
	}

	public void setSubCatalogs(List<WenkuCatalog> subCatalogs) {
		this.subCatalogs = subCatalogs;
	}

	public WenkuCatalog(int level, String catalogName,
			List<WenkuCatalog> subCatalogs) {
		super();
		this.level = level;
		this.catalogName = catalogName;
		this.subCatalogs = subCatalogs;
	}

	@Override
	public String toString() {
		return "WenkuCatalog [level=" + level + ", catalogName=" + catalogName
				+ ", subCatalogs=" + subCatalogs + "]";
	}

}
