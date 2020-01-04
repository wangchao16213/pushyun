package com.common.web;

import java.util.List;


public class Pages {

	int page = 1;// 页号

	int totalNum = -1; // 记录总数

	int perPageNum = 100; // 每页显示记录数

	int allPage = 1; // 总页数

	int cpage = 1; // 当前页

	int spage = 1; // 开始记录数

	String fileName = "";

	boolean useUrlRewrite = false;

	public Pages() {
	}

	public Pages(int page, int totalNum, int perPageNum) {
		this.page = page;
		this.totalNum = totalNum;
		this.perPageNum = perPageNum;
		this.executeCount();
	}

	public int getAllPage() {
		return allPage;
	}

	public int getCpage() {
		return cpage;
	}

	public int getPage() {
		return page;
	}

	public int getPerPageNum() {
		return perPageNum;
	}

	public int getSpage() {
		return spage;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public String getFileName() {

		return fileName;
	}

	public boolean isUseUrlRewrite() {
		return useUrlRewrite;
	}

	public void setAllPage(int allPage) {
		this.allPage = allPage;
	}

	public void setCpage(int cpage) {
		this.cpage = cpage;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setPerPageNum(int perPageNum) {
		this.perPageNum = perPageNum;
	}

	public void setSpage(int spage) {
		this.spage = spage;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public void setFileName(String fileName) {

		this.fileName = fileName;
	}

	public void setUseUrlRewrite(boolean useUrlRewrite) {
		this.useUrlRewrite = useUrlRewrite;
	}
	
	public List getObjects(int page,List list) {
		int pageEndRow=0;
		int pageStartRow=0;
        if (page * perPageNum < totalNum) {// 判断是否为最后一页
            pageEndRow = page * perPageNum;
            pageStartRow = pageEndRow - perPageNum;
        } else {
            pageEndRow = totalNum;
            pageStartRow = perPageNum * (allPage - 1);
        }

        List objects = null;
        if (!list.isEmpty()) {
            objects = list.subList(pageStartRow, pageEndRow);
        }
        //this.description();
        return objects;
    }

	public void executeCount() {
		this.allPage = (int) Math.ceil((this.totalNum + this.perPageNum - 1)
				/ this.perPageNum);
		int intPage = this.page;
		if (intPage > this.allPage) { // pages == 0
			this.cpage = 1;
		} else {
			this.cpage = intPage;
		}
		this.spage = (this.cpage - 1) * this.perPageNum;
	}

}
