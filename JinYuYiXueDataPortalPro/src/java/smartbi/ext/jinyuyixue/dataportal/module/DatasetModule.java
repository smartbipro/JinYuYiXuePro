package smartbi.ext.jinyuyixue.dataportal.module;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smartbi.catalogtree.CatalogElement;
import smartbi.catalogtree.CatalogTreeModule;
import smartbi.catalogtree.ICatalogSearchResult;
import smartbi.ext.jinyuyixue.dataportal.util.CacheDataUtil;
import smartbi.ext.jinyuyixue.dataportal.util.CommonUtils;
import smartbi.ext.jinyuyixue.dataportal.util.PageUtil;
import smartbi.net.sf.json.JSONArray;
import smartbi.net.sf.json.JSONObject;
import smartbi.usermanager.UserManagerModule;
import smartbi.util.StringUtil;
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
	 * @param resIds     资源id
	 * @param pageIndex 页码
	 * @param pageSize 每页大小
	 * @return result 
     * @return
     */
    public JSONObject getDataModelByIndexResId(String resIds, int pageIndex, int pageSize) {
    	try {
    		if(StringUtil.isNullOrEmpty(resIds)) {
    			return CommonUtils.getSuccessData(new JSONArray(), pageIndex, pageSize, 0);
    		}
    		boolean isOnCache = CacheDataUtil.isOnCache();
    		//缓存数据
    		Map<String, JSONObject> cacheDSData = CacheDataUtil.cacheDSData;
    		//是否有创建报表的权限
        	JSONObject opAuthorized = CommonUtils.getReportFunctionByCurrentUser();
        	//返回的列表数据
        	//JSONArray data = new JSONArray();
        	List<JSONObject> list = new ArrayList<JSONObject>();
        	//资源id数组
    		String[] resIdArry = resIds.split(",");
    		for(String resId : resIdArry) {
        		if(cacheDSData != null && isOnCache) {
        			JSONObject cacheDataModelRec = cacheDSData.get(resId);
            		if(cacheDataModelRec != null) {
            			cacheDataModelRec = CommonUtils.addOpAuthorized(cacheDataModelRec, opAuthorized);    			
            			cacheDSData.put(resId, cacheDataModelRec);
            			//data.put(cacheDataModelRec);
            			list.add(cacheDataModelRec);
            			continue;
            		}    			
        		}
        		
        		CatalogElement element = catalogTreeModule.getCatalogElementById(resId);
        		if(element == null) {
        			continue;
        		}
        		JSONObject map = CommonUtils.createJsonByElement(element);
        		//添加默认部门
        		map = CommonUtils.addDefaultDepartment(map, element);      		
        		//添加指标路径
        		map = CommonUtils.addIndexPath(map, element);
        		//添加授权
        		map = CommonUtils.addIsAuthorized(map, element);
        		//是否有创建即席查询、自助仪表盘权限
        		map = CommonUtils.addOpAuthorized(map, opAuthorized);
        		//添加指标模型id和数据模型id
        		map = addModelData(map, resId);
        		//加载如返回列表中    		
        		//data.put(map);  
        		list.add(map);
        		if(cacheDSData != null && isOnCache) {
        			cacheDSData.put(resId, map);
        		}    			
    		}
    		//分页处理
	    	List<JSONObject> pageList = new ArrayList<JSONObject>();
    		if(list.size() >= (pageIndex-1) * pageSize) {
    			pageList = PageUtil.startPage(list, pageIndex, pageSize);
    		}
    		//转换处理
    		JSONArray data = CommonUtils.listToJsonArray(pageList);
	    	return CommonUtils.getSuccessData(data, pageIndex, pageSize, data.length());
    	}catch(Exception e) {
    		LOG.error("getDataModelByIndexResId error：" + e.getMessage(),e);
    		return CommonUtils.getFailData(pageIndex, pageSize, "getDataModelByIndexResId error：" + e.getMessage());
    	}
    } 
    
    /**
     * 增加指标模型id和数据模型id
     * @param json       目标json对象
     * @param modelId    指标模型id
     * @return
     */
    private JSONObject addModelData(JSONObject json, String modelId) { 
		String dataModelId = CommonUtils.getDataModelIdByModelId(modelId);
		json.put("modelId", modelId);
		json.put("dataModelId", dataModelId);
    	return json;
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
    public JSONObject searchIndexModelDataLikeAlias(List<String> types, String alias,
			String purview, int pageIndex, int pageSize) {
    	try {
	    	List<ICatalogSearchResult> list = catalogTreeModule.searchCatalogElementLikeAliasByType(types, alias, purview);
	    	
	    	List<ICatalogSearchResult> pageList = new ArrayList<ICatalogSearchResult>();
    		if(list.size() >= (pageIndex-1) * pageSize) {
    			pageList = PageUtil.startPage(list, pageIndex, pageSize);
    		}
	    	JSONArray resultList = reSetIndexDataList(pageList, CacheDataUtil.cacheDSData);
	    	return CommonUtils.getSuccessData(resultList, pageIndex, pageSize, list.size());
    	}catch(Exception e) {
    		LOG.error("searchIndexModelDataLikeAlias error：" + e.getMessage(),e);
    		return CommonUtils.getFailData(pageIndex, pageSize, "searchIndexModelDataLikeAlias error：" + e.getMessage());
    	}
    }
    
    /////////////////////数据集组合//////////////////////////
    /**
     * 重置数据集资源列表数据
     * @param list 中间的列表对象
     * @param cacheDSData 数据模型缓存对象
     * @return
     */
    public JSONArray reSetIndexDataList(List<ICatalogSearchResult> list, Map<String, JSONObject> cacheDSData){
    	boolean isOnCache = CacheDataUtil.isOnCache();
    	JSONArray result = new JSONArray();
    	//是否有创建报表的权限
    	JSONObject opAuthorized = CommonUtils.getReportFunctionByCurrentUser();
    	for(ICatalogSearchResult item : list) {    		
    		CatalogElement element = (CatalogElement) item.getCatalogElement();
    		String resId = element.getId();
    		JSONObject cacheIndexRec = cacheDSData.get(resId);
    		if(cacheIndexRec != null && isOnCache) {
    			//是否有创建即席查询、自助仪表盘权限
    			cacheIndexRec = CommonUtils.addOpAuthorized(cacheIndexRec, opAuthorized);  
    			cacheDSData.put(resId, cacheIndexRec);
    			result.put(cacheIndexRec);
    			continue;
    		}
    		JSONObject map = CommonUtils.createJsonByElement(element);
    		//创建人部门
    		map = CommonUtils.addDefaultDepartment(map, element);    		
    		//添加指标路径
    		map = CommonUtils.addIndexPath(map, element);
    		//添加授权
    		map = CommonUtils.addIsAuthorized(map, element);
    		//是否有创建即席查询、自助仪表盘权限
    		map = CommonUtils.addOpAuthorized(map, opAuthorized);
    		//添加指标模型id和数据模型id
    		map = addModelData(map, resId);  
    		//加载如返回列表中    		
    		result.put(map);
    		//将指标数据进行缓存
    		if(isOnCache) {
    			cacheDSData.put(resId, map);
    		}
    	}
    	return result;
    }      
    
    
}
