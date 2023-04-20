package smartbi.ext.jinyuyixue.dataportal.module;


import java.util.ArrayList;
import java.util.Date;
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
import smartbix.metricsmodel.metrics.service.MetricsBO;
import smartbix.smartbi.metricsmodel.MetricsModelForVModule;
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
    	//是否有创建报表的权限
    	JSONObject opAuthorized = CommonUtils.getReportFunctionByCurrentUser();
    	for(ICatalogSearchResult item : list) {    		
    		CatalogElement element = (CatalogElement) item.getCatalogElement();
    		String resId = element.getId();
    		indexAddSearchNumber(resId, element.getAlias());
    		JSONObject cacheIndexRec = cacheIndexData.get(resId);
    		if(cacheIndexRec != null) {
        		//添加指标点击、搜索次数
    			cacheIndexRec = addIndexClickSearchNum(cacheIndexRec, element); 
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
    		cacheIndexData.put(resId, map);
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
     * 获取指标详情信息
     * @param resId 指标资源id
     * @return
     */    
	public JSONObject getIndexDetailInfo(String resId) {
		//指标的配置信息
		String configStr = SystemConfigService.getInstance().getLongValue("METRICS_ATTRIBUTE_CONFIGURE");
		//指标对象
		MetricsBO metricsBO = MetricsModelForVModule.getInstance().getMetricsManageService().getMetricsById(resId);
		
		return null;
	}	
}
