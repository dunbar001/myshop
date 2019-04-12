package com.myshop.page.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.myshop.page.service.ItemPageService;

@Component
public class ItemPageDeleteListener implements MessageListener {
	@Autowired
	private ItemPageService itemPageService;
	
	@Override
	public void onMessage(Message message) {
		ObjectMessage textMessage=(ObjectMessage) message;
		try {
			Long[] text = (Long[]) textMessage.getObject();
			itemPageService.deleteHtml(text);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
