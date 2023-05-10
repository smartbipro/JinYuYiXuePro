package smartbi.ext.jinyuyixue.dataportal.repository;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import smartbi.util.JSONFields;

/**
 * 指标点击、查询次数埋点 实体类
 * 
 */
@Entity
@Table(name = "t_ext_index_click")
@NamedQueries({
		@NamedQuery(name = "IndexClickData.getIndexClickDataById", query = "from IndexClickData ri where ri.indexId = ?")})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "POJO")
@JSONFields(fields = { "indexId", "indexName", "clickNum", "searchNum", "createDateTime","updateDateTime"})
public class IndexClickData implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 主键id 指标id
	 */
    @Id
    @Column(name = "c_index_id", nullable = false)
	private String indexId;
	/**
	 * 指标名称
	 */
    @Column(name = "c_index_name")
	private String indexName;
	/**
	 * 指标点击次数
	 */
    @Column(name = "c_click_num")
	private int clickNum;
	/**
	 * 指标查询次数
	 */
    @Column(name = "c_search_num")
	private int searchNum;
	/**
	 * 创建时间
	 */
    @Column(name = "c_datetime")
	private Date createDateTime;
	/**
	 * 更新时间
	 */
    @Column(name = "c_updatetime")
    private Date updateDateTime;    
    
	public IndexClickData() {
	}

	public IndexClickData(String indexId, String indexName, int clickNum, int searchNum, Date createDateTime,
			Date updateDateTime) {
		super();
		this.indexId = indexId;
		this.indexName = indexName;
		this.clickNum = clickNum;
		this.searchNum = searchNum;
		this.createDateTime = createDateTime;
		this.updateDateTime = updateDateTime;
	}

	public String getIndexId() {
		return indexId;
	}

	public void setIndexId(String indexId) {
		this.indexId = indexId;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public int getClickNum() {
		return clickNum;
	}

	public void setClickNum(int clickNum) {
		this.clickNum = clickNum;
	}

	public int getSearchNum() {
		return searchNum;
	}

	public void setSearchNum(int searchNum) {
		this.searchNum = searchNum;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Date getUpdateDateTime() {
		return updateDateTime;
	}

	public void setUpdateDateTime(Date updateDateTime) {
		this.updateDateTime = updateDateTime;
	}

}
