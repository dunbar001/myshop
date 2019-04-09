 //控制层 
app.controller('contentController' ,function($scope,$controller   ,contentService,contentCategoryService,uploadService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		contentService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		contentService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		contentService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=contentService.update( $scope.entity ); //修改  
		}else{
			serviceObject=contentService.add( $scope.entity  );//增加 
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
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		contentService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		contentService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//加载广告分类列表
	$scope.loadContentCategory=function(){
		contentCategoryService.findAll().success(
				function(response){
					$scope.contentCategoryList=response;
				}
		)
	}
	//根据分类id查询广告分类名称
	$scope.findCategoryNameById=function(categoryId){
		if($scope.contentCategoryList!=null && $scope.contentCategoryList.length>0){
			for(var i=0;i<$scope.contentCategoryList.length;i++){
				if($scope.contentCategoryList[i].id==categoryId){
					return $scope.contentCategoryList[i].name;
				}
			}
		}
		return "";
	}
	
	$scope.statusText=["无效","有效"];
    //上传图片
	$scope.uploadImage=function(){
		uploadService.upload().success(
				function(response){
					if(response.success){
						$scope.entity.pic=response.message;
					}else{
						alert(response.message);
					}
				}
		).error(
				function(){
					alert("上传失败");
				}
		);
	}
	//批量更新广告状态
	$scope.updateStatus=function(status){
		contentService.updateStatus($scope.selectIds,status).success(
				function(response){
					if(response.success){
						alert(response.message);
						$scope.reloadList();
						$scope.selectIds=[];
					}else{
						alert(response.message);
					}
				}
		)
	}
});	
