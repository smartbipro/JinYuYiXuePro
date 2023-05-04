package smartbi.ext.jinyuyixue.dataportal.module;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smartbi.catalogtree.CatalogElement;
import smartbi.catalogtree.CatalogTreeModule;
import smartbi.catalogtree.ICatalogSearchResult;
import smartbi.config.SystemConfigService;
import smartbi.ext.jinyuyixue.dataportal.repository.IndexClickData;
import smartbi.ext.jinyuyixue.dataportal.repository.IndexClickDataDAO;
import smartbi.ext.jinyuyixue.dataportal.repository.IndexClickDataDetail;
import smartbi.ext.jinyuyixue.dataportal.repository.IndexClickDataDetailDAO;
import smartbi.ext.jinyuyixue.dataportal.util.CacheDataUtil;
import smartbi.ext.jinyuyixue.dataportal.util.CommonUtils;
import smartbi.ext.jinyuyixue.dataportal.util.ConfigUtil;
import smartbi.ext.jinyuyixue.dataportal.util.PageUtil;
import smartbi.index.IDocument;
import smartbi.metadata.MetadataModule;
import smartbi.metadata.assist.CategoryResource;
import smartbi.ext.jinyuyixue.dataportal.util.ConfigUtil.INDEX_CLICK_DATA_TYPE;
import smartbi.ext.jinyuyixue.dataportal.util.ConfigUtil.REPORT_TYPE;
import smartbi.net.sf.json.JSONArray;
import smartbi.net.sf.json.JSONObject;
import smartbi.user.IDepartment;
import smartbi.usermanager.UserManagerModule;
import smartbix.metricsmodel.dimension.service.DimensionBO;
import smartbix.metricsmodel.metrics.service.MetricsBO;
import smartbix.smartbi.metricsmodel.MetricsModelForVModule;
import smartbi.ext.jinyuyixue.dataportal.util.ConfigUtil.INDEX_DETAIL_TITLE;
/**
 * 指标模块实现类
 */
public class IndexModule {
	/**
	 * 日志对象
	 */
    private static final Logger LOG = LoggerFactory.getLogger(IndexModule.class);
    /**
     * 用户管理module对象
     */
    private UserManagerModule userManagerModule = UserManagerModule.getInstance();
    /**
     * 资源树对象
     */
    private CatalogTreeModule catalogTreeModule = CatalogTreeModule.getInstance();     
    /**
     * 指标点击查询数据埋点数据dao实现类
     */
    private IndexClickDataDAO indexClickDataDao = IndexClickDataDAO.getInstance();
    /**
     * 指标点击查询数据埋点明细数据dao实现类
     */    
    private IndexClickDataDetailDAO indexClickDataDetailDao = IndexClickDataDetailDAO.getInstance();
    /**
     * 指标模块对象
     */
    private static IndexModule instance;
    
    public static IndexModule getInstance() {
        if (instance == null) {
            instance = new IndexModule();
        }
        return instance;
    }
    
    /**
     * 指标查询数据埋点计算
     * @param indexId    指标id
     * @param indexName  指标名称
     */
    public void indexAddSearchNumber(String indexId, String indexName) {
    	indexAddNumber(indexId, indexName, INDEX_CLICK_DATA_TYPE.SEARCH);
    }
    
    /**
     * 指标点击数据埋点计算
     * @param indexId    指标id
     * @param indexName  指标名称
     */    
    public void indexAddClickNumber(String indexId, String indexName) {
    	indexAddNumber(indexId, indexName, INDEX_CLICK_DATA_TYPE.CLICK);
    }
    
    /**
     * 指标点击、查询数据埋点计算
     * @param indexId    指标id
     * @param indexName  指标名称
     * @param indexClickType  类型：点击和查询
     */
    private void indexAddNumber(String indexId, String indexName, String indexClickType) {
    	IndexClickData indexClickData = indexClickDataDao.getIndexClickDataById(indexId);
    	Date now = new Date();
    	if(indexClickData != null) {
    		indexClickData.setUpdateDateTime(now);
    		if(INDEX_CLICK_DATA_TYPE.CLICK.equals(indexClickType)) {
    			indexClickData.setClickNum(indexClickData.getClickNum() + 1);
    		}else {
    			indexClickData.setSearchNum(indexClickData.getSearchNum() + 1);
    		}
    		indexClickDataDao.update(indexClickData);
    	} else {
    		indexClickData = new IndexClickData(indexId, indexName, 0, 1, now, now);
    		indexClickDataDao.save(indexClickData);
    	}
    	//保存明细数据
    	String uuid = UUID.randomUUID().toString().replaceAll("-", "");
    	String userId = userManagerModule.getCurrentUser().getId();
    	IndexClickDataDetail indexClickDataDetail = new IndexClickDataDetail(uuid, indexId, userId, indexClickType, now);
    	indexClickDataDetailDao.save(indexClickDataDetail);    	
    }
    
    /**
     * 判断资源当前用户是否有引用权限
     * @param resId 资源id
     * @return
     */
    public boolean hasRefByResId(String resId) {
    	return catalogTreeModule.isCatalogElementAccessible(resId, "REF");
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
    public JSONObject searchetricsIdLikeAliasByType(List<String> types, String alias,
			String purview, int pageIndex, int pageSize) {
    	JSONObject result = new JSONObject();
    	try {
	    	List<ICatalogSearchResult> list = catalogTreeModule.searchCatalogElementLikeAliasByType(types, alias, purview);
	    	List<ICatalogSearchResult> pageList = PageUtil.startPage(list, pageIndex, pageSize);
	    	JSONArray resultList = reSetIndexDataList(pageList, CacheDataUtil.cacheIndexData);
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
    
    
    /////////////////////指标组合//////////////////////////
    /**
     * 重置资源列表数据
     * @param list
     * @return
     */
    public JSONArray reSetIndexDataList(List<ICatalogSearchResult> list, Map<String, JSONObject> cacheIndexData){
    	JSONArray result = new JSONArray();
    	boolean isOnCache = CacheDataUtil.isOnCache();
    	//是否有创建报表的权限
    	JSONObject opAuthorized = CommonUtils.getReportFunctionByCurrentUser();
    	for(ICatalogSearchResult item : list) {    		
    		CatalogElement element = (CatalogElement) item.getCatalogElement();
    		String resId = element.getId();
    		indexAddSearchNumber(resId, element.getAlias());
    		JSONObject cacheIndexRec = cacheIndexData.get(resId);
    		if(cacheIndexRec != null && isOnCache) {
        		//添加指标点击、搜索次数
    			cacheIndexRec = addIndexClickSearchNum(cacheIndexRec, element); 
        		//添加授权
    			cacheIndexRec = CommonUtils.addIsAuthorized(cacheIndexRec, element);
        		//是否有创建即席查询、自助仪表盘权限
    			cacheIndexRec = CommonUtils.addOpAuthorized(cacheIndexRec, opAuthorized);    			
    			cacheIndexData.put(resId, cacheIndexRec);
    			result.put(cacheIndexRec);
    			continue;
    		}
    		JSONObject map = CommonUtils.createJsonByElement(element);
    		MetricsBO metricsBO = MetricsModelForVModule.getInstance().getMetricsManageService().getMetricsById(resId);
    		//添加指标事实表字段
    		map = addIndexFieldName(map, metricsBO);
    		//添加指标点击、搜索次数
    		map = addIndexClickSearchNum(map, element);
    		//添加指标路径
    		map = CommonUtils.addIndexPath(map, element);
    		//添加数据更新日期和提出部门////////////////////////////////
    		map = addDataUpdateAndProposedDept(map, metricsBO);
    		//添加授权
    		map = CommonUtils.addIsAuthorized(map, element);
    		//是否有创建即席查询、自助仪表盘权限
    		map = CommonUtils.addOpAuthorized(map, opAuthorized);
    		//加载如返回列表中    		
    		result.put(map);
    		//将指标数据进行缓存
    		if(isOnCache) {
    			cacheIndexData.put(resId, map);
    		}
    	}
    	return result;
    }  
    
    /**
     * 添加数据更新日期和提出部门
     * @param json
     * @param element
     * @return
     */
    private JSONObject addDataUpdateAndProposedDept(JSONObject json, MetricsBO metricsBO) {
    	//获取到扩展指标的值
    	json.put("proposedDept", "提出部门从指标扩展内容中取值");
    	json.put("dataUpdateDate", "数据更新日期从指标扩展内容中取值");
		return json;
    }
    
    /**
     * 添加指标点击、查询次数信息
     * @param json
     * @param element
     * @return
     */    
    private JSONObject addIndexClickSearchNum(JSONObject json, CatalogElement element) {
    	IndexClickData indexClickData = indexClickDataDao.getIndexClickDataById(element.getId());
    	int clickNum = 0;
    	int searchNum = 0;
    	if(indexClickData != null) {
    		clickNum = indexClickData.getClickNum();
    		searchNum = indexClickData.getSearchNum();
    	}
    	json.put("clickNum", clickNum);
    	json.put("searchNum", searchNum);
    	return json;
    }
    
    /**
     * 指标名称加上事实表字段名称
     * @param json
     * @param element
     * @return
     */
    private JSONObject addIndexFieldName(JSONObject json, MetricsBO metricsBO) {    	
		json.put("alias2", json.get("alias") + "(" + metricsBO.getFactTableField().getName() + ")");
		json.put("factTableName", metricsBO.getFactTable().getName());
    	return json;
    }    
 
    /**
     * 获取指标详情信息  如后续指标详情修改了，该方法也需要做相应调整
     * @param resId 指标资源id
     * @return
     */    
	public JSONArray getIndexDetailByResId(String resId) {
		JSONArray result = new JSONArray();
		//指标的配置信息
		String configStr = SystemConfigService.getInstance().getLongValue("METRICS_ATTRIBUTE_CONFIGURE");
		JSONObject config = JSONObject.fromString(configStr);
		JSONArray defaultFields = config.getJSONArray("defaultFields");
		JSONArray extendedFields = config.getJSONArray("extendedFields");
		//指标对象
		MetricsBO metricsBO = MetricsModelForVModule.getInstance().getMetricsManageService().getMetricsById(resId);
		//获取指标类型
		String classId = metricsBO.getClassId();
		result.put(getIndexClassObjectById((classId)));

		//指标基本的信息
		for(int i = 0, len = defaultFields.length(); i < len; i++) {
			JSONObject source = defaultFields.getJSONObject(i);
			result.put(toDefaultFieldJson(source, metricsBO));
		}
		//指标扩展信息
		JSONObject extendedConfig = new JSONObject();
		if(metricsBO.getExtended() != null) {
			extendedConfig = JSONObject.fromString(metricsBO.getExtended());
		}
		for(int i = 0, len = extendedFields.length(); i < len; i++) {
			JSONObject source = extendedFields.getJSONObject(i);
			result.put(toExtendedFieldJson(source, extendedConfig));
		}
		return result;
	}	
	
	/**
	 * 根据指标分类id获取指标分类信息
	 * @param classId
	 * @return
	 */
	private JSONObject getIndexClassObjectById(String classId) {
		JSONObject result = new JSONObject();
		result.put("name", classId);
		result.put("alias", "指标分类");
		CatalogElement classElement = catalogTreeModule.getCatalogElementById(classId);
		if(classElement != null) {
			result.put("name", classElement.getName());
			result.put("value", classElement.getAlias());
		}
		return result;
	}
	
	/**
	 * 指标基本信息的获取方法
	 * @param source     单独一个字段的指标配置信息
	 * @param metricsBO  指标对象
	 * @return
	 */
	private JSONObject toDefaultFieldJson(JSONObject source, MetricsBO metricsBO) {
		JSONObject result = new JSONObject();
		String name = source.optString("name");
		String alias = source.optString("alias");
		result.put("name", name);
		result.put("alias", alias);		
		switch(name) {
			case "code"://指标编码
				result.put("value", metricsBO.getCode());
				break;
			case "name"://指标名称
				result.put("value", metricsBO.getName());
				break;
			case "type"://指标类型
				String type = metricsBO.getType().toString();
				Map<String, String> typeMap = changeJsonToMap(source);
				if(typeMap.get(type) != null) {
					type = typeMap.get(type);
				}
				result.put("value", type);
				break;
			case "summaryBasis"://汇总依据
				String summaryBasis = metricsBO.getSummaryBasis();
				Map<String, String> summaryBasisMap = changeJsonToMap(source);
				if(summaryBasisMap.get(summaryBasis) != null) {
					summaryBasis = summaryBasisMap.get(summaryBasis);
				}
				result.put("value", summaryBasis);				
				break;
			case "status"://指标状态
				String status = metricsBO.getStatus();
				Map<String, String> statusMap = changeJsonToMap(source);
				if(statusMap.get(status) != null) {
					status = statusMap.get(status);
				}
				result.put("value", status);
				break;
			case "effectiveDate"://启用日期
				result.put("value", metricsBO.getEffectiveDate());
				break;
			case "expiratedDate"://禁用日期
				result.put("value", metricsBO.getExpiratedDate());
				break;
			case "dimensionIds"://选择维度
				List<DimensionBO> list = metricsBO.getBindingDimensions();
				StringBuilder dimension = new StringBuilder();
				for(DimensionBO bo : list) {
					dimension.append(bo.getAlias()).append(",");
				}
				if(dimension.length() > 0) {
					dimension.deleteCharAt(dimension.length()-1);
				}
				result.put("value", dimension.toString());
				result.put("alias", "维度");
				break;
			case "factTableId"://选择事实表
				result.put("value", metricsBO.getFactTable().getAlias());
				result.put("alias", "事实表");
				break;
			case "factTableFieldId"://选择字段
				result.put("value", metricsBO.getFactTableField().getAlias());
				result.put("alias", "事实表字段");
				break;
		}
		return result;
	}
	
	/**
	 * 基础字段和扩展字段中的枚举值转换为map
	 * @param source  显示一个字段的所有配置信息，系统选项中配置的内容
	 * @return
	 */
	private Map<String, String> changeJsonToMap(JSONObject source){
		try {
			JSONObject componentDefine = source.getJSONObject("componentDefine");
			JSONObject standbyValue = componentDefine.getJSONObject("standbyValue");
			JSONArray values = standbyValue.getJSONArray("value");
			Map<String, String> result = new HashMap<String, String>();
			for(int i = 0, len = values.length(); i < len; i++) {
				JSONObject rc = values.getJSONObject(i);
				result.put(rc.optString("value"), rc.optString("label"));
			}		
			return result;
		}catch(Exception e) {
			return new HashMap<String, String>();
		}
	}
	
	/**
	 * 设置扩展字段的返回值
	 * @param source   扩展字段的配置
	 * @param config   扩展字段所有的值
	 * @return
	 */
	private JSONObject toExtendedFieldJson(JSONObject source, JSONObject config) {
		JSONObject result = new JSONObject();
		String name = source.optString("name");
		String alias = source.optString("alias");
		String value = config.optString(name);
		
		result.put("name", name);
		result.put("alias", alias);
		result.put("value", value);
		Map<String, String> map = changeJsonToMap(source);
		if(map.get(value) != null) {
			result.put("value", map.get(value));
		}
		return result;
	}
	
	/**
	 * 获取指标点击次数数据	
	 * @param resId 资源id
	 * @return
	 */
    public IndexClickData getIndexClickDataById(String resId) {
    	return indexClickDataDao.getIndexClickDataById(resId);
    }
	
}
