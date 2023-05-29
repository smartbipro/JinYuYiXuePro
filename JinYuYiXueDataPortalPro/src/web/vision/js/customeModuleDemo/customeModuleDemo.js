var jsloader = parent.parent.jsloader;
if(!jsloader){
	jsloader = new JSLoader();
}
var util = jsloader.resolve('freequery.common.util');
var lang = jsloader.resolve("freequery.lang.lang");
var userService = jsloader.imports('bof.usermanager.UserService').getInstance();
var passed = false;
var jyyxDataPortalModual = "JinYuYiXueDataPortalModule";
var registry = jsloader.resolve('freequery.lang.registry');
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
	//login();
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
/*	var SearchHelper = jsloader.resolve('bof.baseajax.common.searchHelper');
	debugger;
	SearchHelper.open("I8a8a85a20187b1f6b1f6bfa00187b205bc520064", "xxxx", "SIMPLE_REPORT");*/
	
	var bannerView = registry.get('bannerView');
	//bannerView._combinedQuery_getCreateAnalysisItems();
	var config = bannerView._combinedQuery_getCreateAnalysisItems();
	
	  var items = bannerView._combinedQuery_getCreateAnalysisItems();
	  items.push({
	    'text': '即席查询',
	    'action': 'CreateCombinedquery',
	    'licenses': 'WizardQuery',
	    'funcIds': 'CUSTOM_DISPLAYCUSTOM_COMBINEDQUERY',
	    'order': 101,
	    'moduleid':'Analysis'
	  });

	  var cmd = {
	    'text': '即席查询',
	    'action': 'CreateCombinedquery',
	    'licenses': 'WizardQuery',
	    'funcIds': 'CUSTOM_DISPLAYCUSTOM_COMBINEDQUERY',
	    'order': 101,
	    'moduleid':'Analysis'
	  };
      var func = 'CREATE';
      var manager = registry.get('CurrentManager');
      var tab = manager.createTab(['DEFAULT_TREENODE',[ cmd, func ]], '新建即席查询');
      if (!tab) {
        return;
      }
      var commandFactory = jsloader.resolve('freequery.tree.superviseCommandFactory');
      var command = commandFactory.getCommand('CombinedQueryCommand');
      if (command) {
        tab.command = command;
        command.onClose.subscribe(function() {
          tab.doClose();
        }, this);
        command.execute(func);
      } else {
        tab.doClose();
      }

	bannerView.__combinedQuery_doCmd(items,this);
}

/**
 * 获取uuid
 */
function getUUID(){
	var UUIDGenerator = jsloader.resolve("freequery.thirdparty.UUIDGenerator");
	var uuid = new UUIDGenerator();
	return uuid.createUUID();
}


function createTabByUrl(){
	var type = "combinedquery";
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
		debugger;
		var bannerView = registry.get('bannerView');
		if(bannerView){
			//bannerView.openMySettings();
			//bannerView.doCmd('jinyuCreateCombinedquery');
			//bannerView.doCreateTabByUrl("../vision/openresource.jsp?resid=" + document.getElementById("jxcxresid").value);
		}
		//jinyuCreateCombinedquery
		//CreateXDashboard
		//CreateCombinedquery
		window.open(url);
		
	} else {
		alert("传入的类型不正确:" + type);
	}	
}

function createTabTest(flag){
	var bannerView = registry.get('bannerView');
	switch(flag){
		case "1"://即席查询新建
			bannerView.doCmd('jinyuCreateCombinedquery');
		break;
		case "2"://即席查询新建带数据模型id
			bannerView.createCustomeCombinedBySourceId('I8a8af1ef01801c6f1c6f5082018040e28fe711e2');
		break;		
		case "3"://即系查询只读
			bannerView.openCustomeTabByResId('combinedquery',true,'I8a88a4960188098f098fc0fa0188098fc0fa0000');
		break;
		case "4"://即席查询编辑
			bannerView.openCustomeTabByResId('combinedquery',false,'I8a88a4960188098f098fc0fa0188098fc0fa0000');
		break;
		case "5"://自助仪表盘新建
			bannerView.createCustomeDashboard(true);
		break;
		case "6"://自助仪表盘新建带数据模型id
			bannerView.createCustomeDashboardBySourceId(true, "I8a8af1ef01801c6f1c6f5082018040e28fe711e2");
		break;
		case "7"://自助仪表盘只读
			bannerView.openCustomeTabByResId('dashboard',true,'0e6f2e9d7b2231ff5d16d15fb4ef750b');
		break;		
		case "8"://自助仪表盘编辑
			bannerView.openCustomeTabByResId('dashboard',false,'0e6f2e9d7b2231ff5d16d15fb4ef750b');
		break;	
		case "9"://打开窗口
			bannerView.createCustomeOpenTabByUrl("明细报表","../vision/customeModuleByIndex.jsp");
			break;
	}
}


