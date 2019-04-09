package entity;

import java.io.Serializable;
import java.util.List;

import com.myshop.pojo.TbBrand;

public class PageResult implements Serializable {
	private Long total;
	private List rows;
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	public List<TbBrand> getRows() {
		return rows;
	}
	public void setRows(List<TbBrand> rows) {
		this.rows = rows;
	}
	public PageResult(Long total, List rows) {
		super();
		this.total = total;
		this.rows = rows;
	}
}
