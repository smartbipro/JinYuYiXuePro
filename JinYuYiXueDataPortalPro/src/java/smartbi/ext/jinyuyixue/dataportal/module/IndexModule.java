package smartbi.ext.jinyuyixue.dataportal.module;


import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smartbi.ext.jinyuyixue.dataportal.repository.IndexClickData;
import smartbi.ext.jinyuyixue.dataportal.repository.IndexClickDataDAO;
import smartbi.ext.jinyuyixue.dataportal.repository.IndexClickDataDetail;
import smartbi.ext.jinyuyixue.dataportal.repository.IndexClickDataDetailDAO;
import smartbi.ext.jinyuyixue.dataportal.util.ConfigUtil.INDEX_CLICK_DATA_TYPE;
import smartbi.usermanager.UserManagerModule;
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
}
