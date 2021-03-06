package com.myshop.shop.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.myshop.pojo.TbBrand;
import com.myshop.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/brand")
public class BrandController {
	@Reference
	private BrandService brandService;

	@RequestMapping("/findAll")
	public List<TbBrand> findAll() {
		return brandService.findAll();
	}

	@RequestMapping("/findPage")
	public PageResult findPage(int page, int pageSize) {
		return brandService.findPage(page, pageSize);
	}

	@RequestMapping("/findOne")
	public TbBrand findOne(Long id) {
		return brandService.findOne(id);
	}

	@RequestMapping("/save")
	public Result save(@RequestBody TbBrand entity) {
		try {
			brandService.save(entity);
			return new Result(true, "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "保存失败");
		}
	}

	@RequestMapping("/search")
	public PageResult search(@RequestBody TbBrand entity, int page, int pageSize) {
		return brandService.findPage(entity, page, pageSize);
	}
	
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			brandService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
}
