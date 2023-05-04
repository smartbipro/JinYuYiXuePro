var AbstractSystemConfigItem = jsloader.resolve("freequery.config.configitem.AbstractSystemConfigItem");
var domutils = jsloader.resolve("freequery.lang.domutils");

var IndexExtendsConfig = function () {
    this.itemName = "${JYYX_INDEX_EXTENDS_CONFIG}";  //配置项名称
    this.dbkey = "JYYX_INDEX_EXTENDS_CONFIG";
};
lang.extend(IndexExtendsConfig, AbstractSystemConfigItem);

IndexExtendsConfig.prototype.init = function () {
    this.tr = document.createElement("tr");
    this.tr.height = "10";

    this.td1 = document.createElement("td");
    this.td1.align = "left";
    this.td1.width = "200px";
    this.td1.innerHTML = this.itemName + "：";
    this.tr.appendChild(this.td1);

    this.td2 = document.createElement("td");
    this.td2.innerHTML = "<textarea class='IndexExtendsConfig inputtext' style='width:100%;height:40px;resize:none'/>";
    this.tr.appendChild(this.td2);
    this.editer = domutils.findElementByClassName([this.tr], "IndexExtendsConfig");

    this.td3 = document.createElement("td");
    this.td3.innerHTML = "${JYYX_INDEX_EXTENDS_CONFIG_TIP}";
    this.tr.appendChild(this.td3);

    return this.tr;
}

IndexExtendsConfig.prototype.save = function () {
    obj = {};
    obj.key = this.dbkey;
    obj.longValue = this.editer.value;
    return obj;
}

IndexExtendsConfig.prototype.handleConfig = function (systemConfig) {
    for (var i in systemConfig) {
        if (systemConfig[i].key == this.dbkey) {
            this.editer.value = systemConfig[i].longValue;
        }
    }
};