package com.myshop.manager.controller;

import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.myshop.pojo.TbGoods;
import com.myshop.pojo.TbItem;
import com.myshop.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination queueSolrDestination;
	@Autowired
	private Destination queueSolrDeleteDestination;
	@Autowired
	private Destination topicPageDestination;
	@Autowired
	private Destination topicPageDeleteDestination;
	
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids,String status){
		try{
			goodsService.updateStatus(ids,status);
			if(status.equals("1")){
				//1.导入solr索引库
				List<TbItem> list = goodsService.findItemsByGoodsIdsAndStatus(ids, status);
				if(list.size()>0){
					String jsonString = JSON.toJSONString(list);
					jmsTemplate.send(queueSolrDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(jsonString);
						}
					});
				}else{
					System.out.println("没有明细数据");
				}
				
				//2.生成静态页面
				for(Long id:ids){
					//itemPageService.genItemHtml(id);
					jmsTemplate.send(topicPageDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(id+"");
						}
					});
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return new Result(false,"更新失败");
		}
		return new Result(true,"更新成功");
	}
	
	@RequestMapping("/delete")
	public Result delete(Long[] ids){
		try{
			goodsService.delete(ids);
			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			
			jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			
		}catch(Exception e){
			e.printStackTrace();
			return new Result(false,"删除失败");
		}
		return new Result(true,"删除成功");
	}
	
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods,int page,int rows){
		return goodsService.findPage(goods, page, rows);
	}
}
