import SmartBIExt from 'smartbi-ext'

let {
    DashModule: {
        DashEventEmum: {
            DASHBOARD_ON_SAVE_DIALOG_INIT
        },
        BaseDashExtender
    },
    AugmentModule: {
        AugmentEventEmum: {
            AUGMENTEDDATASET_ON_TOOLBAR_INIT
        },
        BaseAugmentExtender
    },
    ModelQueryModule: {
        ModelQueryEventEnum: {
            MODEL_QUERY_ON_SAVE_DIALOG_INIT
        },
        BaseModelQueryExtender
    },
    Utils: {
        ExtensionUtil: {rmi}
    }
} = SmartBIExt

class DashExtenter extends BaseDashExtender {
    async install() {
		debugger;
        // 获取配置的参数列表
        let resp = await rmi('XinSenZhiNengModule', 'isIntegration');
        console.info("获取当前参数：" + resp.result);
        if (resp.result == null || resp.result == "") {
            return;
        }
        let params = JSON.parse(resp.result);

        //限制仪表盘保存目录
        this.on(DASHBOARD_ON_SAVE_DIALOG_INIT, (iSaveDialog) => {
            if (params && params.isIntegration && params.isIntegration == "true" && params.reportDefaultPath) {
                let reportDefaultPath = params.reportDefaultPath;
                iSaveDialog.setRootNodeId(reportDefaultPath);
                // 删除所有操作按钮
                iSaveDialog.setOperateButtonFilter(item => {
                    return false
                })
            }
        })

        //限制即席查询保存目录
        this.on(MODEL_QUERY_ON_SAVE_DIALOG_INIT, (iSaveDialog) => {
            if (params && params.isIntegration && params.isIntegration == "true" && params.reportDefaultPath) {
                let reportDefaultPath = params.reportDefaultPath;
                iSaveDialog.setRootNodeId(reportDefaultPath);
                // 删除所有操作按钮
                iSaveDialog.setOperateButtonFilter(item => {
                    return false
                })
            }
        })

        //屏蔽数据模型另存为操作
        this.on(AUGMENTEDDATASET_ON_TOOLBAR_INIT, (iToolbar) => {
            if (params && params.isIntegration == "true" && params.forbidDataSaveAs == "true") {
                // 获取按钮配置
                let btns = iToolbar.getButtons();
                for (let i = 0; i < btns.length; i++) {
                    if (btns[i].action === 'SAVE') {
                        // 移除save的子菜单
                        btns[i].children = [];
                        // 去掉下拉图标
                        btns[i].type = 'icon';
                    }
                }
            }
        })
    }
}

export default DashExtenter