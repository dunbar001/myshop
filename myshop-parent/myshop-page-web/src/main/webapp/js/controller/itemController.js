app.controller("itemController",function($scope){
	//+-数量
	$scope.addNum=function(x){
		$scope.num+=x;
		if($scope.num<1){
			$scope.num=1;
		}
	}
	$scope.specification={};
	//选择规格
	$scope.selectSpecification=function(key,value){
		$scope.specification[key]=value;
		selectSku();
	}
	//判断是否选择当前规格
	$scope.isSelected=function(key,value){
		if($scope.specification[key] == value){
			return true;
		}else{
			return false;
		}
	}
	//加载sku
	$scope.loadSku=function(){
		$scope.sku=skuList[0];
		$scope.specification=JSON.parse(JSON.stringify($scope.sku.spec));
	}
	
	//比较两个对象是否相等
	matchObj=function(obj1,obj2){
		for(var key in obj1){
			if(obj1[key]!=obj2[key]){
				return false;
			}
		}
		
		for(var key in obj2){
			if(obj2[key]!=obj1[key]){
				return false;
			}
		}
		return true;
	}
	//选择sku
	selectSku=function(){
		
		for(var i=0;i<skuList.length;i++){
			if(matchObj(skuList[i],$scope.specification)){
				$scope.sku=skuList[i];
				return ;
			}
		}
		$scope.sku={"id":0,"title":"---","price":0};
		
	}
	//加入购物车
	$scope.addCart=function(){
		alert("skuid" + $scope.sku.id);
		
	}
})