var ConfigurationPatch = {
    extensionPoints: {
        SystemConfig: {
            configItem: [{
                tabName: "${JYYX_SYSTEM_CONFIG}",
                groupName: "${JYYX_INTERFACE_CONFIG}",
                className: "ext.configItems.IndexYwyConfig",
                itemNumber: 10001
            },{
                tabName: "${JYYX_SYSTEM_CONFIG}",
                groupName: "${JYYX_INTERFACE_CONFIG}",
                className: "ext.configItems.IndexExtendsConfig",
                itemNumber: 10002
            },{
                tabName: "${JYYX_SYSTEM_CONFIG}",
                groupName: "${JYYX_INTERFACE_CONFIG}",
                className: "ext.configItems.ReportDetailPath",
                itemNumber: 10003
            },{
                tabName: "${JYYX_SYSTEM_CONFIG}",
                groupName: "${JYYX_INTERFACE_CONFIG}",
                className: "ext.configItems.DataCacheConfig",
                itemNumber: 10004
            }]
        }
    }
};
