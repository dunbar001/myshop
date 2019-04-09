package com.myshop.search.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.myshop.search.service.ItemSearchService;

@RestController
@RequestMapping("/")
public class ItemSearchController {
	@Reference
	private ItemSearchService service;
	
	@RequestMapping("/search")
	public Map search(@RequestBody Map searchMap){
		Map<String, Object> map = service.search(searchMap);
		System.out.println(map);
		return map;
	}
}
