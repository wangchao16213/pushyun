/**
 * 
 */
package com.common.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * B-JUI 分页对象
 *
 */
public class Page<T> implements Serializable{
	private static final long serialVersionUID = 1L;
	private List<T> list;				// 分页数据列表
	private int pageCurrent;				// 当前页码
	private int pageSize;				// 每页记录数
	private int totalPage;				// 总页数
	private int total;				//总记录数
	private int totalRow;
	private Map<String, String> extra;
	
	
	public Map<String, String> getExtra() {
		return extra;
	}
	public void setExtra(Map<String, String> extra) {
		this.extra = extra;
	}
	public Page() {
		this(null, 1, 30, 1);
	}
	public Page(List<T> list, int pageCurrent, int total) {
		super();
		this.list = list;
		this.setPageCurrent(pageCurrent);
		this.total = total;
		this.totalRow=total;
	}
	public Page(List<T> list, int pageCurrent, int pageSize, int total) {
		super();
		this.list = list;
		this.setPageCurrent(pageCurrent);
		this.pageSize = pageSize;
		this.total = total;
		this.totalRow=total;
	}
	public Page(List<T> list, int pageCurrent, int pageSize, int totalPage,
			int total) {
		super();
		if(list==null){
			this.list = new ArrayList<T>();
		}else{
			this.list=list;
		}
		this.setPageCurrent(pageCurrent);
		this.pageSize = pageSize;
		this.totalPage = totalPage;
		this.total = total;
		this.totalRow=total;
	}
	public List<T> getList() {
		return list;
	}
	public void setList(List<T> list) {
		this.list = list;
	}
	public int getPageCurrent() {
		return pageCurrent;
	}
	public void setPageCurrent(int pageCurrent) {
		pageCurrent = pageCurrent <= 1?1:pageCurrent;
		this.pageCurrent = pageCurrent;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public int getTotalRow() {
		return totalRow;
	}
	public void setTotalRow(int totalRow) {
		this.totalRow = totalRow;
	}
	
	
	
}
