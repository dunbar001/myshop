app.controller("searchController",function($scope,$location,searchService){
	$scope.searchMap={'spec':{},'category':'','brand':'','price':'','pageNo':1,'pageSize':15,'sortType':'','sortField':''};
	
	$scope.search=function(){
		$scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
		searchService.search($scope.searchMap).success(
				function(response){
					$scope.resultMap=response;
					builderPageLabel();
				}
		)
	}
	
	$scope.addSearchMap=function(key,value){
		if(key == 'category' || key == 'brand' || key == 'price'){
			$scope.searchMap[key]=value;
		}else{
			$scope.searchMap.spec[key]=value;
		}
		$scope.search();
	}
	
	$scope.removeSearchMap=function(key){
		if(key == 'category' || key == 'brand' || key == 'price'){
			$scope.searchMap[key]='';
		}else{
			delete $scope.searchMap.spec[key];
		}
		$scope.search();
	}
	
	//构建页码
	builderPageLabel=function(){
		$scope.pageLabel=[];
		var totalPages = $scope.resultMap.totalPages;
		var firstPage = 1;
		var lastPage = totalPages;
		$scope.isShowPreDotted=false;
		$scope.isShowAfterDotted=false;
		//以总共7页为例
		//大于7页
		if(totalPages > 5){
			if($scope.searchMap.pageNo<=4){
				lastPage = 5;
				$scope.isShowAfterDotted=true;
			}else if($scope.searchMap.pageNo>=lastPage-2){
				firstPage=lastPage-4;
				$scope.isShowPreDotted=true;
			}else{
				firstPage=$scope.searchMap.pageNo-2;
				lastPage=$scope.searchMap.pageNo+2;
				$scope.isShowPreDotted=true;
				$scope.isShowAfterDotted=true;
			}
		}
		
		for(var i=firstPage;i<=lastPage;i++){
			$scope.pageLabel.push(i);
		}
	}
	//根据页码查询
	$scope.queryByPage=function(pageNo){
		if(pageNo<1 || pageNo>$scope.resultMap.totalPages){
			return;
		}
		$scope.searchMap.pageNo=pageNo;
		$scope.search();
	}
	//排序查询
	$scope.querySort=function(sortType,sortField){
		$scope.searchMap.sortType=sortType;
		$scope.searchMap.sortField=sortField;
		$scope.search();
	}
	//判断关键字是否为品牌
	$scope.keywordsEQBrand=function(){
		for(var i=0;i<$scope.resultMap.brandList.length;i++){
			if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
				return true;
			}
		}
		return false;
	}
	
	//首页跳转查询
	$scope.initSearch=function(){
		var keywords = $location.search()['keywords'];
		$scope.searchMap.keywords=keywords;
		$scope.search();
	}
	
})