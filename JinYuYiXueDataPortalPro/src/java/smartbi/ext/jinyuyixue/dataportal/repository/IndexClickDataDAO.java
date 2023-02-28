package smartbi.ext.jinyuyixue.dataportal.repository;

import java.util.List;

import smartbi.repository.AbstractDAO;
import smartbi.repository.DAOModule;

/**
 * 指标点击查询记录次数埋点 实现类
 */
public class IndexClickDataDAO extends AbstractDAO<IndexClickData, String> {
	private static IndexClickDataDAO instance = new IndexClickDataDAO();

	/**
	 * 指标点击查询次数埋点对象dao
	 * @return 下发对象dao
	 */
	public static IndexClickDataDAO getInstance() {
		return instance;
	}

	IndexClickDataDAO() {
		super(DAOModule.getInstance());
	}

	/**
	 * 根据id获取对象
	 * 
	 * @param id
	 *            指标主键id
	 * @return
	 */
	public IndexClickData getIndexClickDataById(String indexId) {
		List<IndexClickData> list = this.findByNamedQuery("IndexClickData.getIndexClickDataById", 
				new Object[] { indexId });
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

}
