package com.common.web;

import java.util.List;

import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

public class PaginatedListHelper implements PaginatedList {

    private int fullListSize=0; // 总记录数

    private List list; // 每页列表

    private int objectsPerPage=1;// 每页记录数 page size

    private int pageNumber = 1; // 当前页码

    private String searchId;

    private String sortCriterion;

    private SortOrderEnum sortDirection;
    
    private String fileName = "";

    public int getFullListSize() {
        return fullListSize;
    }

    public void setFullListSize(int fullListSize) {
        this.fullListSize = fullListSize;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public int getObjectsPerPage() {
        return objectsPerPage;
    }

    public void setObjectsPerPage(int objectsPerPage) {
        this.objectsPerPage = objectsPerPage;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public String getSortCriterion() {
        return sortCriterion;
    }

    public void setSortCriterion(String sortCriterion) {
        this.sortCriterion = sortCriterion;
    }

    public SortOrderEnum getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(SortOrderEnum sortDirection) {
        this.sortDirection = sortDirection;
    }

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

   

}
