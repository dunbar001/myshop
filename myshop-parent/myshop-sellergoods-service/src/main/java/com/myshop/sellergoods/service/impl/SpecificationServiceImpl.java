package com.myshop.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.myshop.mapper.TbSpecificationMapper;
import com.myshop.mapper.TbSpecificationOptionMapper;
import com.myshop.pojo.TbSpecification;
import com.myshop.pojo.TbSpecificationExample;
import com.myshop.pojo.TbSpecificationExample.Criteria;
import com.myshop.pojo.TbSpecificationOption;
import com.myshop.pojo.TbSpecificationOptionExample;
import com.myshop.sellergoods.service.SpecificationService;

import entity.PageResult;
import entity.Specification;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
		specificationMapper.insert(specification.getSpecEntity());
		for(TbSpecificationOption opt : specification.getSpecOptionList()){
			opt.setSpecId(specification.getSpecEntity().getId());
			specificationOptionMapper.insert(opt);
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
		specificationMapper.updateByPrimaryKey(specification.getSpecEntity());
		
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		com.myshop.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(specification.getSpecEntity().getId());
		specificationOptionMapper.deleteByExample(example );
		
		for(TbSpecificationOption opt : specification.getSpecOptionList()){
			specificationOptionMapper.insert(opt);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		Specification spec = new Specification();
		TbSpecification specification = specificationMapper.selectByPrimaryKey(id);
		spec.setSpecEntity(specification);
		
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		com.myshop.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(id);
		List<TbSpecificationOption> specOptionList = specificationOptionMapper.selectByExample(example );
		spec.setSpecOptionList(specOptionList);
		return spec;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			specificationMapper.deleteByPrimaryKey(id);
			
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			com.myshop.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(id);
			specificationOptionMapper.deleteByExample(example );
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Override
		public List<Map> findSpecSelectDatas() {
			// TODO Auto-generated method stub
			return specificationMapper.findSpecSelectDatas();
		}
	
}
