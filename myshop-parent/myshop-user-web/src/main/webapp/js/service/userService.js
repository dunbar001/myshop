app.service("userSerivce",function($http){
	this.reg=function(entity,smscode){
		return $http.post("../user/add.do?smscode="+smscode,entity);
	}
	this.sendSmsCode=function(phone){
		return $http.get("../user/sendSmsCode.do?phone="+phone);
	}
	this.findLoginName=function(){
		return $http.get("../user/findLoginName.do");
	}
})