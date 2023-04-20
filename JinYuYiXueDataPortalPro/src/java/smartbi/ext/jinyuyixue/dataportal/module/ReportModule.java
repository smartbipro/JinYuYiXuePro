package smartbi.ext.jinyuyixue.dataportal.module;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smartbi.catalogtree.CatalogTreeModule;
import smartbi.catalogtree.ICatalogSearchResult;
import smartbi.ext.jinyuyixue.dataportal.util.CacheDataUtil;
import smartbi.ext.jinyuyixue.dataportal.util.CommonUtils;
import smartbi.ext.jinyuyixue.dataportal.util.PageUtil;
import smartbi.index.IDocument;
import smartbi.metadata.MetadataModule;
import smartbi.metadata.assist.CategoryResource;
import smartbi.net.sf.json.JSONArray;
import smartbi.net.sf.json.JSONObject;
import smartbi.usermanager.UserManagerModule;
/**
 * 报表模块实现类
 */
public class ReportModule {
	/**
	 * 日志对象
	 */
    private static final Logger LOG = LoggerFactory.getLogger(ReportModule.class);
    
    /**
     * 资源树对象
     */
    private CatalogTreeModule catalogTreeModule = CatalogTreeModule.getInstance();
    /**
     * 用户管理module对象
     */
    private UserManagerModule userManagerModule = UserManagerModule.getInstance();    
    /**
     * 报表模块对象
     */
    private static ReportModule instance;

    public static ReportModule getInstance() {
        if (instance == null) {
            instance = new ReportModule();
        }
        return instance;
    }
    
    /**
	 * 公共报表模糊查询 列表分页方式呈现
	 * @param types 类型列表
	 * @param alias 别名
	 * @param purview purview
	 * @param pageIndex 页码
	 * @param pageSize 每页大小
	 * @return result 
     * @return
     */
    public JSONObject searchCommonReportLikeAliasByType(List<String> types, String alias,
			String purview, int pageIndex, int pageSize) {
    	JSONObject result = new JSONObject();
    	try {
	    	List<ICatalogSearchResult> list = catalogTreeModule.searchCatalogElementLikeAliasByType(types, alias, purview);
	    	List<ICatalogSearchResult> pageList = PageUtil.startPage(list, pageIndex, pageSize);
	    	JSONArray resultList = CommonUtils.reSetIndexModelAndReportDataListByCatalog(pageList, CacheDataUtil.cacheReportData, true);
	    	result.put("data", resultList);
	    	result.put("success", true);
	    	result.put("pageIndex", pageIndex);
	    	result.put("pageSize", pageSize);
	    	result.put("count", resultList.length());
    	}catch(Exception e) {
    		LOG.error("关联指标数据模型错误：" + e.getMessage(),e);
    		result.put("success", false);
    		result.put("error", e.getMessage());
    	}
    	return result;
    }  
    
    /**
	 * 私有报表模糊查询 列表分页方式呈现
	 * @param types 类型列表
	 * @param alias 别名
	 * @param purview purview
	 * @param pageIndex 页码
	 * @param pageSize 每页大小
	 * @return result 
     * @return
     */    
    public JSONObject searchSelfReportLikeAlias(List<String> types, String alias,
			String purview, int pageIndex, int pageSize) {
    	JSONObject result = new JSONObject();
    	try {
	    	List<ICatalogSearchResult> list = catalogTreeModule.searchCatalogElementLikeAliasByType(types, alias, purview);
	    	List<ICatalogSearchResult> pageList = PageUtil.startPage(list, pageIndex, pageSize);
	    	JSONArray resultList = CommonUtils.reSetIndexModelAndReportDataListByCatalog(pageList, CacheDataUtil.cacheReportData, true);
	    	result.put("data", resultList);
	    	result.put("success", true);
	    	result.put("pageIndex", pageIndex);
	    	result.put("pageSize", pageSize);
	    	result.put("count", resultList.length());
    	}catch(Exception e) {
    		LOG.error("关联指标数据模型错误：" + e.getMessage(),e);
    		result.put("success", false);
    		result.put("error", e.getMessage());
    	}
    	return result;
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
    	JSONObject result = new JSONObject();
    	try {
    		CategoryResource categoryResource = new CategoryResource(); 
    		categoryResource.setId(resId);
    		List<String> filters = new ArrayList<String>();
    		filters.add("SMARTBIX_PAGE");//自助仪表盘
    		filters.add("COMBINED_QUERY");//即席查询
    		//filters.add("SPREADSHEET_REPORT");//电子表格
    		//filters.add("WEB_SPREADSHEET_REPORT");//WEB电子表格    		
    		List<IDocument> list = MetadataModule.getInstance().searchByReferenced(categoryResource, filters);    		
	    	List<IDocument> pageList = PageUtil.startPage(list, pageIndex, pageSize);
	    	JSONArray resultList = CommonUtils.reSetIndexModelAndReportDataListByDoc(pageList, CacheDataUtil.cacheReportData, true);
	    	result.put("data", resultList);
	    	result.put("success", true);
	    	result.put("pageIndex", pageIndex);
	    	result.put("pageSize", pageSize);
	    	result.put("count", resultList.length());
    	}catch(Exception e) {
    		LOG.error("模糊查询报表错误：" + e.getMessage(),e);
	    	result.put("data", new JSONArray());
	    	result.put("success", false);
	    	result.put("pageIndex", pageIndex);
	    	result.put("pageSize", pageSize);  
	    	result.put("count", 0);
    		result.put("error", e.getMessage());
    	}
    	return result;
    }      
    
}
