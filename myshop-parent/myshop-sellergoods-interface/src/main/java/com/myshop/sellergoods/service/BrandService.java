package com.myshop.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.myshop.pojo.TbBrand;

import entity.PageResult;
import entity.Result;

public interface BrandService {
	/**
	 * 返回全部列表
	 * 
	 * @return
	 */
	public List<TbBrand> findAll();

	/**
	 * 返回分页数据
	 * 
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PageResult findPage(int page, int pageSize);

	/**
	 * 根据id查找品牌
	 * 
	 * @param id
	 * @return
	 */
	public TbBrand findOne(Long id);

	/**
	 * 保存或者修改品牌
	 * 
	 * @param entity
	 */
	public void save(TbBrand entity);

	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 加条件的分页
	 * @param entity
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PageResult findPage(TbBrand entity, int page, int pageSize);

	public List<Map> findBrandSelectDatas();
}
