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
 * 报表详情 实体类
 * 
 */
@Entity
@Table(name = "t_ext_report_detail")
@NamedQueries({
		@NamedQuery(name = "ReportDetail.getReportDetailById", query = "from ReportDetail ri where ri.reportId = ?")})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "POJO")
@JSONFields(fields = { "reportId", "reportAlias", "reportType", "reportDesc", "createUserId", "updateUserId", "imagePath", "imageName", "tmpImagePath", "tmpImageName", "createDateTime", "updateDateTime", "remark"})
public class ReportDetail implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 报表资源id
	 */
    @Id
    @Column(name = "c_reportid", nullable = false)
	private String reportId;
	/**
	 * 报表别名
	 */
    @Column(name = "c_reportalias")
	private String reportAlias;
    /**
     * 文件类型
     */
    @Column(name = "c_reporttype")
    private String reportType;    
	/**
	 * 报表备注
	 */
    @Column(name = "c_reportdesc")
	private String reportDesc;
	/**
	 * 创建人id
	 */
    @Column(name = "c_createuserid")
	private String createUserId;
	/**
	 * 修改人id
	 */
    @Column(name = "c_updateuserid")
	private String updateUserId;   
	/**
	 * 缩略图路径
	 */
    @Column(name = "c_imagepath")
	private String imagePath;   
    /**
	 * 缩略图名称
	 */
    @Column(name = "c_imagename")
	private String imageName;   
    
    /**
	 * 缩略图临时路径
	 */
    @Column(name = "c_tmpimagepath")    
    private String tmpImagePath;
    
    /**
	 * 缩略图名称
	 */
    @Column(name = "t_tmpimagename")    
    private String tmpImageName;
    
	/**
	 * 创建时间
	 */
    @Column(name = "c_createtime")
	private Date createDateTime;
	/**
	 * 更新时间
	 */
    @Column(name = "c_updatetime")
    private Date updateDateTime;   
    
   /**
    * 说明
    */
    @Column(name = "c_remark")
    private String remark;
    
	public ReportDetail() {
	}

	
	/**
	 * 报表详情析构类
	 * @param reportId         报表资源id
	 * @param reportAlias      报表别名
	 * @param reportType       文件类型
	 * @param reportDesc       报表详情说明
	 * @param createUserId     创建人用户id
	 * @param updateUserId     更新热用户id 
	 * @param imagePath        缩略图路径
	 * @param imageName        缩略图名称
	 * @param tmpImagePath     缩略图临时路径
	 * @param tmpImageName     缩略图临时名称
	 * @param createDateTime   创建记录的时间
	 * @param updateDateTime   更新记录的时间
	 * @param remark           备注
	 */
	public ReportDetail(String reportId, String reportAlias, String reportType, String reportDesc, String createUserId,
			String updateUserId, String imagePath, String imageName, String tmpImagePath, String tmpImageName,
			Date createDateTime, Date updateDateTime, String remark) {
		super();
		this.reportId = reportId;
		this.reportAlias = reportAlias;
		this.reportType = reportType;
		this.reportDesc = reportDesc;
		this.createUserId = createUserId;
		this.updateUserId = updateUserId;
		this.imagePath = imagePath;
		this.imageName = imageName;
		this.tmpImagePath = tmpImagePath;
		this.tmpImageName = tmpImageName;
		this.createDateTime = createDateTime;
		this.updateDateTime = updateDateTime;
		this.remark = remark;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getReportAlias() {
		return reportAlias;
	}

	public void setReportAlias(String reportAlias) {
		this.reportAlias = reportAlias;
	}
	

	public String getReportType() {
		return reportType;
	}


	public void setReportType(String reportType) {
		this.reportType = reportType;
	}


	public String getReportDesc() {
		return reportDesc;
	}

	public void setReportDesc(String reportDesc) {
		this.reportDesc = reportDesc;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public String getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getTmpImagePath() {
		return tmpImagePath;
	}

	public void setTmpImagePath(String tmpImagePath) {
		this.tmpImagePath = tmpImagePath;
	}

	public String getTmpImageName() {
		return tmpImageName;
	}

	public void setTmpImageName(String tmpImageName) {
		this.tmpImageName = tmpImageName;
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


	public String getRemark() {
		return remark;
	}
	
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
