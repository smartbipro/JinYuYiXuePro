package smartbi.ext.jinyuyixue.dataportal.module;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smartbi.catalogtree.CatalogElement;
import smartbi.catalogtree.CatalogTreeModule;
import smartbi.catalogtree.ResourceTreeNode;
import smartbi.catalogtree.ResourceTreeNodeDAO;
import smartbi.ext.jinyuyixue.dataportal.util.CommonUtils;
import smartbi.net.sf.json.JSONArray;
import smartbi.net.sf.json.JSONObject;
import smartbi.usermanager.UserManagerModule;
import smartbi.util.StringUtil;
/**
 * 菜单功能点实现类
 */
public class MemuModule {
	/**
	 * 日志对象
	 */
    private static final Logger LOG = LoggerFactory.getLogger(MemuModule.class);
    /**
     * 资源树对象
     */
    private CatalogTreeModule catalogTreeModule = CatalogTreeModule.getInstance(); 
    /**
     * 用户管理module对象
     */
    private UserManagerModule userManagerModule = UserManagerModule.getInstance();
    /**
     * 菜单模块对象
     */
    private static MemuModule instance;

    public static MemuModule getInstance() {
        if (instance == null) {
            instance = new MemuModule();
        }
        return instance;
    }
    
	/**
	 * 重命名报表
	 * 
	 * @param resId
	 *            需要重命名的资源id 此处指新的资源别称
	 */
	public JSONObject renamePortalResource(String resId, String alias) {
		JSONObject result = new JSONObject();
		result.put("success", true);
		// 先重命名魔数师实际的资源，重命名成功之后，再重命名t_portal_analysis表中存的名称
		ResourceTreeNode node = (ResourceTreeNode) ResourceTreeNodeDAO.getInstance().load(resId);

		// 先检测同级目录下是否存在同名资源
		JSONObject checkData = checkNodeAlias(node, alias);
		if(!checkData.optBoolean("success", true)) {
			return checkData;
		}

		// node为空的原因：1、这个resId对应的是魔数师首页新建的目录，存在于t_portal_analysis表中；
		// 2、不存在这个资源
		if (node != null) {
			// 旧方法只改变了alias这一列的值，没有对应更新表头等其他属性，改用其他方式，直接调用产品的updateCatalogNode方法。
			JSONObject jsonNode = new JSONObject();
			jsonNode.put("alias", alias);
			jsonNode.put("desc", alias);
			catalogTreeModule.updateCatalogNode(resId, jsonNode.toString(), null);
			return result;
		} else {
			// 提示不存在此资源
			LOG.info("报表" + resId + "门户中不存在！");
			result.put("success", false);			
			result.put("errorMsg", "没有找到指定的资源节点: 自助分析资源不存在,重命名失败！");
			return result;
		}
	}    
	
	/**
	 * 重命名报表调用，检测此资源同级目录下是否存在同名资源，假如存在，直接抛异常。 参考CatalogTreeModule.checkNodeAlias
	 * 
	 * @param node
	 *            需要被检测的资源
	 * @param alias
	 *            名称
	 * @return 
	 */
	@SuppressWarnings("rawtypes")
	private JSONObject checkNodeAlias(ResourceTreeNode node, String alias) {
		JSONObject result = new JSONObject();
		result.put("success", true);
		if (node != null && !StringUtil.isNullOrEmpty(alias)) {
			List<ResourceTreeNode> nodes = ResourceTreeNodeDAO.getInstance().getSameAliasNodes(node.getParentNode(),
					alias);
			Iterator iterator = nodes.iterator();
			while (iterator.hasNext()) {
				ResourceTreeNode tempNode = (ResourceTreeNode) iterator.next();
				if (!tempNode.getId().equals(node.getId())) {
					result.put("success", false);
					result.put("errorMsg", "已存在同名的对象:" + alias);
					break;
				}
			}
		}
		return result;
	}	
	
	/**
	 * 将资源移动到某个目录下	
	 * @param resId   资源id
	 * @param toDirResId   目录资源id
	 * @return
	 */
	public JSONObject moveCatalogElement(String resId, String toDirResId) {
		JSONObject result = new JSONObject();
		try {
			result.put("success", true);
			CatalogElement srcElement = catalogTreeModule.getCatalogElementById(resId);
			if(srcElement == null) {
				result.put("success", false);
				result.put("errorMsg", "资源id：[" + resId + "]不存在!");
				return result;
			}
			CatalogElement dstElement = catalogTreeModule.getCatalogElementById(toDirResId);
			if(dstElement == null) {
				result.put("success", false);
				result.put("errorMsg", "目标目录资源id：[" + toDirResId + "]不存在!");
				return result;
			}
			if(!(dstElement.getType().equals("DEFAULT_TREENODE") || dstElement.getType().equals("SELF_TREENODE"))) {
				result.put("success", false);
				result.put("errorMsg", "目标资源非目录，不允许移动到该资源下，id：[" + toDirResId + "]!");
				return result;
			}
			
			ResourceTreeNode node = (ResourceTreeNode) ResourceTreeNodeDAO.getInstance().load(toDirResId);

			// 先检测同级目录下是否存在同名资源
			JSONObject checkData = checkNodeAlias(node, srcElement.getAlias());
			if(!checkData.optBoolean("success", true)) {
				return checkData;
			}			
			
			catalogTreeModule.moveCatalogElement(resId, toDirResId);
		} catch(Exception e) {
			result.put("success", false);
			result.put("errorMsg", "移动目录错误:" + e.getMessage());
		}
		return result;
	}
	
	/**
	 * 根据资源id获取该资源的root根目录的目录数据
	 * @param resId 资源id
	 * @return
	 */
	public JSONObject getDefaultDirByResId(String resId) {
		JSONObject result = new JSONObject();
		result.put("success", true);
		String path = catalogTreeModule.getCatalogElementFullPath(resId);
		if(StringUtil.isNullOrEmpty(path)) {
			result.put("success", false);
			result.put("errorMsg", "系统中不存在resId=[" + resId + "]的资源");
			return result;
		}
		String rootPathResId = null;
		if (path.indexOf("我的空间") == 0) {
			rootPathResId = "SELF_" + userManagerModule.getCurrentUser().getId();
		} else if (path.indexOf("分析报表") == 0) {
			rootPathResId = "PUBLIC_ANALYSIS";
		} else if (path.indexOf("公共空间") == 0) {
			rootPathResId = "DEFAULT_TREENODE";
		}
		
		if(StringUtil.isNullOrEmpty(rootPathResId)) {
			result.put("success", false);
			result.put("errorMsg", "资源id的root目录id找不到，resId=[" + resId + "]的资源");
			return result;			
		}
		CatalogElement element = catalogTreeModule.getCatalogElementById(rootPathResId);
		result.put("data", CommonUtils.createJsonByElement(element));
		return result;
	}
	
	/**
	 * 根据目录接待获取下级目录节点
	 * @param dirId 目录资源id
	 * @return
	 */
	public JSONObject getChildDirByDirId(String dirId) {
		//产品前端的个人目录需要排除以下的目录节点
		//包含的目录节点有：数据连接(SELF_DATACONNECTION_ADMIN)、自助ETL(SELF_ETL_ADMIN)、ETL自动化(SELF_ETL_AUTOMATION_ADMIN)、作业流(SELF_JOBFLOW_ADMIN)
		//业务主题(SELF_BUSINESSTHEME_ADMIN)、数据集(SELF_DATASET_ADMIN)、数据挖掘(SELF_DATAMINING_ADMIN)、任务(SELF_TASK_ADMIN)、计划(SELF_SCHEDULE_ADMIN)		
		//分析报表需要显示出来 分析报表(SELF_ANALYSIS_ADMIN)
		List<String> excludeDirList = getCurrentUserExcludeDirList();
		JSONObject result = new JSONObject();
		try {
			result.put("success", true);
			List<CatalogElement> list = catalogTreeModule.getChildElementsByTypes(dirId, new String[] {"DEFAULT_TREENODE", "SELF_TREENODE"});
			if(list == null && list.size() == 0) {
				result.put("data", new JSONArray());
				return result;
			}
			JSONArray data = new JSONArray();
			for(CatalogElement element : list) {
				if("DEFAULT_TREENODE".equals(element.getType()) || "SELF_TREENODE".equals(element.getType())) {
					//获取到个人空间的数据时，会将数据连接、自助ETL、ETL自动化、作业流、业务主题、数据集、数据挖掘、任务、计划的子目录获取到，需要排除这些目录
					//只需要获取到公共目录和我的空间下的子目录即可
					if(excludeDirList.contains(element.getId())) {
						continue;
					}
					data.put(CommonUtils.createJsonByElement(element));
				}
			}
			result.put("data", data);
		}catch(Exception e){
			result.put("success", false);
			result.put("data", new JSONArray());
		}
		return result;
	}

	/**
	 * 产品前端的个人目录需要排除以下的目录节点 排除的节点列表数据集
	 * @return
	 */
	private List<String> getCurrentUserExcludeDirList() {
		List<String> result = new ArrayList<String>();
		String currentUserId = userManagerModule.getCurrentUser().getId();
		result.add("SELF_DATACONNECTION_" + currentUserId);//数据连接
		result.add("SELF_ETL_" + currentUserId);//自助ETL
		result.add("SELF_ETL_AUTOMATION_" + currentUserId);//ETL自动化
		result.add("SELF_JOBFLOW_" + currentUserId);//作业流
		result.add("SELF_BUSINESSTHEME_" + currentUserId);//业务主题
		result.add("SELF_DATASET_" + currentUserId);//数据集
		result.add("SELF_DATAMINING_" + currentUserId);//数据挖掘
		result.add("SELF_TASK_" + currentUserId);//任务
		result.add("SELF_SCHEDULE_" + currentUserId);//计划
		return result;
	}    
}
