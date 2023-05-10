package smartbi.ext.jinyuyixue.dataportal.upgrade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smartbi.util.DBType;
import smartbi.util.ValueType;
import smartbi.repository.AbstractUpgradeTask;
import java.sql.Connection;

/**
 * 报表详情表
 */
public class UpgradeTask_0_0_2 extends AbstractUpgradeTask {
	/**
	 * 日志对象
	 */
    private static final Logger LOG = LoggerFactory.getLogger(UpgradeTask_0_0_1.class);

    public boolean doUpgrade(Connection conn, DBType dbType) {
        try {
            // 表名
            String tableName = "t_ext_report_detail";
            // 判断表是否存在
            if (this.isTableExists(tableName)) {
                LOG.info(tableName + "表已经存在！");
                return true;
            }
            this.createTable(tableName,
					new String[] {"c_reportid", "c_reportalias", "c_reportdesc", "c_createuserid", "c_updateuserid", "c_imagepath", "c_imagename", "c_createtime", "c_updatetime"},
					new ValueType[] {ValueType.STRING, ValueType.STRING, ValueType.STRING, ValueType.STRING, ValueType.STRING, ValueType.STRING, ValueType.STRING, ValueType.DATETIME, ValueType.DATETIME},
					new boolean[] {false, true, true, true, true, true, true, true, true}, 
					new String[] {"c_reportid"},
					new int[] { 255, 255, 255, 255, 255, 255, 255, 0, 0});            
            return true;
        } catch (Exception e) {
            LOG.error("Upgrade UpgradeTask_New '" + this.getClass().getPackage().getName() + "' to " + getNewVersion() + " fail.", e);
            return false;
        }
    }

    public String getNewVersion() {
        return "0.0.3";
    }

    public String getDate() {
        return "2023-12-26 00:00:00";
    }
}