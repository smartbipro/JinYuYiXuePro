package smartbi.ext.jinyuyixue.dataportal;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smartbi.catalogtree.CatalogElement;
import smartbi.catalogtree.PurviewType;
import smartbi.config.SystemConfigService;
import smartbi.ext.jinyuyixue.dataportal.module.DatasetModule;
import smartbi.ext.jinyuyixue.dataportal.module.IndexHomePageModule;
import smartbi.ext.jinyuyixue.dataportal.module.IndexModule;
import smartbi.ext.jinyuyixue.dataportal.module.ReportModule;
import smartbi.ext.jinyuyixue.dataportal.repository.IndexClickData;
import smartbi.ext.jinyuyixue.dataportal.repository.IndexClickDataDetail;
import smartbi.ext.jinyuyixue.dataportal.util.CacheDataUtil;
import smartbi.ext.jinyuyixue.dataportal.util.CommonUtils;
import smartbi.ext.jinyuyixue.dataportal.util.ConfigUtil;
import smartbi.framework.IModule;
import smartbi.net.sf.json.JSONArray;
import smartbi.net.sf.json.JSONObject;
import smartbi.repository.IDAOModule;
import smartbix.augmenteddataset.util.StringUtil;

/**
 * 数据门户扩展包Module实现类
 */
public class JinYuYiXueDataPortalModule implements IModule {
	/**
	 * 日志对象
	 */
    private static final Logger LOG = LoggerFactory.getLogger(JinYuYiXueDataPortalModule.class);
    /**
     * modual对象
     */
    private static JinYuYiXueDataPortalModule instance;
    /**
     * 数据资产首页实现
     */
    private IndexHomePageModule indexHomePageModule = IndexHomePageModule.getInstance();
    /**
     * 指标模块相关实现
     */
    private IndexModule indexModule = IndexModule.getInstance();
    /**
     * 数据集模块相关实现
     */
    private DatasetModule datasetModule = DatasetModule.getInstance();
    /**
     * 报表模块相关实现
     */
    private ReportModule reportModule = ReportModule.getInstance();
    
    /**
     * dao对象
     */
    private IDAOModule daoModule;

    public static JinYuYiXueDataPortalModule getInstance() {
        if (instance == null) {
            instance = new JinYuYiXueDataPortalModule();
        }
        return instance;
    }
    
	public IDAOModule getDaoModule() {
		return daoModule;
	}

	public void setDaoModule(IDAOModule daoModule) {
		this.daoModule = daoModule;
	
	}     

    @Override
    public void activate() {
        LOG.info("JinYuYiXueDataPortalModule is running");
        daoModule.addPOJOClass(IndexClickData.class);
        daoModule.addPOJOClass(IndexClickDataDetail.class);
    }
    
    /**
     * 清空指标查询数据缓存
     * @return
     */
    public String clearCacheIndexData() {
    	return CacheDataUtil.clearCacheIndexData();	
    }
    
    /**
     * 数据资产首页，包含“常用工具”、"我的报表资源”、“公共报表资源”初始数据
     * @return
     */
    public JSONObject getIndexHomeTreeNodes() {
    	return indexHomePageModule.getIndexHomeTreeNodes();
    }
    
    /**
     * 获取常用工具下的请求地址
     * @return
     */
    public JSONArray getCommonTools() {
    	return indexHomePageModule.getCommonTools();
    }
    
    /**
     * 获取公共空间下的子节点
     * @return
     */
    public JSONArray getCommonTreeNodeData() {
    	return indexHomePageModule.getCommonTreeNodeData();
    }    
    
    /**
     * 获取个人空间下的所有子节点
     * @return
     */
    public JSONArray getSeflTreeNodeData() {
    	return indexHomePageModule.getSeflTreeNodeData();
    }
    
    /**
     * 获取资源下的子节点
     * @param resId 资源id
     * @return
     */
    public JSONArray getChildElements(String resId) {
    	return indexHomePageModule.getChildElements(resId);
    }
    
    /**
     * 删除资源节点
     * @param resId 资源id
     * @return
     */
    public boolean deleteTreeNodeByResId(String resId) {
    	return indexHomePageModule.deleteTreeNodeByResId(resId);
    }
    
    /**
     * 获取即系查询、透视分析、可视化查询新建的url
     * @return
     */
    public JSONObject getReportCreateUrl(){
    	return indexHomePageModule.getReportCreateUrl();
    }
    
    /**
     * 指标查询数据埋点计算
     * @param indexId    指标id
     * @param indexName  指标名称
     */
    public void indexAddSearchNumber(String indexId, String indexName) {
    	indexModule.indexAddSearchNumber(indexId, indexName);
    }
    
    /**
     * 指标点击数据埋点计算
     * @param indexId    指标id
     * @param indexName  指标名称
     */    
    public void indexAddClickNumber(String indexId, String indexName) {
    	indexModule.indexAddClickNumber(indexId, indexName);
    }
    
    /**
     * 判断资源当前用户是否有引用权限
     * @param resId 资源id
     * @return
     */
    public boolean hasRefByResId(String resId) {
    	return indexModule.hasRefByResId(resId);
    }
    
    /**
     * 判断当前用户是否有资源的 相关权限
     * @param resId 资源id
     * @param purViewType 权限类型
     * @return
     */
    public boolean hasPurViewByResIdType(String resId, String purViewType) {
    	return CommonUtils.hasPurViewByResIdType(resId, purViewType);
    }
    /**
     * 判断当前用户是否有即席查询、透视分析、自助仪表盘创建的操作权限
     * @return
     */
    public JSONObject getReportFunctionByCurrentUser(){
    	return CommonUtils.getReportFunctionByCurrentUser();
    }
    
	/**
	 * 获取指标点击次数数据	
	 * @param resId 资源id
	 * @return
	 */
    public JSONObject getIndexClickDataById(String resId) {
    	IndexClickData indexClickData =  indexModule.getIndexClickDataById(resId);
    	JSONObject result = new JSONObject();
    	if(indexClickData != null) {
    		result.put("indexId", indexClickData.getIndexId());
    		result.put("indexName", indexClickData.getIndexName());
    		result.put("clickNum", indexClickData.getClickNum());
    		result.put("searchNum", indexClickData.getSearchNum());
    		result.put("createDateTime", indexClickData.getCreateDateTime());
    		result.put("updateDateTime", indexClickData.getUpdateDateTime());
    	}
    	return result;
    }
    
    /**
	 * 指标模糊查询 列表分页方式呈现
	 * @param alias 别名
	 * @param pageIndex 页码
	 * @param pageSize 每页大小
	 * @param pathResIds 业务域资源id
	 * @return result 
     * @return
     */    
    public JSONObject searchIndexDataLikeAlias(String alias, int pageIndex, int pageSize, String pathResIds) {
    	List<String> types = new ArrayList<String>();
    	types.add("MT_ATOM_METRICS");//原子指标
    	types.add("MT_VIRTUAL_METRICS");//计算指标
    	if(StringUtil.isNullOrEmpty(pathResIds)) {
    		return indexModule.searchetricsIdLikeAliasByType(types, alias, ConfigUtil.PURVIEWTYPE.REF, pageIndex, pageSize);
    	} else {
    		return indexModule.searchetricsIdLikeAliasByTypeAndPath(pathResIds, alias, PurviewType.REF, pageIndex, pageSize);
    	}
    }    
    
    /**
	 * 根据指标id获取影响性的模型列表数据
	 * @param resId     模型的资源id
	 * @param pageIndex 页码
	 * @param pageSize 每页大小
	 * @return result 
     * @return
     */
    public JSONObject getDataModelByIndexResId(String resId, int pageIndex, int pageSize) {
    	return datasetModule.getDataModelByIndexResId(resId, pageIndex, pageSize);
    }
	
    /**
	 * 根据指标id获取影响性的报表列表数据
	 * @param resId     资源id
	 * @param pageIndex 页码
	 * @param pageSize 每页大小
	 * @return result 
     * @return
     */
    public JSONObject getReportByIndexResId(String resId, int pageIndex, int pageSize) {
    	return reportModule.getReportByIndexResId(resId, pageIndex, pageSize);
    }
    
    /**
	 * 根据type 搜索 ICatalogSearchResult
	 * @param types 类型列表
	 * @param alias 别名
	 * @param purview purview
	 * @param pageIndex 页码
	 * @param pageSize 每页大小
	 * @return result 
     * @return
     */
    public JSONObject searchIndexModelDataLikeAlias(String alias, int pageIndex, int pageSize) {
    	List<String> types = new ArrayList<String>();
    	types.add("MT_MODEL");//指标模型
    	//types.add("AUGMENTED_DATASET");//数据模型
    	return datasetModule.searchIndexModelDataLikeAlias(types, alias, ConfigUtil.PURVIEWTYPE.REF, pageIndex, pageSize);
    }    
    
    /**
	 * 公共报表模糊查询 列表分页方式呈现
	 * @param alias 别名
	 * @param pageIndex 页码
	 * @param pageSize 每页大小
	 * @return result 
     * @return
     */    
//    public JSONObject searchCommonReportLikeAlias(String alias, int pageIndex, int pageSize) {
//    	List<String> types = new ArrayList<String>();
//    	types.add("SMARTBIX_PAGE");//自助仪表盘
//    	types.add("COMBINED_QUERY");//即席查询
//    	//types.add("SPREADSHEET_REPORT");//电子表格
//    	//types.add("WEB_SPREADSHEET_REPORT");//WEB电子表格    	    	
//    	return reportModule.searchCommonReportLikeAliasByType(types, alias, ConfigUtil.PURVIEWTYPE.REF, pageIndex, pageSize);    			
//    }
    
    /**
	 * 私有报表模糊查询 列表分页方式呈现
	 * @param alias 别名
	 * @param pageIndex 页码
	 * @param pageSize 每页大小
	 * @return result 
     * @return
     */    
//    public JSONObject searchSelfReportLikeAlias(String alias, int pageIndex, int pageSize) {
//    	List<String> types = new ArrayList<String>();
//    	types.add("SMARTBIX_PAGE");//自助仪表盘
//    	types.add("COMBINED_QUERY");//即席查询
//    	//types.add("SPREADSHEET_REPORT");//电子表格
//    	//types.add("WEB_SPREADSHEET_REPORT");//WEB电子表格
//    	return reportModule.searchSelfReportLikeAlias(types, alias, ConfigUtil.PURVIEWTYPE.REF, pageIndex, pageSize);    			
//    }
    
    /**
	 * 指标模糊查询 列表分页方式呈现
	 * @param alias 别名
	 * @param pageIndex 页码
	 * @param pageSize 每页大小
	 * @param reportType 类型 当传入为空时，不区分公共报表和个人报表 "person":个人报表 "common":公共报表
	 * @return result 
     * @return
     */    
    public JSONObject searchReportDataLikeAlias(String alias, int pageIndex, int pageSize, String reportType) {
    	List<String> types = new ArrayList<String>();
    	types.add("SMARTBIX_PAGE");//自助仪表盘
    	types.add("COMBINED_QUERY");//即席查询    	
    	return reportModule.searchReportDataLikeAlias(types, alias, reportType, ConfigUtil.PURVIEWTYPE.REF, pageIndex, pageSize);
    }
    
    
    /**
     * 即席查询/透视分析根据报表id获取表头信息
     * @param resId
     * @param type
     * @return
     */
    public JSONArray getReportMultiHeader(String resId, String type) {
    	if("COMBINED_QUERY".equals(type)) {
    		return getCombinedMultiHeader(resId);
    	} else if("INSIGHT".equals(type)) {
    		return getInsightMultiHeader(resId);
    	}
    	return null;
    }
    
    /**
     * 透视分析根据报表id获取表头信息
     * @param resId
     * @return
     */
    public JSONArray getInsightMultiHeader(String resId) {
    	return reportModule.getInsightMultiHeader(resId);
    }
    
    /**
     * 即席查询根据报表id获取表头信息
     * @param resId
     * @return
     */
    public JSONArray getCombinedMultiHeader(String resId) {
    	return reportModule.getCombinedMultiHeader(resId);
    }
    
    
    /**
     * 指标业务域系统选项配置
     * @return
     */
    public JSONArray getIndexYwyConfig() {
    	String indexYwyConfig = getSystemConfigLongValue("JYYX_INDEX_YWY_CONFIG");
    	return JSONArray.fromString(indexYwyConfig);
    }
    
    /**
     * 获取指标详情信息  如后续指标详情修改了，该方法也需要做相应调整
     * @param resId 指标资源id
     * @return
     */    
    public JSONArray getIndexDetailByResId(String resId) {
    	return indexModule.getIndexDetailByResId(resId);
    }
    
    /**
     * 获取系统选项长字符串
     * @param key 键值
     * @return
     */
    public String getSystemConfigLongValue(String key) {
    	return SystemConfigService.getInstance().getLongValue(key);    	
    }
    
    
    /**
     * 获取系统选项字符串
     * @param key 键值
     * @return
     */
    public String getSystemConfigValue(String key) {
    	return SystemConfigService.getInstance().getValue(key);    	
    }
    
}
