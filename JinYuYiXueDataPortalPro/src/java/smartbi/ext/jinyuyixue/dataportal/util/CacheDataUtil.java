package smartbi.ext.jinyuyixue.dataportal.util;

import java.util.HashMap;
import java.util.Map;

import smartbi.net.sf.json.JSONObject;

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
    	return "清空自定义指标缓存数据成功.";
    }  
    
    /**
     * 清空数据集数据缓存
     * @return
     */
    public static String clearCacheDSData() {
    	cacheDSData.clear();
    	return "清空自定义数据集缓存数据成功.";
    }
    
    /**
     * 清空报表数据缓存
     * @return
     */
    public static String clearCacheReportData() {
    	cacheReportData.clear();
    	return "清空自定义报表缓存数据成功.";
    }
    
    /**
     * 清空所有自定义缓存数据
     * @return
     */
    public static String clearCacheAllData() {
    	clearCacheIndexData();
    	clearCacheDSData();
    	clearCacheReportData();
    	return "清空自定义指标、数据集、报表缓存所有数据成功.";
    }

    /**
     * 是否启动缓存
     * @return
     */
    public static boolean isOnCache() {
    	return true;
    }

}
