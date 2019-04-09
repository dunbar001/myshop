package com.myshop.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.myshop.mapper.TbBrandMapper;
import com.myshop.mapper.TbGoodsDescMapper;
import com.myshop.mapper.TbGoodsMapper;
import com.myshop.mapper.TbItemCatMapper;
import com.myshop.mapper.TbItemMapper;
import com.myshop.mapper.TbSellerMapper;
import com.myshop.pojo.TbBrand;
import com.myshop.pojo.TbGoods;
import com.myshop.pojo.TbGoodsDesc;
import com.myshop.pojo.TbGoodsDescExample;
import com.myshop.pojo.TbGoodsExample;
import com.myshop.pojo.TbGoodsExample.Criteria;
import com.myshop.pojo.TbItem;
import com.myshop.pojo.TbItemCat;
import com.myshop.pojo.TbItemExample;
import com.myshop.pojo.TbSeller;
import com.myshop.sellergoods.service.GoodsService;

import entity.Goods;
import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		goods.getGoods().setAuditStatus("0");
		goodsMapper.insert(goods.getGoods());
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		goodsDescMapper.insert(goods.getGoodsDesc());
		
		String title=goods.getGoods().getGoodsName();
		TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		if("1".equals(goods.getGoods().getIsEnableSpec())){
			List<TbItem> itemList = goods.getItemList();
			for(TbItem item:itemList){
				Map<String,Object> map = JSON.parseObject(item.getSpec(),Map.class);
				for(String key : map.keySet()){
					title +=" " + map.get(key);
				}
				item.setTitle(title);
				setItemVaules(goods,item,tbBrand,itemCat,seller);
				itemMapper.insert(item);
			}
		}else{
			TbItem item = new TbItem();
			setItemVaules(goods,item,tbBrand,itemCat,seller);
			item.setPrice(goods.getGoods().getPrice());
			item.setStatus("1");
			item.setIsDefault("1");
			item.setNum(99999);
			item.setSpec("{}");
			itemMapper.insert(item);
		}
	}
	
	private void setItemVaules(Goods goods,TbItem item,TbBrand tbBrand,TbItemCat itemCat,TbSeller seller){
		item.setGoodsId(goods.getGoods().getId());
		item.setSellerId(goods.getGoods().getSellerId());//商家编号
		item.setCategoryid(goods.getGoods().getCategory3Id());//商品分类编号（3级）
		item.setCreateTime(new Date());//创建日期
		item.setUpdateTime(new Date());//修改日期 
		
		item.setBrand(tbBrand.getName());
		item.setCategory(itemCat.getName());
		item.setSeller(seller.getNickName());
		List<Map> itemImageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
		if(itemImageList.size() > 0){
			item.setImage(itemImageList.get(0).get("url") + "");
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbGoods goods){
		goodsMapper.updateByPrimaryKey(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		TbItemExample example = new TbItemExample();
		com.myshop.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> itemList = itemMapper.selectByExample(example);
		
		Goods goods = new Goods();
		goods.setGoods(tbGoods);
		goods.setGoodsDesc(goodsDesc);
		goods.setItemList(itemList);
		
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			/*goodsMapper.deleteByPrimaryKey(id);*/
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(tbGoods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
		}
		criteria.andIsDeleteIsNull();
		PageHelper.startPage(pageNum, pageSize);
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);
		System.out.println("总共查询到的商品数：" + page.getTotal());
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Override
		public void updateStatus(Long[] ids, String status) {
			for(Long id : ids){
				TbGoods goods = goodsMapper.selectByPrimaryKey(id);
				goods.setAuditStatus(status);
				goodsMapper.updateByPrimaryKey(goods);
			}
		}
		
		public List<TbItem> findItemsByGoodsIdsAndStatus(Long[] ids,String status){
			TbItemExample example = new TbItemExample();
			com.myshop.pojo.TbItemExample.Criteria criteria = example.createCriteria();
			criteria.andGoodsIdIn(Arrays.asList(ids));
			criteria.andStatusEqualTo(status);
			return itemMapper.selectByExample(example );
		}
	
}
