app.service("contentService",function($http){
	this.search=function(categoryId){
		return $http.get('/findByCategoryId.do?categoryId='+categoryId);
	}
})