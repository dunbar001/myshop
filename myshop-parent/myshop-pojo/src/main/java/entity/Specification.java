package entity;

import java.io.Serializable;
import java.util.List;

import com.myshop.pojo.TbSpecification;
import com.myshop.pojo.TbSpecificationOption;

public class Specification implements Serializable {
	private TbSpecification specEntity;
	private List<TbSpecificationOption> specOptionList;

	public TbSpecification getSpecEntity() {
		return specEntity;
	}

	public void setSpecEntity(TbSpecification specEntity) {
		this.specEntity = specEntity;
	}

	public List<TbSpecificationOption> getSpecOptionList() {
		return specOptionList;
	}

	public void setSpecOptionList(List<TbSpecificationOption> specOptionList) {
		this.specOptionList = specOptionList;
	}

}
