package smartbi.ext.jinyuyixue.dataportal.util;

import java.util.HashMap;
import java.util.Map;

import smartbi.config.SystemConfigService;
import smartbi.net.sf.json.JSONObject;
import smartbi.util.StringUtil;

/**
 * 缓存类
 */
public class CacheDataUtil {
    /**
     * 指标数据缓存
     */
    public static Map<String, JSONObject> cacheIndexData = new HashMap<String, JSONObject>();
    
    /**
     * 数据集数据缓存
     */
    public static Map<String, JSONObject> cacheDSData = new HashMap<String, JSONObject>();
    /**
     * 报表数据缓存
     */
    public static Map<String, JSONObject> cacheReportData = new HashMap<String, JSONObject>();  
    
    /**
     * 清空指标查询数据缓存
     * @return
     */
    public static String clearCacheIndexData() {
    	cacheIndexData.clear();
    	return StringUtil.getLanguageValue("JYYX_DATA_CACHE_CLEAR_INDEX_SUCCESS");
    }  
    
    /**
     * 清空数据集数据缓存
     * @return
     */
    public static String clearCacheDSData() {
    	cacheDSData.clear();
    	return StringUtil.getLanguageValue("JYYX_DATA_CACHE_CLEAR_DATASET_SUCCESS");
    }
    
    /**
     * 清空报表数据缓存
     * @return
     */
    public static String clearCacheReportData() {
    	cacheReportData.clear();
    	return StringUtil.getLanguageValue("JYYX_DATA_CACHE_CLEAR_REPORT_SUCCESS");
    }
    
    /**
     * 清空所有自定义缓存数据
     * @return
     */
    public static String clearCacheAllData() {
    	clearCacheIndexData();
    	clearCacheDSData();
    	clearCacheReportData();
    	return StringUtil.getLanguageValue("JYYX_DATA_CACHE_CLEAR_ALL_SUCCESS");
    }

    /**
     * 是否启动缓存  可通过系统选项加个开关设置是否启动缓存
     * @return
     */
    public static boolean isOnCache() {
    	String isOn = SystemConfigService.getInstance().getLongValue("JYYX_DATA_CACHE_CONFIG");
    	if(StringUtil.isNullOrEmpty(isOn)) {
    		return false;
    	}
    	if("true".equals(isOn)) {
    		return true;
    	}
    	return false;
    }

}
