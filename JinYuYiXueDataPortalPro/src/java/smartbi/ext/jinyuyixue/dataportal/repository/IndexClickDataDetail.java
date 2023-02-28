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
 * 指标点击、查询次数埋点详情 实体类
 * 
 */
@Entity
@Table(name = "t_ext_index_click_detail")
@NamedQueries({
		@NamedQuery(name = "IndexClickDataDetail.getIndexClickDataDetailById", query = "from IndexClickDataDetail ri where ri.id = ?"),
		@NamedQuery(name = "IndexClickDataDetail.getIndexClickDataDetailByIndexId", query = "from IndexClickDataDetail ri where ri.indexId = ?"),
		@NamedQuery(name = "IndexClickDataDetail.getIndexClickDataDetailByUserId", query = "from IndexClickDataDetail ri where ri.userId = ?")})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "POJO")
@JSONFields(fields = { "c_id", "c_index_id", "c_user_id", "c_type", "c_datetime"})
public class IndexClickDataDetail implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 主键id 指标id
	 */
    @Id
    @Column(name = "c_id", nullable = false)
	private String id;
	/**
	 * 指标id
	 */
    @Column(name = "c_index_id")
	private String indexId;
	/**
	 * 用户id
	 */
    @Column(name = "c_user_id")
	private String userId;
	/**
	 * 类型 click-点击 search-查询
	 */
    @Column(name = "c_type")
	private String type;
	/**
	 * 创建时间
	 */
    @Column(name = "c_datetime")
	private Date createDateTime;
    
	public IndexClickDataDetail() {
	}
	
	public IndexClickDataDetail(String id, String indexId, String userId, String type, Date createDateTime) {
		super();
		this.id = id;
		this.indexId = indexId;
		this.userId = userId;
		this.type = type;
		this.createDateTime = createDateTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIndexId() {
		return indexId;
	}

	public void setIndexId(String indexId) {
		this.indexId = indexId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

}
