var AbstractSystemConfigItem = jsloader
    .resolve("freequery.config.configitem.AbstractSystemConfigItem");
var util = jsloader.resolve("freequery.common.util");

// 报表明细路径
var ReportDetailPath = function() {
  this.itemName = "${JYYX_REPORT_DETAIL_PATH}"; // 配置项名称
  this.dbkey = "JYYX_REPORT_DETAIL_PATH";
};
lang.extend(ReportDetailPath, AbstractSystemConfigItem);
// 进行初始化化动作并返回一个 tr 元素
ReportDetailPath.prototype.init = function() {
  this.tr = document.createElement('tr');
  this.tr.height = '30';

  this.td1 = document.createElement('td');
  this.td1.align = 'left';
  this.td1.width = '200px';
  this.td1.innerHTML = this.itemName + '${Colon}';
  this.tr.appendChild(this.td1);

  this.td2 = document.createElement('td');
  this.td2.innerHTML = '<input class="JYYX_REPORT_DETAIL_PATH inputtext" type="text" maxlength="300" style="width:100%;" />';
  this.tr.appendChild(this.td2);

  this.td3 = document.createElement('td');
  this.td3.innerHTML = "${Theinitialvalue(Blank)}";
  this.tr.appendChild(this.td3);

  this.td4 = document.createElement('td');
  this.td4.innerHTML = "<div style='height:30px;  '><input class='button-buttonbar-noimage' value='${Restoreoriginalvalues}' type='button' style='width:100%;' /></div>";

  this.tr.appendChild(this.td4);

  this.editer = domutils.findElementByClassName([ this.tr ], 'JYYX_REPORT_DETAIL_PATH');
  this.editer.value = '';
  return this.tr;
};

ReportDetailPath.prototype.validate = function() {
  return true;
};

ReportDetailPath.prototype.save = function() {
  if (!this.validate()) {
    return false;
  }
  var obj = {
    key : this.dbkey,
    value : this.editer.value
  };
  return obj;
};

ReportDetailPath.prototype.handleConfig = function(systemConfig) {
  for ( var i in systemConfig) {
    if (systemConfig[i].key == this.dbkey) {
      this.editer.value = systemConfig[i].value;
      break;
    }
  }
};;