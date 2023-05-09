package smartbi.ext.jinyuyixue.dataportal.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smartbi.SmartbiException;
import smartbi.catalogtree.CatalogElement;
import smartbi.catalogtree.CatalogTreeErrorCode;
import smartbi.catalogtree.CatalogTreeModule;
import smartbi.catalogtree.ICatalogSearchResult;
import smartbi.catalogtree.ResourceTreeNode;
import smartbi.catalogtree.ResourceTreeNodeDAO;
import smartbi.ext.jinyuyixue.dataportal.repository.IndexClickDataDAO;
import smartbi.ext.jinyuyixue.dataportal.util.ConfigUtil.FUNCTION_DATA;
import smartbi.ext.jinyuyixue.dataportal.util.ConfigUtil.REPORT_TYPE;
import smartbi.index.IDocument;
import smartbi.net.sf.json.JSONArray;
import smartbi.net.sf.json.JSONObject;
import smartbi.scheduletask.ScheduleTaskModule;
import smartbi.sdk.ClientConnector;
import smartbi.sdk.ClientConnectorFactory;
import smartbi.user.IDepartment;
import smartbi.usermanager.UserManagerModule;
import smartbi.util.StringUtil;
/**
 * 工具类
 */
public class CommonUtils {
	/**
	 * 日志对象
	 */
    private static final Logger LOG = LoggerFactory.getLogger(CommonUtils.class);
	
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
    	if(json.optBoolean("isAuthorized", false)) {
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
		boolean isOnCache = CacheDataUtil.isOnCache();
    	JSONArray result = new JSONArray();
    	//是否有创建报表的权限
    	JSONObject opAuthorized = CommonUtils.getReportFunctionByCurrentUser();
    	for(ICatalogSearchResult item : list) {    		
    		CatalogElement element = (CatalogElement) item.getCatalogElement();
    		String resId = element.getId();
    		JSONObject cacheRec = cacheData.get(resId);
    		if(cacheRec != null && isOnCache) {
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
		if(CacheDataUtil.isOnCache()) {
			cacheData.put(resId, map);
		}
		return map;
	}
	
    /**
     * 添加默认部门信息
     * @param json
     * @param element
     * @return
     */
    public static JSONObject addDefaultDepartment(JSONObject json, CatalogElement element) {
		IDepartment department = userManagerModule.getDefaultDepartment(element.getAuthorUserid());
		json.put("defaultDept", "");
		if(department != null) {
			json.put("defaultDept", department.getAlias());
		}
    	return json;
    }	
    
    /**
     * 返回正确的数据集内容
     * @param data       数据列表
     * @param pageIndex  页码
     * @param pageSize   每页大小
     * @return
     */
    public static JSONObject getSuccessData(JSONArray data,int pageIndex, int pageSize) {
    	JSONObject result = new JSONObject();
    	result.put("data", data);
    	result.put("success", true);
    	result.put("pageIndex", pageIndex);
    	result.put("pageSize", pageSize);
    	result.put("count", data.length());  
    	return result;
    }
    
    /**
     * 返回正确的数据集内容
     * @param data       数据列表
     * @param pageIndex  页码
     * @param pageSize   每页大小
     * @param errmsg     错误信息
     * @return
     */    
    public static JSONObject getFailData(int pageIndex, int pageSize, String errmsg) {
    	JSONObject result = new JSONObject();
    	result.put("data", new JSONArray());
    	result.put("success", false);
    	result.put("pageIndex", pageIndex);
    	result.put("pageSize", pageSize);  
    	result.put("count", 0);
		result.put("error", errmsg);
    	return result;    	
    }    
    
    /**
     * 获取ClientConnector
     * 
     * @param username 用户名
     * @return connector
     */
    public static ClientConnector getClientConnector(String username) {
        ClientConnector connector = null;
        connector = ClientConnectorFactory.getClientConnector(null);
        String user = "scheduleAdmin";
        String password = ScheduleTaskModule.getInstance().getScheduleAdminPassword();
        connector.openFromDB(user, password);
        connector.switchUser(username);
        return connector;
    }    
    
	/**
	 * 重命名报表
	 * 
	 * @param resId
	 *            需要重命名的资源id 此处指新的资源别称
	 */
	public static JSONObject renamePortalResource(String resId, String alias) {
		JSONObject result = new JSONObject();
		result.put("success", true);
		// 先重命名魔数师实际的资源，重命名成功之后，再重命名t_portal_analysis表中存的名称
		ResourceTreeNode node = (ResourceTreeNode) ResourceTreeNodeDAO.getInstance().load(resId);

		// 先检测同级目录下是否存在同名资源
		JSONObject checkData = checkNodeAlias(node, alias);
		if(!checkData.optBoolean("success", true)) {
			return checkData;
		}

		// node为空的原因：1、这个resId对应的是魔数师首页新建的目录，存在于t_portal_analysis表中；
		// 2、不存在这个资源
		if (node != null) {
			// 旧方法只改变了alias这一列的值，没有对应更新表头等其他属性，改用其他方式，直接调用产品的updateCatalogNode方法。
			JSONObject jsonNode = new JSONObject();
			jsonNode.put("alias", alias);
			jsonNode.put("desc", alias);
			catalogTreeModule.updateCatalogNode(resId, jsonNode.toString(), null);
			return result;
		} else {
			// 提示不存在此资源
			LOG.info("报表" + resId + "门户中不存在！");
			result.put("success", false);			
			result.put("errorMsg", "没有找到指定的资源节点: 自助分析资源不存在,重命名失败！");
			return result;
		}
	}    
	
	/**
	 * 重命名报表调用，检测此资源同级目录下是否存在同名资源，假如存在，直接抛异常。 参考CatalogTreeModule.checkNodeAlias
	 * 
	 * @param node
	 *            需要被检测的资源
	 * @param alias
	 *            名称
	 * @return 
	 */
	@SuppressWarnings("rawtypes")
	private static JSONObject checkNodeAlias(ResourceTreeNode node, String alias) {
		JSONObject result = new JSONObject();
		result.put("success", true);
		if (node != null && !StringUtil.isNullOrEmpty(alias)) {
			List<ResourceTreeNode> nodes = ResourceTreeNodeDAO.getInstance().getSameAliasNodes(node.getParentNode(),
					alias);
			Iterator iterator = nodes.iterator();
			while (iterator.hasNext()) {
				ResourceTreeNode tempNode = (ResourceTreeNode) iterator.next();
				if (!tempNode.getId().equals(node.getId())) {
					result.put("success", false);
					//throw (new SmartbiException(CatalogTreeErrorCode.CATALOG_DUPLICATE_FOLDER)).setDetail(": " + alias);
					result.put("errorMsg", "已存在同名的对象:" + alias);
					break;
				}
			}
		}
		return result;
	}	
	
	/**
	 * 将资源移动到某个目录下	
	 * @param resId   资源id
	 * @param toDirResId   目录资源id
	 * @return
	 */
	public static JSONObject moveCatalogElement(String resId, String toDirResId) {
		JSONObject result = new JSONObject();
		result.put("success", true);
		catalogTreeModule.moveCatalogElement(resId, toDirResId);
		return result;
	}
	
	/**
	 * 根据资源id获取该资源的root根目录的目录数据
	 * @param resId 资源id
	 * @return
	 */
	public static JSONObject getDefaultDirByResId(String resId) {
		JSONObject result = new JSONObject();
		result.put("success", true);
		String path = catalogTreeModule.getCatalogElementFullPath(resId);
		if(StringUtil.isNullOrEmpty(path)) {
			result.put("success", false);
			result.put("errMsg", "系统中不存在resId=[" + resId + "]的资源");
			return result;
		}
		String rootPathResId = null;
		if (path.indexOf("我的空间") == 0) {
			rootPathResId = "SELF_" + userManagerModule.getCurrentUser().getId();
		} else if (path.indexOf("分析报表") == 0) {
			rootPathResId = "PUBLIC_ANALYSIS";
		} else if (path.indexOf("公共空间") == 0) {
			rootPathResId = "DEFAULT_TREENODE";
		}
		if(!StringUtil.isNullOrEmpty(rootPathResId)) {
			result.put("success", false);
			result.put("errMsg", "资源id的root目录id找不到，resId=[" + resId + "]的资源");
			return result;			
		}
		CatalogElement element = catalogTreeModule.getCatalogElementById(rootPathResId);
		result.put("data", createJsonByElement(element));
		return result;
	}
	
	/**
	 * 根据目录接待获取下级目录节点
	 * @param dirId 目录资源id
	 * @return
	 */
	public static JSONObject getChildDirByDirId(String dirId) {
		JSONObject result = new JSONObject();
		try {
			result.put("success", true);
			List<CatalogElement> list = catalogTreeModule.getChildElementsByTypes(dirId, null);
			if(list == null && list.size() == 0) {
				result.put("data", new JSONArray());
				return result;
			}
			JSONArray data = new JSONArray();
			for(CatalogElement element : list) {
				data.put(createJsonByElement(element));
			}
		}catch(Exception e){
			result.put("success", false);
			result.put("data", new JSONArray());
		}
		return result;
	}

}
