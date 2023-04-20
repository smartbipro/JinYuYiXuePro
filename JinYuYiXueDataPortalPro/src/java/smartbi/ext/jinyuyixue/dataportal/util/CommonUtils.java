package smartbi.ext.jinyuyixue.dataportal.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import smartbi.catalogtree.CatalogElement;
import smartbi.catalogtree.CatalogTreeModule;
import smartbi.catalogtree.ICatalogSearchResult;
import smartbi.ext.jinyuyixue.dataportal.repository.IndexClickDataDAO;
import smartbi.ext.jinyuyixue.dataportal.util.ConfigUtil.FUNCTION_DATA;
import smartbi.ext.jinyuyixue.dataportal.util.ConfigUtil.REPORT_TYPE;
import smartbi.index.IDocument;
import smartbi.net.sf.json.JSONArray;
import smartbi.net.sf.json.JSONObject;
import smartbi.user.IDepartment;
import smartbi.usermanager.UserManagerModule;

/**
 * 工具类
 */
public class CommonUtils {
	
    /**
     * 用户管理module对象
     */
    private static UserManagerModule userManagerModule = UserManagerModule.getInstance();
    
    /**
     * 资源树对象
     */
    private static CatalogTreeModule catalogTreeModule = CatalogTreeModule.getInstance();  
    
    /**
     * 指标点击查询数据埋点数据dao实现类
     */
    private static IndexClickDataDAO indexClickDataDao = IndexClickDataDAO.getInstance();   
    
    /**
     * 判断当前用户是否有即席查询、透视分析、自助仪表盘创建的操作权限
     * @return
     */
    public static JSONObject getReportFunctionByCurrentUser(){
    	JSONObject result = new JSONObject();
    	result.put(REPORT_TYPE.COMBINEDQUERY, 
    			userManagerModule.isCurUserFuncTypeAccessible(FUNCTION_DATA.CUSTOM_DISPLAYCUSTOM_COMBINEDQUERY));
    	result.put(REPORT_TYPE.INSIGHT, 
    			userManagerModule.isCurUserFuncTypeAccessible(FUNCTION_DATA.CUSTOM_DISPLAYCUSTOM_INSIGHT));
    	result.put(REPORT_TYPE.DASHBOARD, 
    			userManagerModule.isCurUserFuncTypeAccessible(FUNCTION_DATA.XDASHBOARD_CREATE));
    	return result;
    }    
	
    /**
     * 创建json
     * @param type  报表类型  
     * @param text  报名说明
     * @param url   报表的url
     * @return
     */
    public static JSONObject createJson(String type, String text, String url) {
    	JSONObject result = new JSONObject();
    	result.put("type", type);
    	result.put("alias", text);
    	result.put("url", url);
    	return result;
    }
    

    /**
     * 根据资源节点创建json对象
     * @param element
     * @return
     */
    public static JSONObject createJsonByElement(CatalogElement element) {
    	JSONObject result = new JSONObject();
    	if(element != null) {
    		result.put("id", element.getId());
    		result.put("name", element.getName());
    		result.put("alias", element.getAlias());
    		result.put("desc", element.getDesc());
    		result.put("type", element.getType());
    		result.put("createdDate", element.getCreatedDate());
    		result.put("lastModifiedDate", element.getLastModifiedDate());
    		result.put("authorUserid", element.getAuthorUserid());
    		result.put("author", element.getAuthor());
    	}
    	return result;
    }
    
    /**
     * 添加指标点击、查询次数信息
     * @param json
     * @param element
     * @return
     */    
    public static JSONObject addIndexPath(JSONObject json, CatalogElement element) {
    	json.put("path", catalogTreeModule.getCatalogElementFullPath(element.getId()));
    	return json;
    } 
    
    /**
     * 添加资源授权信息
     * @param json
     * @param element
     * @return
     */    
    public static JSONObject addIsAuthorized(JSONObject json, CatalogElement element) {
    	json.put("isAuthorized", CommonUtils.hasPurViewByResIdType(element.getId(), ConfigUtil.PURVIEWTYPE.READ));
    	return json;
    }    
    
    /**
     * 判断当前用户是否有资源的 相关权限
     * @param resId 资源id
     * @param purViewType 权限类型
     * @return
     */
    public static boolean hasPurViewByResIdType(String resId, String purViewType) {
    	return catalogTreeModule.isCatalogElementAccessible(resId, purViewType);
    }    
    
    /**
     * 是否可以新建报表资源
     * @param json
     * @param element
     * @return
     */
    public static JSONObject addOpAuthorized(JSONObject json, JSONObject opAuthorized) {
    	json.put("isCreateCombinedquery", false);
    	json.put("isCreateDashboard", false);  
    	json.put("isCreateInsight", false); 
    	if(!json.optBoolean("isAuthorized", false)) {
	    	json.put("isCreateCombinedquery", opAuthorized.optBoolean(REPORT_TYPE.COMBINEDQUERY, false));
	    	json.put("isCreateDashboard", opAuthorized.optBoolean(REPORT_TYPE.DASHBOARD, false));
	    	json.put("isCreateInsight", opAuthorized.optBoolean(REPORT_TYPE.INSIGHT, false));
    	}
    	return json;
    }     
       
    /**
     * 重置数据模型、报表数据列表  影响性分析
     * @param list  IDocument列表
     * @param cacheData 缓存数据
     * @return
     */
	public static JSONArray reSetIndexModelAndReportDataListByDoc(List<IDocument> pageList,
			Map<String, JSONObject> cacheData, boolean isReportType) {
    	JSONArray result = new JSONArray();
    	//是否有创建报表的权限
    	JSONObject opAuthorized = CommonUtils.getReportFunctionByCurrentUser();    	
    	for(IDocument item : pageList) {    	
    		String resId = item.getId();
			JSONObject cacheRec = cacheData.get(resId);
    		if(cacheRec != null) {
    			result.put(cacheRec);
    			continue;
    		}
    		CatalogElement element = catalogTreeModule.getCatalogElementById(resId);
    		JSONObject map = resetObject(element, cacheData, isReportType, opAuthorized);
    		//加载如返回列表中    		
    		result.put(map);
    	}
    	return result;
	}    
	
    /**
     * 重置数据模型、报表数据列表 资源查询
     * @param list  ICatalogSearchResult列表
     * @param cacheData 缓存数据
     * @param isReportType 是否报表
     * @return
     */
	public static JSONArray reSetIndexModelAndReportDataListByCatalog(List<ICatalogSearchResult> list,
			Map<String, JSONObject> cacheData, boolean isReportType) {
    	JSONArray result = new JSONArray();
    	//是否有创建报表的权限
    	JSONObject opAuthorized = CommonUtils.getReportFunctionByCurrentUser();
    	for(ICatalogSearchResult item : list) {    		
    		CatalogElement element = (CatalogElement) item.getCatalogElement();
    		String resId = element.getId();
    		JSONObject cacheRec = cacheData.get(resId);
    		if(cacheRec != null) {
        		//添加指标点击、搜索次数
    			result.put(cacheRec);
    			continue;
    		}
    		JSONObject map = resetObject(element, cacheData, isReportType, opAuthorized);
    		//加载如返回列表中    		
    		result.put(map);
    	}
    	return result;		
	} 
	
	/**
	 * 重构数据集、数据模型、报表输出字段
	 * @param element
	 * @param cacheData
	 * @param isReportType
	 * @param opAuthorized
	 * @return
	 */
	private static JSONObject resetObject(CatalogElement element, Map<String, JSONObject> cacheData, boolean isReportType,
			JSONObject opAuthorized ) {
		String resId = element.getId();
		JSONObject map = CommonUtils.createJsonByElement(element);
		//添加路径
		map = addIndexPath(map, element);
		//添加授权
		map = addIsAuthorized(map, element);
		//添加默认部门
		map = addDefaultDepartment(map, element);  
		//非报表时需要新增 是否可以创建即席查询、自助仪表盘权限
		if(!isReportType) {
    		//是否有创建即席查询、自助仪表盘权限
    		map = CommonUtils.addOpAuthorized(map, opAuthorized);
		}
		//将指标模型、报表数据进行缓存
		cacheData.put(resId, map);	
		return map;
	}
	
    /**
     * 添加默认部门信息
     * @param json
     * @param element
     * @return
     */
    private static JSONObject addDefaultDepartment(JSONObject json, CatalogElement element) {
		IDepartment department = userManagerModule.getDefaultDepartment(element.getAuthorUserid());
		json.put("defaultDept", "");
		if(department != null) {
			json.put("defaultDept", department.getAlias());
		}
    	return json;
    }	
}