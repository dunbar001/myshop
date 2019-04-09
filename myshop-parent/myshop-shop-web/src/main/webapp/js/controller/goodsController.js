 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,goodsService,itemCatService,uploadService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	//保存
	$scope.add = function(){
		$scope.entity.goodsDesc.introduction=editor.html();
		goodsService.add( $scope.entity).success(
				function(response){
					if(response.success){
						alert("保存成功");
						$scope.entity={};
						editor.html('');
					}else{
						alert("保存失败");
					}
				}
		)
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//读取一级分类
	$scope.selectCategory1List=function(){
		itemCatService.findByParentId(0).success(
				function(response){
					$scope.itemCat1List=response;
				}
		)
	}
	
	//加载二级分类
	$scope.$watch("entity.goods.category1Id",function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(
				function(response){
					$scope.itemCat2List=response;
				}
		)
	})
	
	//加载三级分类
	$scope.$watch("entity.goods.category2Id",function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(
				function(response){
					$scope.itemCat3List=response;
				}
		)
	})
	
	//加载模板ID
	$scope.$watch("entity.goods.category3Id",function(newValue,oldValue){
		
		itemCatService.findOne(newValue).success(
				function(response){
					$scope.entity.goods.typeTemplateId=response.typeId;
				}
		)
		
		//加载规格列表
		typeTemplateService.findSpecListById(newValue).success(
				function(response){
					$scope.specList=response;
				}
		)
		
	})
	
	$scope.$watch("entity.goods.typeTemplateId",function(newValue,oldValue){
		
		typeTemplateService.findOne(newValue).success(
				function(response){
					$scope.typeTemplate=response;
					$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);
					$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);
				}
		)
		
	})
	
	//上传图片
	$scope.upload=function(){
		uploadService.upload().success(
				function(response){
					if(response.success){
						$scope.image_entity.url=response.message;
					}else{
						alert(response.message);
					}
				}
		).error(
				function(){
					alert("上传发送错误");
				}
		)
	}
	
	//新增图片项
	$scope.add_image_entity=function(){
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}
	//删除图片项
	$scope.remove_image_entity=function(index){
		$scope.entity.goodsDesc.itemImages.splice(index,1);
	}
	//
	$scope.selectSpec=function($event,name,value){
		//[{specName:"内存",specOptions:["3G","4G"]},{specName:"内存",specOptions:["3G","4G"]}]
		var specItemList = $scope.entity.goodsDesc.specificationItems;
		var obj = $scope.searchObjectByKey(specItemList,"attributeName",name);
		if(obj != null){
			if($event.target.checked){
				obj.attributeValue.push(value);
			}else{
				obj.attributeValue.splice(obj.attributeValue.indexOf(value),1);
				if(obj.attributeValue.length==0){
					specItemList.splice(specItemList.indexOf(obj),1);
				}
			}
		}else{
			specItemList.push({"attributeName":name,"attributeValue":[value]});
		}
	}
	
	$scope.createItemList=function(){
		$scope.entity.itemList=[{spec:{},price:0,num:999999,status:"0",isDefault:"0"}];
		var specList = $scope.entity.goodsDesc.specificationItems;
		for(var i=0;i<specList.length;i++){
			$scope.entity.itemList=addCulumn($scope.entity.itemList,specList[i].attributeName,specList[i].attributeValue);
		}
	}
	
	addCulumn=function(list,name,value){
		var newRowList=[];
		for(var i=0;i<list.length;i++){
			var oldRow = list[i];
			for(var j=0;j<value.length;j++){
				var newRow =JSON.parse(JSON.stringify(oldRow));
				newRow.spec[name]=value[j];
				list.push(newRow);
			}
		}
		
	}
    
});	
