package com.myshop.search.service.impl;

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.myshop.pojo.TbItem;
import com.myshop.search.service.ItemSearchService;

@Component
public class ItemSearchListener implements MessageListener {

	@Autowired
	private ItemSearchService itemSearchService;
	
	@Override
	public void onMessage(Message message) {
		TextMessage textMessage=(TextMessage) message;
		try {
			List<TbItem> list = JSON.parseArray(textMessage.getText(), TbItem.class);
			for(TbItem item:list){
				Map specMap = JSON.parseObject(item.getSpec());
				item.setSpecMap(specMap);
			}
			itemSearchService.importItemList(list);
			System.out.println("导入索引库成功");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
