package smartbi.ext.jinyuyixue.dataportal.module;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smartbi.catalogtree.CatalogElement;
import smartbi.catalogtree.CatalogTreeModule;
import smartbi.ext.jinyuyixue.dataportal.util.CacheDataUtil;
import smartbi.ext.jinyuyixue.dataportal.util.CommonUtils;
import smartbi.ext.jinyuyixue.dataportal.util.PageUtil;
import smartbi.index.IDocument;
import smartbi.metadata.MetadataModule;
import smartbi.metadata.assist.CategoryResource;
import smartbi.net.sf.json.JSONArray;
import smartbi.net.sf.json.JSONObject;
import smartbi.user.IDepartment;
import smartbi.usermanager.UserManagerModule;
/**
 * 数据集模块实现类
 */
public class DatasetModule {
	/**
	 * 日志对象
	 */
    private static final Logger LOG = LoggerFactory.getLogger(DatasetModule.class);
    /**
     * 资源树对象
     */
    private CatalogTreeModule catalogTreeModule = CatalogTreeModule.getInstance();  
    
    /**
     * 用户管理module对象
     */
    private UserManagerModule userManagerModule = UserManagerModule.getInstance();
    
    /**
     * 数据集模块对象
     */
    private static DatasetModule instance;

    public static DatasetModule getInstance() {
        if (instance == null) {
            instance = new DatasetModule();
        }
        return instance;
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
    	JSONObject result = new JSONObject();
    	try {
    		CategoryResource categoryResource = new CategoryResource(); 
    		categoryResource.setId(resId);
    		List<String> filters = new ArrayList<String>();
    		filters.add("AUGMENTED_DATASET");//数据模型
    		filters.add("METRICS_MODEL");//指标模型
    		List<IDocument> list = MetadataModule.getInstance().searchByReferenced(categoryResource, filters);    		
	    	List<IDocument> pageList = PageUtil.startPage(list, pageIndex, pageSize);
	    	JSONArray resultList = CommonUtils.reSetIndexModelAndReportDataListByDoc(pageList, CacheDataUtil.cacheDSData, false);
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
