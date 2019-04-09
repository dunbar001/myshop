package com.myshop.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.myshop.pojo.TbItem;
import com.myshop.search.service.ItemSearchService;

@Service(timeout=3000)
public class ItemSearchServiceImpl implements ItemSearchService {

	@Autowired
	private SolrTemplate solrTemplate;

/*	public Map<String, Object> search(Map searchMap) {
		Map map = new HashMap<String, Object>();
		Query query = new SimpleQuery("*:*");
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria );
		ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
		System.out.println(page.getContent());
		map.put("rows", page.getContent());
		return map;
	}*/
	
	public Map<String,Object> search(Map searchMap){
		Map map = new HashMap<String, Object>();
		
		map.putAll(searchList(searchMap));
		
		List categoryList = getCategoryList(searchMap);
		//查询产品分类
		map.put("categoryList", categoryList);
		//查询品牌和规格
		if(categoryList!=null && categoryList.size() > 0){
			map.putAll(getSpecAndBrandList(categoryList.get(0)+""));
		}
		
		String category = (String) searchMap.get("category");
		
		if(!"".equals(category)){
			map.putAll(getSpecAndBrandList(category));
		}else{
			if(categoryList!=null && categoryList.size() > 0){
				map.putAll(getSpecAndBrandList(categoryList.get(0)+""));
			}
		}
		
		return map;
	}
	
	private Map searchList(Map searchMap){
		Map map = new HashMap<String, Object>();
		String keywords = (String) searchMap.get("keywords");
		HighlightQuery query = new SimpleHighlightQuery();
		HighlightOptions highlightOptions = new HighlightOptions();
		highlightOptions.addField("item_title");
		highlightOptions.setSimplePrefix("<em style='color:red'>");
		highlightOptions.setSimplePostfix("</em>");
		query.setHighlightOptions(highlightOptions );
		Criteria criteria = new Criteria("item_keywords").is(keywords.replace(" ", ""));
		query.addCriteria(criteria );
		//根据分类
		String category = (String) searchMap.get("category");
		if(!"".equals(category)){
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_category").is(category);
			filterQuery.addCriteria(filterCriteria);
			query.addFilterQuery(filterQuery );
		}
		//根据品牌
		String brand = (String) searchMap.get("brand");
		if(!"".equals(brand)){
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_brand").is(brand);
			filterQuery.addCriteria(filterCriteria);
			query.addFilterQuery(filterQuery );
		}
		//根据规格
		Map<String,String> specMap = (Map) searchMap.get("spec");
		if(specMap!=null){
			for(String key : specMap.keySet()){
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
				filterQuery.addCriteria(filterCriteria);
				query.addFilterQuery(filterQuery );
			}
		}
		//根据价格
		String priceStr = (String) searchMap.get("price");
		if(!"".equals(priceStr)){
			String minPrice = priceStr.split("-")[0];
			String maxPrice = priceStr.split("-")[1];
			FilterQuery filterQuery = new SimpleFilterQuery();
			if(!minPrice.equals("0")){
				Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(minPrice);
				filterQuery.addCriteria(filterCriteria);
				query.addFilterQuery(filterQuery );
			}
			if(!maxPrice.equals("*")){
				Criteria filterCriteria = new Criteria("item_price").lessThanEqual(maxPrice);
				filterQuery.addCriteria(filterCriteria);
				query.addFilterQuery(filterQuery );
			}
		}
		//排序
		if(!"".equals(searchMap.get("sortType"))){
			if(searchMap.get("sortType").equals("desc")){
				Sort sort = new Sort(Direction.DESC,"item_" + searchMap.get("sortField"));
				query.addSort(sort);
			}else{
				Sort sort = new Sort(Direction.ASC,"item_"+ searchMap.get("sortField"));
				query.addSort(sort);
			}
		}
		//分页查询
		Integer pageNo = (Integer) searchMap.get("pageNo");
		Integer pageSize = (Integer) searchMap.get("pageSize");
		if(pageNo==null){
			pageNo=1;
		}
		if(pageSize == null){
			pageSize=15;
		}
		query.setOffset((pageNo-1)*pageSize);
		query.setRows(pageSize);
		
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
		
		for(HighlightEntry<TbItem> h :page.getHighlighted()){
			TbItem item = h.getEntity();
			if(h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0){
				item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
			}
		}
		map.put("rows", page.getContent());
		map.put("totalPages", page.getTotalPages());
		map.put("total", page.getTotalElements());
		return map;
	}
	
	private List getCategoryList(Map searchMap){
		List list = new ArrayList();
		Query query = new SimpleQuery();
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria );
		GroupOptions groupOptions = new GroupOptions();
		groupOptions.addGroupByField("item_category");
		query.setGroupOptions(groupOptions );
		GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
		GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		List<GroupEntry<TbItem>> content = groupEntries.getContent();
		for(GroupEntry entry : content){
			list.add(entry.getGroupValue());
		}
		return list;
	}
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	private Map getSpecAndBrandList(String category){
		Map map = new HashMap<>();
		Long typeTemplateId =new Long(redisTemplate.boundHashOps("itemCat").get(category)+"");
		if(typeTemplateId!=null){
			List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeTemplateId);
			List specList = (List) redisTemplate.boundHashOps("specList").get(typeTemplateId);
			map.put("brandList", brandList);
			map.put("specList", specList);
		}
		return map;
	}
	
	public void importItemList(List<TbItem> itemList){
		solrTemplate.saveBean(itemList);
		solrTemplate.commit();
	}

	@Override
	public void deleteByGoodsIds(Long[] ids) {
		Query query = new SimpleQuery("*:*");
		Criteria criteria = new Criteria("item_goodsid").in(ids);
		query.addCriteria(criteria );
		solrTemplate.delete(query);
		solrTemplate.commit();
	}
}
