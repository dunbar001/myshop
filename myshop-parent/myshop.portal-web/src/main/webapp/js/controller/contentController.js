app.controller("contentController",function($scope,contentService){
	$scope.findContentByCategoryId=function(categoryId){
		contentService.search(categoryId).success(
				function(response){
					$scope.contentList=response;
				}
		)
	}
	
	$scope.search=function(){
		location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
	}
})