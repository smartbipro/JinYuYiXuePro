//金域医学新建透视分析
var JinYuCombinedQueryCommand = function(opLogHeaders) {
	JinYuCombinedQueryCommand.superclass.constructor.call(this, opLogHeaders);
	var ret = util.remoteInvokeEx("JinYuYiXueDataPortalModule", "getCurrentUserMsg", []);
	if (ret && ret.succeeded) {
		this.saveFolderId = ret.result.selfPath;
	}
	this.reSetSubCommandMap();
}
lang.extend(JinYuCombinedQueryCommand, 'smartbi.combinedquery.CombinedQueryCommand');

JinYuCombinedQueryCommand.prototype.destroy = function() {
	JinYuCombinedQueryCommand.superclass.destroy.call(this);
	//tab.closeNotConfirm = true
};

JinYuCombinedQueryCommand.prototype.reSetSubCommandMap = function(){
	//this.subCommandMap['AugmentedDataSet'] = "CustomeSmartbiXCommand";
}

JinYuCombinedQueryCommand.prototype.createJinYuYiXue = function(node, isDataSet, skipSelectDataSource) {
	if(!util.checkFunctionValid("CUSTOM_DISPLAYCUSTOM_COMBINEDQUERY") && !util.checkFunctionValid("AUGMENTED_DATASET_ADHOC")){
		alert("您无权进行此项操作");
		return;
	}
	// var DataSourceDialog = jsloader.resolve('smartbi.combinedquery.dialog.DataSourceDialog');
	var DataSourceDialog = jsloader.resolve("freequery.dialog.DataSourceDialog");
	if (!isDataSet) {
		DataSourceDialog = jsloader.resolve("smartbi.combinedquery.dialog.CombinedChooseSource");
	}
	
	// 左侧的树需要先渲染出来
	// 不再使用dialog方式打开弹窗来选择数据源，将这里删除掉
	this.innerType = node && node.innerType;
	
	// 下方这个if应该是判断来源，即是从基本表/业务视图右键创建即席查询，如果是的话，会直接传递已经确认的DSinfo信息，去打开即席查询
	if(node && (node._type == "BASETABLE" || node._type == "BASETABLE_REF" 
		|| node._type == "BASEVIEW")){
		var ds = util.remoteInvoke("DataSourceService", "getDataSourceByTableID", [node._id]);
		if(ds && ds.result && ds.result.id){
			var info = {
					dataSource: ds.result.id,
					tableId: node._id,
			}
			this.selectDsDone(info);
		}
	}else{
		// 新创建的即席查询，此时是没有选择对应的dsInfo信息的，改为不再通过dialog选择dsInfo
		if (skipSelectDataSource) {
			var info = {
					dataSetType: node.dataSetType,
					dataSetId: node.dataSetId,
					node: node
			}
			this.selectDsDone(info);
			var EnumQueryType = jsloader.resolve('freequery.businessview.EnumQueryType');
			this.queryType = EnumQueryType.SQL; // 指定该属性才会显示业务主题
		} else {
			var EnumQueryType = jsloader.resolve('freequery.businessview.EnumQueryType');
			this.queryType = EnumQueryType.SQL; // 指定该属性才会显示业务主题
			DataSourceDialog.show(this.selectDsDone, this);
		}
	}
};