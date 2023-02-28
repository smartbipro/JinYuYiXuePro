package smartbi.ext.jinyuyixue.dataportal.util;

import smartbi.catalogtree.CatalogElement;
import smartbi.net.sf.json.JSONObject;

/**
 * 工具类
 */
public class CommonUtils {

	
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
    		result.put("authorUserid", element.getAuthorUserid());
    		result.put("hasChild", element.isHasChild());
    		result.put("author", element.getAuthor());
    		result.put("order", element.getOrder());
    	}
    	return result;
    }
}
