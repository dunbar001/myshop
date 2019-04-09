package com.myshop.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.myshop.mapper.TbBrandMapper;
import com.myshop.pojo.TbBrand;
import com.myshop.pojo.TbBrandExample;
import com.myshop.pojo.TbBrandExample.Criteria;
import com.myshop.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;

@Service
public class BrandServiceImpl implements BrandService {
	@Autowired
	private TbBrandMapper mapper;

	@Override
	public List<TbBrand> findAll() {
		return mapper.selectByExample(null);
	}

	@Override
	public PageResult findPage(int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		Page<TbBrand> pageList = (Page<TbBrand>) mapper.selectByExample(null);
		return new PageResult(pageList.getTotal(), pageList.getResult());
	}

	@Override
	public TbBrand findOne(Long id) {
		return mapper.selectByPrimaryKey(id);
	}

	@Override
	public void save(TbBrand entity) {
		if (entity.getId() != null) {
			// 修改
			mapper.updateByPrimaryKey(entity);
		} else {
			// 新增
			mapper.insert(entity);
		}
	}

	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			mapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findPage(TbBrand entity, int page, int pageSize) {
		TbBrandExample example = new TbBrandExample();
		Criteria criteria = example.createCriteria();
		if (entity != null) {
			if (entity.getName() != null && entity.getName().length() > 0) {
				criteria.andNameLike("%" + entity.getName() + "%");
			}
			if (entity.getFirstChar() != null && entity.getFirstChar().length() > 0) {
				criteria.andFirstCharLike("%" + entity.getFirstChar() + "%");
			}
		}
		PageHelper.startPage(page, pageSize);
		Page<TbBrand> pageList = (Page<TbBrand>) mapper.selectByExample(example);
		return new PageResult(pageList.getTotal(), pageList.getResult());
	}

	@Override
	public List<Map> findBrandSelectDatas() {
		return (List<Map>) mapper.findBrandSelectDatas();
	}
}
