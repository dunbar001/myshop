package com.myshop.search.service;

import java.util.List;
import java.util.Map;

import com.myshop.pojo.TbItem;

public interface ItemSearchService {
	/**
	 * 查询商品sku列表
	 * @param searchMap
	 * @return
	 */
	public Map<String, Object> search(Map searchMap);
	
	/**
	 * 导入商品sku数据
	 * @param itemList
	 */
	public void importItemList(List<TbItem> itemList);
	
	/**
	 * 批量删除索引库
	 * @param ids
	 */
	public void deleteByGoodsIds(Long[] ids);
}
