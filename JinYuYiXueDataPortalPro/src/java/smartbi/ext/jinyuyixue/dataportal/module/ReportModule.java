package smartbi.ext.jinyuyixue.dataportal.module;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import smartbi.catalogtree.CatalogElement;
import smartbi.catalogtree.CatalogTreeModule;
import smartbi.catalogtree.ICatalogSearchResult;
import smartbi.combinedquery.repository.CombinedQuery;
import smartbi.combinedquery.repository.CombinedQueryDAOFactory;
import smartbi.ext.jinyuyixue.dataportal.repository.ReportDetail;
import smartbi.ext.jinyuyixue.dataportal.repository.ReportDetailDAO;
import smartbi.ext.jinyuyixue.dataportal.util.CacheDataUtil;
import smartbi.ext.jinyuyixue.dataportal.util.CommonUtils;
import smartbi.ext.jinyuyixue.dataportal.util.ConfigUtil;
import smartbi.ext.jinyuyixue.dataportal.util.PageUtil;
import smartbi.index.IDocument;
import smartbi.insight.repository.Insight;
import smartbi.insight.repository.InsightDAOFactory;
import smartbi.metadata.MetadataModule;
import smartbi.metadata.assist.CategoryResource;
import smartbi.net.sf.json.JSONArray;
import smartbi.net.sf.json.JSONObject;
import smartbi.usermanager.UserManagerModule;
import smartbi.util.XmlUtility;
import smartbix.modelquery.bean.ModelQueryBean;
import smartbix.modelquery.bean.ModelQueryField;
import smartbix.smartbi.modelquery.ModelQueryForVModule;
import smartbix.util.StringUtil;
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
     * 
	 * 公共报表模糊查询 列表分页方式呈现
	 * @param types 类型列表
	 * @param alias 别名
	 * @param reportType 报表类型  个人报表 "person":个人报表 "common":公共报表
	 * @param purview purview
	 * @param pageIndex 页码
	 * @param pageSize 每页大小
     * @return
     */
    public JSONObject searchReportDataLikeAlias(List<String> types, String alias, String reportType,
			String purview, int pageIndex, int pageSize) {
    	try {
	    	List<ICatalogSearchResult> list = catalogTreeModule.searchCatalogElementLikeAliasByType(types, alias, purview);
	    	
	    	if(StringUtil.isNotNullAndEmpty(reportType)) {
	    		if("person".equals(reportType)) {
	    			list = filterSelfReportData(list);
	    		}
	    		if("common".equals(reportType)) {
	    			list = filterCommonReportData(list);
	    		}
	    	}
	    	if(list != null && list.size() == 0) {
	    		return CommonUtils.getSuccessData(new JSONArray(), pageIndex, pageSize, list.size());
	    	}	    
	    	List<ICatalogSearchResult> pageList = new ArrayList<ICatalogSearchResult>();
    		if(list.size() >= (pageIndex-1) * pageSize) {
    			pageList = PageUtil.startPage(list, pageIndex, pageSize);
    		}
	    	JSONArray resultList = CommonUtils.reSetIndexModelAndReportDataListByCatalog(pageList, CacheDataUtil.cacheReportData, true);
	    	return CommonUtils.getSuccessData(resultList, pageIndex, pageSize, list.size());
    	}catch(Exception e) {
    		LOG.error("searchReportDataLikeAlias error：" + e.getMessage(),e);
    		return CommonUtils.getFailData(pageIndex, pageSize, "searchReportDataLikeAlias error：" + e.getMessage());
    	}
    }
    
    /**
     * 获取公共报表的数据
     * @param list 报表列表数据
     * @return
     */
    private List<ICatalogSearchResult> filterCommonReportData(List<ICatalogSearchResult> list){
    	return filterReportData(list, false);
    }
    
    /**
     * 获取个人报表数据
     * @param list  报表列表数据
     * @return
     */
    private List<ICatalogSearchResult> filterSelfReportData(List<ICatalogSearchResult> list){
    	return filterReportData(list, true);
    }   
    
    /**
     * 根据类型过滤出公共报表还是个人报表
     * @param list    列表  
     * @param isSelf  true 个人报表 false 公共报表
     * @return
     */
    private List<ICatalogSearchResult> filterReportData(List<ICatalogSearchResult> list, boolean isSelf) {
    	List<ICatalogSearchResult> result = new ArrayList<ICatalogSearchResult>();
    	String pathFirstName = "我的空间";//判断路径，当时个人报表时，路径为我的空间的位置为0，其他为-1
    	if(!isSelf) {
    		pathFirstName = "公共空间";
    	}
    	for(ICatalogSearchResult item : list) {    		
    		CatalogElement element = (CatalogElement) item.getCatalogElement();
    		String path = catalogTreeModule.getCatalogElementFullPath(element.getId());
    		//个人空间
    		if(StringUtil.isNotNullAndEmpty(path) && path.indexOf(pathFirstName) == 0) {
    			result.add(item);
    		}
    	}
    	return result;    	
    }
    
    /**
	 * 根据指标id获取影响性的报表列表数据
	 * @param resIds     资源id
	 * @param pageIndex 页码
	 * @param pageSize 每页大小
	 * @return result 
     * @return
     */
    public JSONObject getReportByIndexResId(String resIds, int pageIndex, int pageSize) {
    	try {    		
    		if(StringUtil.isNullOrEmpty(resIds)) {
    			return CommonUtils.getFailData(pageIndex, pageSize, "getReportByIndexResId error：resids is null");
    		}    		
    		String[] arryResIds = resIds.split(",");
    		List<IDocument> list = new ArrayList<IDocument>();
    		for(String resId : arryResIds) {
        		//影响性分析
    			CategoryResource categoryResource = new CategoryResource(); 
        		categoryResource.setId("AUGMENTED_DATASET_MEASURE." + resId);
        		List<String> filters = new ArrayList<String>();
        		filters.add(ConfigUtil.RESTREE_REPORT_TYPE.SMARTBIX_PAGE);//自助仪表盘
        		filters.add(ConfigUtil.RESTREE_REPORT_TYPE.COMBINED_QUERY);//即席查询
        		List<IDocument> tmpList = MetadataModule.getInstance().searchByReferenced(categoryResource, filters);
        		if(tmpList != null && tmpList.size() > 0) {
        			list.addAll(tmpList);
        		}
    		}
    		if(list != null && list.size() > 0) {
    	    	List<IDocument> pageList = new ArrayList<IDocument>();
        		if(list.size() >= (pageIndex-1) * pageSize) {
        			pageList = PageUtil.startPage(list, pageIndex, pageSize);
        		}
    	    	JSONArray resultList = CommonUtils.reSetIndexModelAndReportDataListByDoc(pageList, CacheDataUtil.cacheReportData, true);
    	    	return CommonUtils.getSuccessData(resultList, pageIndex, pageSize, list.size());    			
    		}
    		return CommonUtils.getSuccessData(new JSONArray(), pageIndex, pageSize, 0); 
    	}catch(Exception e) {
    		LOG.error("getReportByIndexResId error：" + e.getMessage(),e);
    		return CommonUtils.getFailData(pageIndex, pageSize, "getReportByIndexResId error：" + e.getMessage());
    	}
    }  
    
    /**
     * 透视分析根据报表id获取表头信息
     * @param resId
     * @return
     */
    public JSONArray getInsightMultiHeader(String resId) {
    	try {
    		JSONArray result = new JSONArray();
    		Insight insight = InsightDAOFactory.getInsightDAO().load(resId);
			String content= insight.getContent();
			Document doc = XmlUtility.parse(content);
			Element root = (Element) doc.getFirstChild();
			NodeList bizViewDefineList = root.getElementsByTagName("client-config");
			JSONObject config = JSONObject.fromString(bizViewDefineList.item(0).getTextContent());
			JSONObject fieldProps = config.getJSONObject("gridProp").getJSONObject("fieldProps");
			Iterator iterator = fieldProps.keys();
			while(iterator.hasNext()) {
				String key = (String)iterator.next();
				JSONObject json = fieldProps.getJSONObject(key);
				JSONObject rec = new JSONObject();
				rec.put("id", key);
				rec.put("alias", json.optString("headerPath",""));
				result.put(rec);
			}
			return result;
    	}catch(Exception e) {
    		LOG.error("获取透视分析表头有误，resid:" + resId + "," + e.getMessage(), e);
    	}
    	return null;
    }
    
    /**
     * 透视分析根据报表id获取表头信息
     * @param resId
     * @return
     */
    public JSONArray getInsightMultiHeaderEx(String resId) {
    	try {
    		JSONArray result = new JSONArray();
    		Insight insight = InsightDAOFactory.getInsightDAO().load(resId);
			String content= insight.getContent();
			Document doc = XmlUtility.parse(content);
			Element root = (Element) doc.getFirstChild();
			NodeList bizViewDefineList = root.getElementsByTagName("fields");
			Element bizViewDefine = (Element) bizViewDefineList.item(0);
			
			NodeList fields = bizViewDefine.getChildNodes();
			for(int i = 0, len = fields.getLength(); i < len; i++) {
				Element element = (Element)fields.item(i);
				if("row".equals(element.getAttribute("position"))) {
					JSONObject rec = new JSONObject();
					rec.put("id", element.getAttribute("id"));
					rec.put("alias", element.getAttribute("alias"));
					result.put(rec);					
				}
			}
			for(int i = 0, len = fields.getLength(); i < len; i++) {
				Element element = (Element)fields.item(i);
				if("measure".equals(element.getAttribute("position"))) {
					JSONObject rec = new JSONObject();
					rec.put("id", element.getAttribute("id"));
					rec.put("alias", element.getAttribute("alias"));
					result.put(rec);					
				}
			}						
			return result;
    	}catch(Exception e) {
    		LOG.error("获取透视分析表头有误，resid:" + resId + "," + e.getMessage(), e);
    	}
    	return null;
    }
    
    /**
     * 即席查询根据报表id获取表头信息
     * @param resId
     * @return
     */    
    public JSONArray getCombinedMultiHeader(String resId) {
    	try {
    		JSONArray result = new JSONArray();
			CombinedQuery combinedQuery = CombinedQueryDAOFactory.getCombinedQueryDAO().load(resId);
			if(combinedQuery != null) {//非指标创建的即系查询报表
				String content= combinedQuery.getContent();
				Document doc = XmlUtility.parse(content);
				Element root = (Element) doc.getFirstChild();
				NodeList bizViewDefineList = root.getElementsByTagName("bizview-define");
				Element bizViewDefine = (Element) bizViewDefineList.item(0);
				Document subDoc = XmlUtility.parse(bizViewDefine.getTextContent());
				Element subRoot = (Element) subDoc.getFirstChild();
				NodeList list = subRoot.getChildNodes();
				for(int i = 0, len = list.getLength(); i < len; i++) {
					if("workspace".equals(list.item(i).getNodeName())) {
						NodeList outputFields = list.item(i).getFirstChild().getChildNodes();
						for(int j = 0, jLen = outputFields.getLength(); j < jLen; j++) {
							Element element = (Element)outputFields.item(j);
							JSONObject rec = new JSONObject();
							rec.put("id", element.getAttribute("id"));
							rec.put("alias", element.getAttribute("alias"));
							result.put(rec);
						}
						break;
					}
				}
			} else {//指标创建的即席查询报表
				ModelQueryBean openModelQuery = ModelQueryForVModule.getInstance().openModelQuery(resId);
				List<ModelQueryField> list = openModelQuery.getDefine().getFields();
				for(ModelQueryField field : list) {
					JSONObject rec = new JSONObject();
					rec.put("id", field.getId());
					rec.put("alias", field.getAlias());
					result.put(rec);
				}
			}
			return result;
    	}catch(Exception e) {
    		LOG.error("获取即系查询表头有误，resid:" + resId + "," + e.getMessage(), e);
    	}
    	return new JSONArray();
    }
    
    /**
     * 保存报表详情
     * @param resId  报表资源id
     */
    public void saveReportDetail(String resId) {
    	ReportDetail detail = ReportDetailDAO.getInstance().load(resId);
    	if(detail != null) {
    		CatalogElement element = catalogTreeModule.getCatalogElementById(resId);
    		if(element != null) {
    			detail.setReportDesc(element.getDesc());
    		}
    		detail.setImagePath(detail.getTmpImagePath());
			detail.setImageName(detail.getTmpImageName());
			detail.setUpdateUserId(userManagerModule.getCurrentUser().getId());
			detail.setStatus("1");
			detail.setUpdateDateTime(new Date());
			ReportDetailDAO.getInstance().update(detail);
			if(!StringUtil.isNullOrEmpty(detail.getImagePath()) && !StringUtil.isNullOrEmpty(detail.getImageName())
					&& !detail.getTmpImageName().equals(detail.getImageName())) {
				File imageFile = new File(detail.getImagePath() + detail.getImageName());
				if (imageFile.exists()) {
					imageFile.delete();
				}
			}
    	}
    }
    
    /**
     * 根据报表资源id获取报表的详情缩略图数据
     * @param resId 报表资源id
     * @return
     */
    public JSONObject getReportDetail(String resId) {
    	ReportDetail detail = ReportDetailDAO.getInstance().load(resId);
    	if(detail != null) {
    		JSONObject result = new JSONObject();
    		result.put("reportId", detail.getReportId());
    		result.put("reportAlias", detail.getReportAlias());
    		result.put("reportType", detail.getReportType());
    		result.put("reportDesc", detail.getReportDesc());
    		result.put("imagePath", detail.getImagePath() + detail.getImageName());
    		return result;
    	} else {
    		return new JSONObject();
    	}
    }
    
    /**
     * 根据报表id获取报表来源于模型
     * @param resId 资源id
     * @return
     */
    public JSONObject getReportDataModelByResId(String resId, int pageIndex, int pageSize) {
    	try {    		
    		CategoryResource categoryResource = new CategoryResource(); 
    		categoryResource.setId(resId);
    		List<String> filters = new ArrayList<String>();
    		filters.add(ConfigUtil.RESTREE_DATASET_TYPE.AUGMENTED_DATASET);//数据模型
    		//血缘关系查询
    		List<IDocument> list = MetadataModule.getInstance().searchReferringTo(categoryResource, filters);    
    		if(list != null && list.size() > 0) {
    			List<IDocument> pageList = new ArrayList<IDocument>();
        		if(list.size() >= (pageIndex-1) * pageSize) {
        			pageList = PageUtil.startPage(list, pageIndex, pageSize);
        		}
    	    	JSONArray resultList = CommonUtils.reSetIndexModelAndReportDataListByDoc(pageList, CacheDataUtil.cacheReportData, true);
    	    	return CommonUtils.getSuccessData(resultList, pageIndex, pageSize, list.size());    			
    		}
    		return CommonUtils.getSuccessData(new JSONArray(), pageIndex, pageSize, 0); 
    	}catch(Exception e) {
    		LOG.error("getReportDataModelByResId error：" + e.getMessage(),e);
    		return CommonUtils.getFailData(pageIndex, pageSize, "getReportDataModelByResId error：" + e.getMessage());
    	}    	
    }   
    
    /**
     * 根据报表资源id获取报表信息
     * @param resId 资源id
     * @return
     */
    public JSONObject getReportInfoByResId(String resId) {
    	JSONObject result = new JSONObject();
    	CatalogElement element = catalogTreeModule.getCatalogElementById(resId);
    	if(element != null) {
    		result = CommonUtils.createJsonByElement(element);
    		//添加路径
    		result = CommonUtils.addIndexPath(result, element);
    		//是否授权
    		result = CommonUtils.addIsAuthorized(result, element);
    		//创建部门
    		result = CommonUtils.addDefaultDepartment(result, element);
    		//报表缩略图
    		result.put("reportImgPath", "UpLoadReportDetailImageServlet?type=view&resid=" + resId);
    	}
    	return result;
    }    
}
