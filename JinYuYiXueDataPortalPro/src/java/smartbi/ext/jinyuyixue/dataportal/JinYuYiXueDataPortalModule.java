package smartbi.ext.jinyuyixue.dataportal;


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
import smartbi.ext.jinyuyixue.dataportal.util.ConfigUtil.INDEX_CLICK_DATA_TYPE;
import smartbi.framework.IModule;
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
     * 数据资产首页，包含常用工具”、"我的报表资源”、“公共报表资源”初始数据
     * @return
     */
    public JSONObject getIndexHomeTreeNodes() {
    	return indexHomePageModule.getIndexHomeTreeNodes();
    }
    
    /**
     * 获取资源下的子节点
     * @param resId
     * @return
     */
    public List<CatalogElement> getChildElements(String resId) {
    	return indexHomePageModule.getChildElements(resId);
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
    
}
