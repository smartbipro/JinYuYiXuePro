package smartbi.ext.jinyuyixue.dataportal.module;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 报表模块实现类
 */
public class ReportModule {
	/**
	 * 日志对象
	 */
    private static final Logger LOG = LoggerFactory.getLogger(ReportModule.class);
    /**
     * 报表模块对象
     */
    private static ReportModule instance;

    public static ReportModule getInstance() {
        if (instance == null) {
            instance = new ReportModule();
        }
        return instance;
    }
    
}
