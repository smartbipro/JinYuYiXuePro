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
	 * 操作权限
	 */
	public static class FUNCTION_DATA{
		//即席查询创建操作权限
		public static final String CUSTOM_DISPLAYCUSTOM_COMBINEDQUERY = "CUSTOM_DISPLAYCUSTOM_COMBINEDQUERY";
		//透视分析创建操作权限
		public static final String CUSTOM_DISPLAYCUSTOM_INSIGHT = "CUSTOM_DISPLAYCUSTOM_INSIGHT";
		//自助仪表盘创建操作权限
		public static final String XDASHBOARD_CREATE = "XDASHBOARD_CREATE";
	}
	
	/**
	 * 指标埋点数据类型
	 */
	public static class INDEX_CLICK_DATA_TYPE {
		public static final String CLICK = "click";
		public static final String SEARCH = "search";
	}
	
	/**
	 * 资源树报表查询的类型 使用方法searchCatalogElementLikeAliasByType查询
	 */
	public static class RESTREE_REPORT_TYPE{
		//即席查询
		public static final String COMBINED_QUERY = "COMBINED_QUERY";
		//透视分析
		public static final String INSIGHT = "INSIGHT";
		//交互式仪表盘
		public static final String SMARTBIX_PAGE = "SMARTBIX_PAGE";
		//电子表格
		public static final String SPREADSHEET_REPORT = "SPREADSHEET_REPORT";
		//WEB电子表格
		public static final String WEB_SPREADSHEET_REPORT = "WEB_SPREADSHEET_REPORT";
		//灵活分析
		public static final String SIMPLE_REPORT = "SIMPLE_REPORT";
		//仪表分析
		public static final String Dashboard = "Dashboard";
		//多维分析
		public static final String OLAP_REPORT = "OLAP_REPORT";
		//指标报表
		public static final String METRIC_REPORT = "METRIC_REPORT";		
		//WEB连接
		public static final String URL = "URL";
		//本地模板
		public static final String FILE_RESOURCE = "FILE_RESOURCE";		
		//个人目录
		public static final String SELF_TREENODE = "SELF_TREENODE";
		//公共目录
		public static final String DEFAULT_TREENODE = "DEFAULT_TREENODE";
	}

	/**
	 * 资源树数据集查询的类型 使用方法searchCatalogElementLikeAliasByType查询
	 */
	public static class RESTREE_DATASET_TYPE{
		//存储过程查询
		public static final String PROC_BUSINESS_VIEW = "PROC_BUSINESS_VIEW";
		//SQL查询
		public static final String TEXT_BUSINESS_VIEW = "TEXT_BUSINESS_VIEW";
		//原生SQL查询
		public static final String RAWSQL_BUSINESS_VIEW = "RAWSQL_BUSINESS_VIEW";
		//Java查询
		public static final String JAVA_BUSINESS_VIEW = "JAVA_BUSINESS_VIEW";
		//可视化查询
		public static final String BUSINESS_VIEW = "BUSINESS_VIEW";
		//自助数据集
		public static final String SMARTBIX_DATASET = "SMARTBIX_DATASET";
		//数据模型
		public static final String MT_DATAMODELS = "MT_DATAMODELS";
	}	
	
	/**
	 * 资源权限类型
	 */
	public static class PURVIEWTYPE{
		//没有权限
		public static final String NONE = "NONE";
		//引用权限
		public static final String REF = "REF";
		//查看权限
		public static final String READ = "READ";
		//编辑权限
		public static final String WRITE = "WRITE";
		//删除权限（旧版本才有）
		public static final String DELETE = "DELETE";
		//再授权
		public static final String GRANT = "GRANT";
		//所有者
		public static final String OWNER = "OWNER";
		//概览
		public static final String OVERVIEW = "OVERVIEW";
	}
	
}
