package smartbi.ext.jinyuyixue.dataportal.module;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smartbi.catalogtree.CatalogTreeModule;
/**
 * 数据集模块实现类
 */
public class DatasetModule {
	/**
	 * 日志对象
	 */
    private static final Logger LOG = LoggerFactory.getLogger(DatasetModule.class);
    /**
     * 资源树对象
     */
    private CatalogTreeModule catalogTreeModule = CatalogTreeModule.getInstance();    
    /**
     * 数据集模块对象
     */
    private static DatasetModule instance;

    public static DatasetModule getInstance() {
        if (instance == null) {
            instance = new DatasetModule();
        }
        return instance;
    }
    
}
