package com.myshop.page.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.myshop.mapper.TbGoodsDescMapper;
import com.myshop.mapper.TbGoodsMapper;
import com.myshop.mapper.TbItemCatMapper;
import com.myshop.mapper.TbItemMapper;
import com.myshop.page.service.ItemPageService;
import com.myshop.pojo.TbGoods;
import com.myshop.pojo.TbGoodsDesc;
import com.myshop.pojo.TbGoodsExample;
import com.myshop.pojo.TbItem;
import com.myshop.pojo.TbItemCat;
import com.myshop.pojo.TbItemExample;
import com.myshop.pojo.TbItemExample.Criteria;

import freemarker.template.Template;

@Service
public class ItemPageServiceImpl implements ItemPageService {

	@Value("${pagedir}")
	private String pageDir;
	
	@Autowired
	private FreeMarkerConfig freeMarkerConfig;
	
	@Autowired
	private TbGoodsMapper goodsMapper;
	
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Override
	public boolean genItemHtml(Long goodsId) {
		try {
			Map dataModel = new HashMap<>();
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
			dataModel.put("goods", tbGoods);
			
			TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
			dataModel.put("goodsDesc", goodsDesc);
			
			TbItemExample example = new TbItemExample();
			Criteria createCriteria = example.createCriteria();
			createCriteria.andGoodsIdEqualTo(goodsId);
			createCriteria.andStatusEqualTo("1");
			example.setOrderByClause("is_default desc");
			List<TbItem> itemList = itemMapper.selectByExample(example );
			dataModel.put("itemList", itemList);
			
			String itemCat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
			String itemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
			String itemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
			dataModel.put("itemCat1", itemCat1);
			dataModel.put("itemCat2", itemCat2);
			dataModel.put("itemCat3", itemCat3);
			
			Template template = freeMarkerConfig.getConfiguration().getTemplate("item.ftl");
			Writer out = new FileWriter(pageDir+goodsId+".html");
			template.process(dataModel, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void deleteHtml(Long[] ids){
		for(Long id : ids){
			File file = new File(pageDir + id + ".html");
			if(file.exists()){
				file.delete();
			}
		}
	}
	
}
