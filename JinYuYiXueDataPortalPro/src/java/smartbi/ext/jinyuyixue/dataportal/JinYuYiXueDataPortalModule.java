package smartbi.ext.jinyuyixue.dataportal;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smartbi.catalogtree.CatalogElement;
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
    public List<CatalogElement> getChildElements(String resId) {
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
	 * 指标模糊查询 列表分页方式呈现
	 * @param alias 别名
	 * @param pageIndex 页码
	 * @param pageSize 每页大小
	 * @return result 
     * @return
     */    
    public JSONObject searchIndexDataLikeAlias(String alias, int pageIndex, int pageSize) {
    	List<String> types = new ArrayList<String>();
    	types.add("MT_ATOM_METRICS");//原子指标
    	types.add("MT_VIRTUAL_METRICS");//计算指标
    	return indexModule.searchetricsIdLikeAliasByType(types, alias, ConfigUtil.PURVIEWTYPE.REF, pageIndex, pageSize);    			
    }    
    
    /**
     * 获取指标详情信息
     * @param resId 指标资源id
     * @return
     */
    public JSONObject getIndexDetailInfo(String resId) {
    	return indexModule.getIndexDetailInfo(resId); 
    }

	
    /**
	 * 根据指标id获取影响性的模型列表数据
	 * @param resId     资源id
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
	 * 公共报表模糊查询 列表分页方式呈现
	 * @param alias 别名
	 * @param pageIndex 页码
	 * @param pageSize 每页大小
	 * @return result 
     * @return
     */    
    public JSONObject searchCommonReportLikeAlias(String alias, int pageIndex, int pageSize) {
    	List<String> types = new ArrayList<String>();
    	types.add("SMARTBIX_PAGE");//自助仪表盘
    	types.add("COMBINED_QUERY");//即席查询
    	//types.add("SPREADSHEET_REPORT");//电子表格
    	//types.add("WEB_SPREADSHEET_REPORT");//WEB电子表格
    	return reportModule.searchCommonReportLikeAliasByType(types, alias, ConfigUtil.PURVIEWTYPE.REF, pageIndex, pageSize);    			
    }
    
    /**
	 * 私有报表模糊查询 列表分页方式呈现
	 * @param alias 别名
	 * @param pageIndex 页码
	 * @param pageSize 每页大小
	 * @return result 
     * @return
     */    
    public JSONObject searchSelfReportLikeAlias(String alias, int pageIndex, int pageSize) {
    	List<String> types = new ArrayList<String>();
    	types.add("SMARTBIX_PAGE");//自助仪表盘
    	types.add("COMBINED_QUERY");//即席查询
    	//types.add("SPREADSHEET_REPORT");//电子表格
    	//types.add("WEB_SPREADSHEET_REPORT");//WEB电子表格
    	return reportModule.searchSelfReportLikeAlias(types, alias, ConfigUtil.PURVIEWTYPE.REF, pageIndex, pageSize);    			
    }    
    
}
