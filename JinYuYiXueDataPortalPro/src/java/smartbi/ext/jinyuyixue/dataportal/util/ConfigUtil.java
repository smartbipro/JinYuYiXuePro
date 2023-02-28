package smartbi.ext.jinyuyixue.dataportal.util;


/**
 * 配置工具类
 */
public class ConfigUtil {
	
	/**
	 * 资源节点ID
	 */
	public static class CATALOGTREE_NODE {
		//公共空间
		public static final String DEFAULT_TREENODE = "DEFAULT_TREENODE";
		//我的空间 格式:SELF_当前用户的用户ID
		public static final String SELF_TREENODE = "SELF_";
		//公共空间下的应用目录
		public static final String APP_INSTALL_FOLDER = "APP_INSTALL_FOLDER";
		//我的空间下的分析报表目录 格式:SELF_ANALYSIS_当前用户的用户ID
		public static final String SELF_ANALYSIS_TREENODE = "SELF_ANALYSIS_";
		//我的空间默认节点
		public static final String SELF_TREENODE_DEFAULT = "SELF_TREENODE";
		//常用工具
		public static final String COMMON_TOOLS = "COMMON_TOOLS";
	}
	
	/**
	 * 创建报表的url 
	 */
	public static class CREATERESOURCE_URL {
		//即席查询URL
		public static final String COMBINEDQUERY_URL = "/vision/createresource.jsp?sourcetype=new&restype=combinedquery&hidetoolbaritems=MYFAVORITE,MPP,PRINT,CREATEINSIGHTINQUERY,TEMPTABLE";
		//透视分析URL
		public static final String INSIGHT_URL = "/vision/createresource.jsp?sourcetype=new&restype=INSIGHT&hidetoolbaritems=MPP,PRINT,CREATEINSIGHTINQUERY,TEMPTABLE";
		//可视化查询URL
		public static final String DASHBOARD_URL = "../smartbix/?integrated=true&showheader=false&dashboardInitId=I1f81a9e00169c94ac94a43130169d2cd942b0430&l=zh_CN&nodeid=DEFAULT_TREENODE#/dashboard";
	}	
	
	/**
	 * 常用报表类型
	 */
	public static class REPORT_TYPE {
		//即系查询
		public static final String COMBINEDQUERY = "combinedquery";
		//透视分析
		public static final String INSIGHT = "insight";
		//可视化查询
		public static final String DASHBOARD = "dashboard";
	}
	
	/**
	 * 指标埋点数据类型
	 */
	public static class INDEX_CLICK_DATA_TYPE {
		public static final String CLICK = "click";
		public static final String SEARCH = "search";
	}
}
