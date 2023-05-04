package smartbi.ext.jinyuyixue.dataportal.module;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smartbi.catalogtree.CatalogElement;
import smartbi.catalogtree.CatalogTreeModule;
import smartbi.ext.jinyuyixue.dataportal.util.CommonUtils;
import smartbi.ext.jinyuyixue.dataportal.util.ConfigUtil;
import smartbi.ext.jinyuyixue.dataportal.util.ConfigUtil.CATALOGTREE_NODE;
import smartbi.ext.jinyuyixue.dataportal.util.ConfigUtil.CREATERESOURCE_URL;
import smartbi.ext.jinyuyixue.dataportal.util.ConfigUtil.REPORT_TYPE;
import smartbi.net.sf.json.JSONArray;
import smartbi.net.sf.json.JSONObject;
import smartbi.usermanager.UserManagerModule;
/**
 * 指标首页模块实现类
 */
public class IndexHomePageModule {
	/**
	 * 日志对象
	 */
    private static final Logger LOG = LoggerFactory.getLogger(IndexHomePageModule.class);
    /**
     * 资源树对象
     */
    private CatalogTreeModule catalogTreeModule = CatalogTreeModule.getInstance(); 
    /**
     * 用户管理module对象
     */
    private UserManagerModule userManagerModule = UserManagerModule.getInstance();
    /**
     * 指标首页模块对象
     */
    private static IndexHomePageModule instance;
    /**
     * 个人空间隐藏的目录节点Map对象
     */
    private Map<String, String> selfNodeHideMap = new HashMap<String, String>();

    public static IndexHomePageModule getInstance() {
        if (instance == null) {
            instance = new IndexHomePageModule();
            instance.setSelfNodeHideMap();
        }
        return instance;
    }
    /**
     * 设置个人空间隐藏的目录节点：数据连接，自助ETL，ETL自动化，作业流，业务主题，数据集，数据挖掘，任务，计划
     */
    private void setSelfNodeHideMap() {
    	LOG.info("初始化个人空间隐藏的目录节点.");
    	String[] selfNodeHideArray = {"SELF_DATACONNECTION","SELF_ETL","SELF_ETL_AUTOMATION","SELF_JOBFLOW","SELF_BUSINESSTHEME","SELF_DATASET",
        		"SELF_DATAMINING","SELF_TASK","SELF_SCHEDULE"};
    	if(selfNodeHideMap.isEmpty()) {
    		for(String nodeStr : selfNodeHideArray) {
    			selfNodeHideMap.put(nodeStr, nodeStr);
    		}
    	}
    }
    /**
     * 数据资产首页，包含常用工具”、"我的报表资源”、“公共报表资源”初始数据
     * @return
     */
    public JSONObject getIndexHomeTreeNodes() {
    	JSONObject result = new JSONObject();  
    	result.put(CATALOGTREE_NODE.COMMON_TOOLS, getCommonTools());
    	result.put(CATALOGTREE_NODE.DEFAULT_TREENODE, getCommonTreeNodeData());
    	result.put(CATALOGTREE_NODE.SELF_TREENODE_DEFAULT, getSeflTreeNodeData());    	
    	return result;
    }
    
    /**
     * 获取公共空间下的子节点
     * @return
     */
    public JSONArray getCommonTreeNodeData() {
    	//获取公共空间下的子节点
    	List<CatalogElement> defaultTreeNode = catalogTreeModule.getChildElements(CATALOGTREE_NODE.DEFAULT_TREENODE); 
    	return reSetList(defaultTreeNode);
    }
    
    /**
     * 获取个人空间下的所有子节点
     * @return
     */
    public JSONArray getSeflTreeNodeData() {
    	//获取我的空间的子节点
    	List<CatalogElement> selfTreeNode = catalogTreeModule.getChildElements(CATALOGTREE_NODE.SELF_TREENODE + userManagerModule.getCurrentUser().getId());
    	List<CatalogElement> selfTreeNode_ = new ArrayList<CatalogElement>();
    	for(CatalogElement element : selfTreeNode) {
    		String elementName = element.getName();
    		if(selfNodeHideMap.get(elementName) != null) {
    			continue;
    		}
    		selfTreeNode_.add(element);
    	}    
    	return reSetList(selfTreeNode_);
    }
    
    /**
     * 重置资源对象列表
     * @param list 资源对象
     * @return
     */
    private JSONArray reSetList(List<CatalogElement> list) {
    	JSONArray result = new JSONArray();
    	for(CatalogElement element : list) {
    		String reportType = element.getType();
    		if(reportType.equals(ConfigUtil.RESTREE_REPORT_TYPE.COMBINED_QUERY) ||
    				reportType.equals(ConfigUtil.RESTREE_REPORT_TYPE.Dashboard) ||
    				reportType.equals(ConfigUtil.RESTREE_REPORT_TYPE.DEFAULT_TREENODE) ||
    				reportType.equals(ConfigUtil.RESTREE_REPORT_TYPE.SELF_TREENODE)) {
    			JSONObject map = CommonUtils.createJsonByElement(element);
    			map = CommonUtils.addDefaultDepartment(map, element);
    			result.put(map);
    		}
    	}
    	return result;
    }
    
    /**
     * 获取常用工具的连接 包含即席查询、透视分析、自助仪表盘
     * @return
     */
    public JSONArray getCommonTools() {
    	JSONArray result = new JSONArray();
    	result.put(CommonUtils.createJson(REPORT_TYPE.COMBINEDQUERY,"即席查询",CREATERESOURCE_URL.COMBINEDQUERY_URL));
    	result.put(CommonUtils.createJson(REPORT_TYPE.INSIGHT,"透视分析",CREATERESOURCE_URL.INSIGHT_URL));
    	result.put(CommonUtils.createJson(REPORT_TYPE.DASHBOARD,"仪表盘",CREATERESOURCE_URL.DASHBOARD_URL));
    	return result;
    }
    
    /**
     * 获取资源下的子节点
     * @param resId
     * @return
     */
    public JSONArray getChildElements(String resId) {
    	List<CatalogElement> list = catalogTreeModule.getChildElements(resId);
    	return reSetList(list);
    }
    
    /**
     * 获取即系查询、透视分析、可视化查询新建的url
     * @return
     */
    public JSONObject getReportCreateUrl(){
    	JSONObject result = new JSONObject();
    	result.put(REPORT_TYPE.COMBINEDQUERY, CREATERESOURCE_URL.COMBINEDQUERY_URL);
    	result.put(REPORT_TYPE.INSIGHT, CREATERESOURCE_URL.INSIGHT_URL);
    	result.put(REPORT_TYPE.DASHBOARD, CREATERESOURCE_URL.DASHBOARD_URL);    	
    	return result;
    }
    
    /**
     * 删除资源节点
     * @param resId
     * @return
     */
    public boolean deleteTreeNodeByResId(String resId) {
    	try {
    		catalogTreeModule.deleteCatalogElement(resId);
    		return true;
    	}catch(Exception e) {
    		LOG.info("删除节点失败.");
    		return false;
    	}
    }
    
}
