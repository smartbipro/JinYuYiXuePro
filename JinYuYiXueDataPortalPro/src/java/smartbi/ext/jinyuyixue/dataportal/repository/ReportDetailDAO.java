package smartbi.ext.jinyuyixue.dataportal.repository;

import java.util.List;

import smartbi.repository.AbstractDAO;
import smartbi.repository.DAOModule;

/**
 * 报表详情 实现类
 */
public class ReportDetailDAO extends AbstractDAO<ReportDetail, String> {
	private static ReportDetailDAO instance = new ReportDetailDAO();

	/**
	 * 报表详情对象dao
	 * @return 下发对象dao
	 */
	public static ReportDetailDAO getInstance() {
		return instance;
	}

	ReportDetailDAO() {
		super(DAOModule.getInstance());
	}

	/**
	 * 根据id获取对象
	 * @param reportId  报表资源id
	 * @return
	 */
	public ReportDetail getReportDetailById(String reportId) {
		List<ReportDetail> list = this.findByNamedQuery("ReportDetail.getReportDetailById", 
				new Object[] { reportId });
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

}
