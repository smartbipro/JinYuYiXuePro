var jsloader = new JSLoader();
var util = jsloader.resolve('freequery.common.util');
var lang = jsloader.resolve("freequery.lang.lang");
var userService = jsloader.imports('bof.usermanager.UserService').getInstance();
var passed = false;
var jyyxDataPortalModual = "JinYuYiXueDataPortalModule";
//初始化
function init(){
/*	var loginmsg = document.getElementById("loginmsg");
	try{
		var currentUser = userService.getCurrentUser();
		if(currentUser){
				loginmsg.textContent = "当前登录账户：" + currentUser.name;			
		}else{
			loginmsg.textContent = "当前未登录";
		}
	}catch(e){
		loginmsg.textContent = "当前未登录";
	}*/
	login();
}
//关闭窗口
function cleanup() {
	if (passed) {
		try {
			userService.logout();
		} catch (e) {
		}
	}
}
//登录
function login(){
	var username = document.getElementById("username").value;
	var password = document.getElementById("password").value;
	passed = userService.isLoginAs(username);
	if (!passed) {
		try {
			passed = userService.login(username, password);
		} catch (e) {
		}
	}
	var loginmsg = document.getElementById("loginmsg");
	if (!passed) {
		loginmsg.textContent = "用户【" + username + "】登录不成功，用户名或密码错误。";
	}else{
		loginmsg.textContent = "当前登录账户：" + username;
	}
}

/**调用接口*/
function doModule() {
	try {		
		var util = jsloader.resolve("freequery.common.util");
		var moduleName = document.getElementById("moduleName").value;
		var moduleFn = document.getElementById("moduleFn").value;
		var params = document.getElementById("params").value;
		var ret = null;
		var jsEvalStr = "ret = util.remoteInvokeEx(\"" + moduleName + "\", \"" + moduleFn + "\", [" + params + "]);"
		eval(jsEvalStr);
		//var ret = util.remoteInvokeEx("JinYuYiXueDataPortalModule", "getIndexHomeTreeNodes", []);
		if (ret && ret.succeeded) {
			var elem = document.getElementById('data');
			elem.value = JSON.stringify(ret.result);
		}
	} catch (e) {
		init();
		alert(e.message);
	}
}

/**新增报表*/
function addReport(type) {
	var url = "";
	var isValid = false;//是否有该操作权限
	var domutils;
	if (!domutils) {
		domutils = jsloader.resolve("freequery.lang.domutils");
	}
	switch (type) {
		case "combinedquery":
			isValid = util.checkFunctionValid("CUSTOM_DISPLAYCUSTOM_COMBINEDQUERY") && util.checkFunctionValid("AUGMENTED_DATASET_ADHOC");
			url = "../vision/createresource.jsp?restype=combinedquery";
			break;
		case "insight":
			isValid = domutils.checkFunctionValid("CUSTOM_DISPLAYCUSTOM_INSIGHT");
			url = "../vision/createresource.jsp?restype=INSIGHT";
			break;
		case "dashboard":
			isValid = util.checkFunctionValid("XDASHBOARD_CREATE");
			url = "../smartbix/?integrated=true&showheader=false&dashboardInitId=I1f81a9e00169c94ac94a43130169d2cd942b0430&l=zh_CN&nodeid=DEFAULT_TREENODE#/dashboard";
			break;
	}
	if (!isValid) {
		alert("您无权进行此项操作");
		return;
	}
	if (url) {
		window.open(url);
	} else {
		alert("传入的类型不正确:" + type);
	}
}

/**修改报表*/
function editReport(type) {
	var url = "";
	switch (type) {
		case "combinedquery":
			isValid = util.checkFunctionValid("CUSTOM_DISPLAYCUSTOM_COMBINEDQUERY") && util.checkFunctionValid("AUGMENTED_DATASET_ADHOC");
			url = "../vision/openresource.jsp?resid=" + document.getElementById("jxcxresid").value;
			break;
		case "insight":
			isValid = util.checkFunctionValid("CUSTOM_DISPLAYCUSTOM_INSIGHT");
			url = "../vision/openresource.jsp?resid=" + document.getElementById("tsfxresid").value;
			break;
		case "dashboard":
			isValid = util.checkFunctionValid("XDASHBOARD_CREATE");
			url = "../smartbix/?integrated=true&showheader=false&isNewWindows=true&l=zh_CN#/dashboard/" + document.getElementById("ybpresid").value;
			break;
	}
	if (url) {
		window.open(url);
	}else{
		alert("传入的类型不正确:" + type);
	}
}

//即系查询 REFRESH SAVE SAVEAS MYFAVORITE EXPORT VIEWSQL VIEW CHARTSETTING PRINT PARAMSETTING REPORTSETTING FIELDSETTING CREATEINSIGHTINQUERY MPP FILTERRELATIONSSETTING USERPARAM TIMECONSUMING BACKWARD FORWARD SPACE2 SELECTFIELD SUBTOTAL SETUSERPARAM EDITDATASET
//透视分析 REFRESH SAVE SAVEAS BTNPARAMSETTING PANEL REPORTSETTING WARNING MYFAVORITE CHARTSETTING VIEW EXPORT PRINT SETUSERPARAM RESOURCETREE FILTERRELATION TIMECONSUMING
//
/**只读查询报表*/
function searchReport(type) {
	var url = "";
	switch (type) {
		case "combinedquery":
			isValid = util.checkFunctionValid("CUSTOM_DISPLAYCUSTOM_COMBINEDQUERY") && util.checkFunctionValid("AUGMENTED_DATASET_ADHOC");
			url = "../vision/openresource.jsp?resid=" + document.getElementById("jxcxresid").value;
			url += "&hidetoolbaritems=SAVE SAVEAS MYFAVORITE EXPORT VIEWSQL VIEW CHARTSETTING PRINT PARAMSETTING REPORTSETTING FIELDSETTING CREATEINSIGHTINQUERY MPP FILTERRELATIONSSETTING USERPARAM TIMECONSUMING";
			url += " BACKWARD FORWARD SPACE2 SELECTFIELD SUBTOTAL SETUSERPARAM EDITDATASET";
			break;
		case "insight":
			isValid = util.checkFunctionValid("CUSTOM_DISPLAYCUSTOM_INSIGHT");
			url = "../vision/openresource.jsp?resid=" + document.getElementById("tsfxresid").value;
			url += "&hidetoolbaritems=SAVE SAVEAS BTNPARAMSETTING PANEL REPORTSETTING WARNING MYFAVORITE CHARTSETTING VIEW EXPORT PRINT SETUSERPARAM RESOURCETREE FILTERRELATION TIMECONSUMING";
			//url += "&showtoolbar=false";
			break;
		case "dashboard":
			isValid = util.checkFunctionValid("XDASHBOARD_CREATE");
			url = "../smartbix/?integrated=true&showheader=false&l=zh_CN#/page/" + document.getElementById("ybpresid").value;
			break;
	}
	if (url) {
		window.open(url);
	} else {
		alert("传入的类型不正确:" + type);
	}
}

/**删除报表*/
function deleteReport(resid) {
	var ret = util.remoteInvokeEx("CatalogService", "getCatalogElementById", [resid]);
	if(!ret.result){
		alert("该资源已被删除或您没有该资源的查看权限。");
		return;	
	}
	//判断是否有对该资源有删除权限
	ret = util.remoteInvokeEx("CatalogService", "isCatalogElementAccessible", [resid, "DELETE"]);
	var elem = document.getElementById('data');
	if (ret && ret.succeeded) {
		elem.value = JSON.stringify(ret.result);
	}	
	if(ret.result){
		var ret2 = util.remoteInvokeEx("CatalogService", "deleteCatalogElement", [resid]);
		if(ret2 && ret2.succeeded){
			elem.value += "=========================";
			elem.value += JSON.stringify(ret2.result);
		}
	}else{
		alert("您没有删除该资源的权限。")
	}
}

/**指标查询计算 */
function indexClickCalc(type){
	var indexId = document.getElementById("indexId").value;
	var indexName = document.getElementById("indexName").value;
	var methodName = "indexAddSearchNumber";//默认查询
	if(type == 2){//指标点击埋点查询
		methodName = "indexAddClickNumber";
	}
	ret = util.remoteInvokeEx(jyyxDataPortalModual, methodName, [indexId, indexName]);
	debugger;
}
