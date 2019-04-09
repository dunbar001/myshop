package com.myshop.page.service.impl;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.myshop.mapper.TbGoodsDescMapper;
import com.myshop.mapper.TbGoodsMapper;
import com.myshop.mapper.TbItemMapper;
import com.myshop.page.service.ItemService;
import com.myshop.pojo.TbGoods;
import com.myshop.pojo.TbGoodsDesc;
import com.myshop.pojo.TbGoodsExample;
import com.myshop.pojo.TbItem;
import com.myshop.pojo.TbItemExample;
import com.myshop.pojo.TbItemExample.Criteria;

import freemarker.template.Template;

public class ItemServiceImpl implements ItemService {

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

	@Override
	public boolean genItemHtml(Long goodsId) {
		try {
			Map dataModel = new HashMap<>();
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
			dataModel.put("goods", tbGoods);
			
			TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
			dataModel.put("goodsDesc", goodsDesc);
			
			/*TbItemExample example = new TbItemExample();
			Criteria createCriteria = example.createCriteria();
			createCriteria.andGoodsIdEqualTo(goodsId);
			createCriteria.andStatusEqualTo("1");
			List<TbItem> list = itemMapper.selectByExample(example );*/
			
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
	
}
