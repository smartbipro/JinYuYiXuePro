package smartbi.ext.jinyuyixue.dataportal.upgrade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smartbi.util.DBType;
import smartbi.util.ValueType;
import smartbi.repository.AbstractUpgradeTask;
import java.sql.Connection;

/**
 * 指标点击查询埋点主表
 */
public class UpgradeTask_New extends AbstractUpgradeTask {
	/**
	 * 日志对象
	 */
    private static final Logger LOG = LoggerFactory.getLogger(UpgradeTask_New.class);

    public boolean doUpgrade(Connection conn, DBType dbType) {
        try {
            // 表名
            String tableName = "t_ext_index_click";
            // 判断表是否存在
            if (this.isTableExists(tableName)) {
                LOG.info(tableName + "表已经存在！");
                return true;
            }
            this.createTable(tableName,
					new String[] {"c_index_id", "c_index_name", "c_click_num", "c_search_num", "c_datetime","c_updatetime"},
					new ValueType[] {ValueType.STRING, ValueType.STRING, ValueType.INTEGER, ValueType.INTEGER, ValueType.DATETIME, ValueType.DATETIME},
					new boolean[] {false, false, true, true, true, true}, 
					new String[] {"c_index_id"},
					new int[] { 255, 255, 0, 0, 0, 0});            
            return true;
        } catch (Exception e) {
            LOG.error("Upgrade UpgradeTask_New '" + this.getClass().getPackage().getName() + "' to " + getNewVersion() + " fail.", e);
            return false;
        }
    }

    public String getNewVersion() {
        return "0.0.1";
    }

    public String getDate() {
        return "2023-22-24 00:00:00";
    }
}