var AbstractSystemConfigItem = jsloader.resolve('freequery.config.configitem.AbstractSystemConfigItem');
var domutils = jsloader.resolve('freequery.lang.domutils');

//数据缓存配置
var DataCacheConfig = function() {
	this.itemName = '${JYYX_DATA_CACHE_CONFIG}'; // 配置项名称
	this.dbkey = 'JYYX_DATA_CACHE_CONFIG';
};
lang.extend(DataCacheConfig, AbstractSystemConfigItem);

DataCacheConfig.prototype.init = function() {
	this.tr = document.createElement('tr');
	this.tr.height = '30';

	this.td1 = document.createElement('td');
	this.td1.align = 'left';
	this.td1.width = '200px';
	this.td1.innerHTML = this.itemName + '${Colon}';
	this.tr.appendChild(this.td1);

	this.td2 = document.createElement('td');
	this.td2.innerHTML = '<input type="radio" name="DataCacheConfig" id="Yes" class="_DataCacheConfigY" checked/><label >${MobileYes}</label>'
			+ '<input type="radio" name="DataCacheConfig" id="No"  class="_DataCacheConfigN" /><label >${MobileNo}</label>';
	this.tr.appendChild(this.td2);

	this.td3 = document.createElement('td');
	this.td3.innerHTML = '${Theinitialvalue(is)}';
	this.tr.appendChild(this.td3);

	this.td4 = document.createElement('td');
	this.td4.innerHTML = '<input class="button-buttonbar-noimage _DataCacheConfigBtn " value="${Restoreoriginalvalues}" type="button" style="width:100%;" />';
	this.tr.appendChild(this.td4);

	this.radioyes = domutils.findElementByClassName([ this.tr ], '_DataCacheConfigY');
	this.radiono = domutils.findElementByClassName([ this.tr ], '_DataCacheConfigN');

	this.initBtn = domutils.findElementByClassName([ this.tr ], '_DataCacheConfigBtn');
	this.addListener(this.initBtn, 'click', this.doInitAutoRealtimeDump, this);
	this.doInitAutoRealtimeDump();
	return this.tr;
}

DataCacheConfig.prototype.validate = function() {
	return true;
}

DataCacheConfig.prototype.save = function() {
	if (!this.validate()) {
		return false;
	}
	var obj = {
		key : this.dbkey,
		value : this.radiono.checked ? 'false' : 'true'
	};
	return obj;
}

DataCacheConfig.prototype.doInitAutoRealtimeDump = function() {
	this.radioyes.checked = true;
	this.radiono.checked = false;
};

DataCacheConfig.prototype.handleConfig = function(systemConfig) {
	for ( var i in systemConfig) {
		if (systemConfig[i].key == this.dbkey) {
			if (systemConfig[i].value == 'false') {
				this.radioyes.setAttribute('__checked', false);
				this.radiono.setAttribute('__checked', true);
			} else {
				this.radioyes.setAttribute('__checked', true);
				this.radiono.setAttribute('__checked', false);
			}
			break;
		}
	}
};
