//金域医学新建透视分析
var CustomeSmartbiXCommand = function() {
	CustomeSmartbiXCommand.superclass.constructor.call(this);
}
lang.extend(CustomeSmartbiXCommand, 'smartbi.smartbix.SmartbiXCommand');

CustomeSmartbiXCommand.prototype.destroy = function() {
	CustomeSmartbiXCommand.superclass.destroy.call(this);
};

/**
 * 在新Tab中打开URL
 * isForce 是否强制执行，执行过isHandledMessage时，不需要再进行isHandledMessage验证
 */
CustomeSmartbiXCommand.prototype.createCustomeCombinedBySourceId = function(data, isForce) {
	if (isForce !== true && this.isHandledMessage(data)) {
		return;
	}
	var action = 'OPEN';
	var resType = data.resType || 'SMARTBIX_PAGE';
	var resId = data.id;
	var tabId = [ resId, action ];
	var tabName = data.alias || data.name;
	var manager = registry.get('CurrentManager');
	if (data.paramsInfo) {
    	// 带有参数信息时同一资源允许打开多次
		tabId.push(JSON.stringify(data.paramsInfo));
	}
	var tab = manager.createTab(tabId, tabName, undefined, undefined, resType);
	if (!tab) {
		return;
	}
	var cmd = 'URLLinkCommand';
	var commandFactory = manager.getCommandFactory();
	var command = tab.command = commandFactory.getCommand(cmd);
	if (!command) {
		tab.doClose();
		return;
	}
	if (command.onClose) {
		command.onClose.subscribe(function() {
			tab.doClose();
    	}, command);
    	command.onClose.subscribe(function() {
      		tab.doClose();
		}, this);
	}
	this.reSetTabCaption(tab, 6);
	command.openLinkInCurrentWindow(data.url, data);
	manager.setCommand(tab, command);
};

//新建仪表盘
CustomeSmartbiXCommand.prototype.createCustomeDashboard = function(data, isForce) {
	if (isForce !== true && this.isHandledMessage(data)) {
		return;
	}
	var action = 'OPEN';
	var resType = data.resType || 'SMARTBIX_PAGE';
	var resId = data.id;
	var tabId = [ resId, action ];
	var tabName = data.alias || data.name;
	var manager = registry.get('CurrentManager');
	if (data.paramsInfo) {
    	// 带有参数信息时同一资源允许打开多次
		tabId.push(JSON.stringify(data.paramsInfo));
	}
	var tab = manager.createTab(tabId, tabName, undefined, undefined, resType);
	if (!tab) {
		return;
	}
	var cmd = 'URLLinkCommand';
	var commandFactory = manager.getCommandFactory();
	var command = tab.command = commandFactory.getCommand(cmd);
	if (!command) {
		tab.doClose();
		return;
	}
	if (command.onClose) {
		command.onClose.subscribe(function() {
			tab.doClose();
    	}, command);
    	command.onClose.subscribe(function() {
      		tab.doClose();
		}, this);
	}
	command.openLinkInCurrentWindow(data.url, data);
	manager.setCommand(tab, command);
};


/**
 * 打开报表
 */
CustomeSmartbiXCommand.prototype.openUrlInTabForJinYuYiXue = function(resId, url) {
	var cmd = "jinyuCreateCombinedquery";
	var func = 'OPEN';
	var manager = registry.get('CurrentManager');
	var tab = manager.createTab([resId,[ cmd, func ]], '');
	if (!tab) {
		return;
	}
	var data ={
		action: "SMARTBIX_OPEN_URL",
		commandid: undefined,
		from: "SMARTBIX",
		id: "",
		isOpenResourceParam: true,
		method: "post",
		paramsInfo: [],
		to: "SMARTBI",
		url: url,
		uuid: this.getCustomeUuid()
	} 	

	var cmd = 'URLLinkCommand';
	var commandFactory = manager.getCommandFactory();
	var command = tab.command = commandFactory.getCommand(cmd);
	if (!command) {
		tab.doClose();
    	return;
	}
	if (command.onClose) {
		command.onClose.subscribe(function() {
			tab.doClose();
		}, command);
		command.onClose.subscribe(function() {
			tab.doClose();
		}, this);
  	}
	command.openLinkInCurrentWindow(url, data);
	manager.setCommand(tab, command);	
}

/**
 * 打开即系查询
 * @param resId   资源id
 * @param isRead  是否只读
 */
CustomeSmartbiXCommand.prototype.openUrlInTabForCombinedQuery = function(resId, isRead) {
	var cmd = "jinyuCreateCombinedquery";
      var func = 'OPEN';
      var manager = registry.get('CurrentManager');
      var tab = manager.createTab([resId,[ cmd, func ]], '');
      if (!tab) {
        return;
      }
	var url = "../vision/openresource.jsp?resid=" + resId;
	if(isRead){
		url += "&hidetoolbaritems=SAVE SAVEAS MYFAVORITE EXPORT VIEWSQL VIEW CHARTSETTING PRINT PARAMSETTING REPORTSETTING FIELDSETTING CREATEINSIGHTINQUERY MPP FILTERRELATIONSSETTING USERPARAM TIMECONSUMING";
		url += " BACKWARD FORWARD SPACE2 SELECTFIELD SUBTOTAL SETUSERPARAM EDITDATASET";
	}
	var data ={
		action: "SMARTBIX_OPEN_URL",
		commandid: undefined,
		from: "SMARTBIX",
		id: "",
		isOpenResourceParam: true,
		method: "post",
		paramsInfo: [],
		to: "SMARTBI",
		url: url,
		uuid: this.getCustomeUuid()
	} 	

  var cmd = 'URLLinkCommand';
  var commandFactory = manager.getCommandFactory();
  var command = tab.command = commandFactory.getCommand(cmd);
  if (!command) {
    tab.doClose();
    return;
  }
  if (command.onClose) {
    command.onClose.subscribe(function() {
      tab.doClose();
    }, command);

    command.onClose.subscribe(function() {
      tab.doClose();
    }, this);
  }
   command.openLinkInCurrentWindow(url, data);
   manager.setCommand(tab, command);
};

/**
 * 打开即系查询
 * @param resId   资源id
 * @param isRead  是否只读
 */
CustomeSmartbiXCommand.prototype.openUrlInTabForDashboard = function(resId, isRead) {
	var cmd = "jinyuCreateCombinedquery";
      var func = 'OPEN';
      var manager = registry.get('CurrentManager');
      var tab = manager.createTab([resId,[ cmd, func ]], '');
      if (!tab) {
        return;
      }
	var url = "../smartbix/?integrated=true&showheader=false&l=zh_CN#/page/" + resId;
	if(isRead){
		url = "../smartbix/?integrated=true&showheader=false&isNewWindows=true&l=zh_CN#/dashboard/" + resId;
	}
	var data ={
		action: "SMARTBIX_OPEN_URL",
		commandid: undefined,
		from: "SMARTBIX",
		id: "",
		isOpenResourceParam: true,
		method: "post",
		paramsInfo: [],
		to: "SMARTBI",
		url: url,
		uuid: this.getCustomeUuid()
	} 	

  var cmd = 'URLLinkCommand';
  var commandFactory = manager.getCommandFactory();
  var command = tab.command = commandFactory.getCommand(cmd);
  if (!command) {
    tab.doClose();
    return;
  }
  if (command.onClose) {
    command.onClose.subscribe(function() {
      tab.doClose();
    }, command);

    command.onClose.subscribe(function() {
      tab.doClose();
    }, this);
  }
   command.openLinkInCurrentWindow(url, data);
   manager.setCommand(tab, command);
};


/**
 * 获取uuid
 */
CustomeSmartbiXCommand.prototype.getCustomeUuid = function(){
	var UUIDGenerator = jsloader.resolve("freequery.thirdparty.UUIDGenerator");
	var uuid = new UUIDGenerator();
	return uuid.createUUID();
}


/**
 * 重置tab的标题
 */
CustomeSmartbiXCommand.prototype.reSetTabCaption = function(tab, len){
	tab.customeSetCaption = tab.setCaption;
	tab.setCaption = function(value){
		tab.customeSetCaption(value);
		if(value && value.length >= len){
			tab.spanText.innerHTML = value.substring(0,(len-1)) + "...";
		}
	}
}

/**
 * 在新Tab中打开URL
 * isForce 是否强制执行，执行过isHandledMessage时，不需要再进行isHandledMessage验证
 */
CustomeSmartbiXCommand.prototype.createCustomeTabByUrlData = function(data, isForce) {
	if (isForce !== true && this.isHandledMessage(data)) {
		return;
	}
	var action = 'OPEN';
	var resType = data.resType || 'SMARTBIX_PAGE';
	var resId = data.id;
	var tabId = [ resId, action ];
	var tabName = data.alias || data.name;
	var manager = registry.get('CurrentManager');
	if (data.paramsInfo) {
    	// 带有参数信息时同一资源允许打开多次
		tabId.push(JSON.stringify(data.paramsInfo));
	}
	var tab = manager.createTab(tabId, tabName, undefined, undefined, resType);
	if (!tab) {
		return;
	}
	var cmd = 'URLLinkCommand';
	var commandFactory = manager.getCommandFactory();
	var command = tab.command = commandFactory.getCommand(cmd);
	if (!command) {
		tab.doClose();
		return;
	}
	if (command.onClose) {
		command.onClose.subscribe(function() {
			tab.doClose();
    	}, command);
    	command.onClose.subscribe(function() {
      		tab.doClose();
		}, this);
	}
	this.reSetTabCaption(tab, 6);
	command.openLinkInCurrentWindow(data.url, data);
	manager.setCommand(tab, command);
};


/**
 * 在新Tab中打开URL  用于指标查询功能
 * isForce 是否强制执行，执行过isHandledMessage时，不需要再进行isHandledMessage验证
 */
CustomeSmartbiXCommand.prototype.createCustomeIndexTabByUrlData = function(data, isForce) {
	if (isForce !== true && this.isHandledMessage(data)) {
		return;
	}
	var manager = registry.get('CurrentManager');
	for(var i in manager.tabControl.activeTabQueue){
		var tmpTab = manager.tabControl.activeTabQueue[i];
		if(tmpTab.title == data.name){
			//tmpTab.btnClose.click();
			if(tmpTab.customeDataPortalUrl == data.url){
				return;
			}
			if(tmpTab.command && tmpTab.command.commandFactory && tmpTab.command.commandFactory.currentCommand 
			&& tmpTab.command.commandFactory.currentCommand.parentObj 
			&& tmpTab.command.commandFactory.currentCommand.parentObj.getElementsByTagName("form")
			&& tmpTab.command.commandFactory.currentCommand.parentObj.getElementsByTagName("form")[0]){
				tmpTab.customeDataPortalUrl = data.url;
				tmpTab.command.commandFactory.currentCommand.parentObj.getElementsByTagName("form")[0].action = data.url;
				tmpTab.command.commandFactory.currentCommand.parentObj.getElementsByTagName("form")[0].submit(); 	
			}
			return;
		}
	}
	var action = 'OPEN';
	var resType = data.resType || 'SMARTBIX_PAGE';
	var resId = data.id;
	var tabId = [ resId, action ];
	var tabName = data.alias || data.name;
	if (data.paramsInfo) {
    	// 带有参数信息时同一资源允许打开多次
		tabId.push(JSON.stringify(data.paramsInfo));
	}
	var tab = manager.createTab(tabId, tabName, undefined, undefined, resType);
	if (!tab) {
		return;
	}
	tab.customeDataPortalUrl = data.url;
	var cmd = 'URLLinkCommand';
	var commandFactory = manager.getCommandFactory();
	var command = tab.command = commandFactory.getCommand(cmd);
	if (!command) {
		tab.doClose();
		return;
	}
	if (command.onClose) {
		command.onClose.subscribe(function() {
			tab.doClose();
    	}, command);
    	command.onClose.subscribe(function() {
      		tab.doClose();
		}, this);
	}
	this.reSetTabCaption(tab, 6);
	command.openLinkInCurrentWindow(data.url, data);
	manager.setCommand(tab, command);
};


/**
 * 在新Tab中打开URL  用于指标详情功能
 * isForce 是否强制执行，执行过isHandledMessage时，不需要再进行isHandledMessage验证
 */
CustomeSmartbiXCommand.prototype.createCustomeIndexDetailTabByUrl = function(data, isForce) {
	if (isForce !== true && this.isHandledMessage(data)) {
		return;
	}
	var manager = registry.get('CurrentManager');
	for(var i in manager.tabControl.activeTabQueue){
		var tmpTab = manager.tabControl.activeTabQueue[i];
		if(tmpTab.title == data.name){
			//tmpTab.btnClose.click();
			if(tmpTab.customeDataPortalUrl == data.url){
				return;
			}
			//先隐藏当前的tab页签
			if (manager.tabControl && manager.tabControl.activeTab){//隐藏前一个tab页的内容
				manager.hideTabPanel(manager.tabControl.activeTab.id);
			}
			this.customeChangeTab(tmpTab);
			if(tmpTab.command && tmpTab.command.commandFactory && tmpTab.command.commandFactory.currentCommand 
			&& tmpTab.command.commandFactory.currentCommand.parentObj 
			&& tmpTab.command.commandFactory.currentCommand.parentObj.getElementsByTagName("form")
			&& tmpTab.command.commandFactory.currentCommand.parentObj.getElementsByTagName("form")[0]){
				tmpTab.customeDataPortalUrl = data.url;
				tmpTab.command.commandFactory.currentCommand.parentObj.getElementsByTagName("form")[0].action = data.url;
				tmpTab.command.commandFactory.currentCommand.parentObj.getElementsByTagName("form")[0].submit(); 	
			}
			return;
		}
	}
	var action = 'OPEN';
	var resType = data.resType || 'SMARTBIX_PAGE';
	var resId = data.id;
	var tabId = [ resId, action ];
	var tabName = data.alias || data.name;
	if (data.paramsInfo) {
    	// 带有参数信息时同一资源允许打开多次
		tabId.push(JSON.stringify(data.paramsInfo));
	}
	var tab = manager.createTab(tabId, tabName, undefined, undefined, resType);
	if (!tab) {
		return;
	}
	tab.customeDataPortalUrl = data.url;
	var cmd = 'URLLinkCommand';
	var commandFactory = manager.getCommandFactory();
	var command = tab.command = commandFactory.getCommand(cmd);
	if (!command) {
		tab.doClose();
		return;
	}
	if (command.onClose) {
		command.onClose.subscribe(function() {
			tab.doClose();
    	}, command);
    	command.onClose.subscribe(function() {
      		tab.doClose();
		}, this);
	}
	this.reSetTabCaption(tab, 6);
	command.openLinkInCurrentWindow(data.url, data);
	manager.setCommand(tab, command);
};

var PortalIndexUtil = jsloader.resolve('ext.shundebank.util.PortalIndexUtil');
CustomeSmartbiXCommand.prototype.customeChangeTab = function(newTab){	
    if (PortalIndexUtil.isCustomPortalPage()) {
        if (newTab.spanM && newTab.spanM.firstElementChild) {
            var allSelectedTab = document.getElementsByClassName("custom_portal_selected_tab_bg");
            if (allSelectedTab.length > 0) {
                for (var tabIndex = 0; tabIndex < allSelectedTab.length; tabIndex++) {
                    allSelectedTab[tabIndex].className = "custom_portal_normal_tab_bg";
                }
            }
        }
    }
	newTab.spanM.parentElement.className = "custom_portal_selected_tab_bg";
	newTab.setActive(true);
}