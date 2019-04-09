package com.myshop.manager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.myshop.pojo.TbGoods;
import com.myshop.pojo.TbItem;
import com.myshop.search.service.ItemSearchService;
import com.myshop.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	@Reference
	private ItemSearchService itemSearchService;
	
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids,String status){
		try{
			goodsService.updateStatus(ids,status);
			if(status.equals("1")){
				List<TbItem> list = goodsService.findItemsByGoodsIdsAndStatus(ids, status);
				if(list.size()>0){
					itemSearchService.importItemList(list);
				}else{
					System.out.println("没有明细数据");
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
			itemSearchService.deleteByGoodsIds(ids);
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
