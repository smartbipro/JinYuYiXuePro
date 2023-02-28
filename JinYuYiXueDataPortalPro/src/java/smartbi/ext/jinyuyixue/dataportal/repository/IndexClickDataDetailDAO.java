package smartbi.ext.jinyuyixue.dataportal.repository;

import java.util.List;

import smartbi.repository.AbstractDAO;
import smartbi.repository.DAOModule;

/**
 * 指标点击查询记录次数埋点明细 实现类
 */
public class IndexClickDataDetailDAO extends AbstractDAO<IndexClickDataDetail, String> {
	private static IndexClickDataDetailDAO instance = new IndexClickDataDetailDAO();

	/**
	 * 指标点击查询次数埋点明细对象dao
	 * @return 下发对象dao
	 */
	public static IndexClickDataDetailDAO getInstance() {
		return instance;
	}

	IndexClickDataDetailDAO() {
		super(DAOModule.getInstance());
	}

	/**
	 * 根据id获取对象
	 * @param id 主键id
	 * @return
	 */
	public IndexClickDataDetail getIndexClickDataById(String id) {
		List<IndexClickDataDetail> list = this.findByNamedQuery("IndexClickDataDetail.getIndexClickDataDetailById", 
				new Object[] { id });
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	
	/**
	 * 根据指标ID获取指标点击、查询次数列表
	 * @param indexId
	 * @return
	 */
	public List<IndexClickDataDetail> getIndexClickDataDetailByIndexId(String indexId){
		List<IndexClickDataDetail> list = this.findByNamedQuery("IndexClickDataDetail.getIndexClickDataDetailByIndexId", 
				new Object[] { indexId });	
		return list;
	}
	
	/**
	 * 根据用户id获取指标点击、查询次数列表
	 * @param userId
	 * @return
	 */
	public List<IndexClickDataDetail> getIndexClickDataDetailByUserId(String indexId){
		List<IndexClickDataDetail> list = this.findByNamedQuery("IndexClickDataDetail.getIndexClickDataDetailByUserId", 
				new Object[] { indexId });	
		return list;
	}	

}
