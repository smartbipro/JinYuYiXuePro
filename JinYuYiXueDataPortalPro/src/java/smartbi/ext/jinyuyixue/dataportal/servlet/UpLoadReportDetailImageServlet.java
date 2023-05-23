package smartbi.ext.jinyuyixue.dataportal.servlet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smartbi.catalogtree.CatalogElement;
import smartbi.catalogtree.CatalogTreeModule;
import smartbi.config.SystemConfigService;
import smartbi.ext.jinyuyixue.dataportal.repository.ReportDetail;
import smartbi.ext.jinyuyixue.dataportal.repository.ReportDetailDAO;
import smartbi.net.sf.json.JSONObject;
import smartbi.usermanager.UserManagerModule;
import smartbi.util.StringUtil;
import smartbi.util.UUIDGenerator;

/**
 * 
 * 说明：图片上传下载
 * 修改记录:
 */
public class UpLoadReportDetailImageServlet extends HttpServlet {
	
	/**
	 * 日志对象
	 */
    private static final Logger LOG = LoggerFactory.getLogger(UpLoadReportDetailImageServlet.class);
    
    /**
     * 报表明细dao对象
     */
    private ReportDetailDAO reportDetailDao = ReportDetailDAO.getInstance();
    /**
     * 资源树对象
     */
    private CatalogTreeModule catalogTreeModule = CatalogTreeModule.getInstance();   
    
    /**
     * 用户对象
     */
    private UserManagerModule userModule = UserManagerModule.getInstance();

	public void init(ServletConfig config) throws ServletException {
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		doPost(request, response);
	}
	
	/**
	 * 
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String type = request.getParameter("type");
		if (StringUtil.equals(type, "download") || StringUtil.equals(type, "view")) {
			this.downloadImage(request, response,type);
		}else{
			this.uploadImage(request, response);
		}
	}

	/**
	 * 上传文件
	 * @param request
	 * @param response
	 * @param type
	 * @throws ServletException
	 * @throws IOException
	 */
	private void uploadImage(HttpServletRequest request,HttpServletResponse response) 
			throws ServletException, IOException {
		try {
			
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding("UTF-8");
			@SuppressWarnings("unchecked")
			List<FileItem> itemList = upload.parseRequest(request);

			FileItem file = null;
			String type = null;
			String resId = null;
			for (FileItem item : itemList) {
				String fieldName = item.getFieldName();
				if (fieldName.equals("uploadReportDetailFile")) {
					file = item;
				} else if (fieldName.equals("resid")) {
					resId = item.getString("UTF-8");
				} else if (fieldName.equals("type")) {
					type = item.getString("UTF-8");
				}
			}
			if (StringUtil.equals(type, "upload")) {
				if (file != null) {
					String fileNameTemp = file.getName();
					String fileName = fileNameTemp.substring(fileNameTemp.lastIndexOf("\\")+1); //解决ie11获得路径问题					
					
					String filePath = SystemConfigService.getInstance().getValue("JYYX_REPORT_DETAIL_PATH");
					if(StringUtil.isNullOrEmpty(filePath)) {
						String info = getFailMsgHtml(StringUtil.getLanguageValue("JYYX_REPORT_DETAIL_IMAGE_FAIL"));
						//"当前没有配置报表缩略图路径，无法操作，请在系统选项中设置正确的路径。"
						response.getOutputStream().write(info.getBytes("UTF-8"));
						return;
					}
					if(filePath.substring(filePath.length() - 1) != File.separator) {
						filePath += File.separator;
					}
					String imageName = UUIDGenerator.generate() +  fileName.substring(fileName.lastIndexOf("."));
					String reportFileName = filePath + imageName;
					File imgFile = new File(reportFileName);
					InputStream in = file.getInputStream();
					try {
						FileOutputStream fout = new FileOutputStream(imgFile);
						int l = -1;
						byte[] tmp = new byte[1024];
						while((l = in.read(tmp, 0, 1024)) != -1) {
							fout.write(tmp, 0, l);
						}
						fout.flush();
						fout.close();
						saveReportDetail(resId, filePath, imageName, fileName);
					}catch(Exception e) {
						LOG.error("上传报表缩略图报错:" + e.getMessage());
					}finally {
						in.close();
					}
					if (!fileName.endsWith(".png") && !fileName.endsWith(".jpg") 
							&& !fileName.endsWith(".jpeg") && !fileName.endsWith(".icon")
							&& !fileName.endsWith(".bmp") && !fileName.endsWith("gif")) {
						StringBuffer sb = new StringBuffer();
						sb.append("<head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/></head>");
						sb.append("<script>");
						sb.append("parent.alert('" + StringUtil.getLanguageValue("Filetypeisnotcorrect")
								+ StringUtil.getLanguageValue("Exclamation") + "')");
						sb.append("</script>");
						String info = sb.toString();
						response.getOutputStream().write(info.getBytes("UTF-8"));
						return;
					}
					String info = getSuccessMsgHtml(fileName);
					response.getOutputStream().write(info.getBytes("UTF-8"));
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			String info = getFailMsgHtml(StringUtil.getLanguageValue("Uploadfailed")
					+ StringUtil.getLanguageValue("Exclamation") );
			response.getOutputStream().write(info.getBytes("UTF-8"));
		}
	}
	
	/**
	 * 将图的信息保存到数据库中
	 * @param resId         报表id
	 * @param imagePath     报表路径
	 * @param imageName     以报表资源id作为报表的名称+后缀名称
	 * @param srcImangeName 原始图片的名称，保存到表中的remark字段中
	 */
	private void saveReportDetail(String resId, String imagePath, String imageName, String srcImangeName) {
		if(null != resId) {
			ReportDetail detail = reportDetailDao.load(resId);
			if(null != detail) {
				String tmpFileStr = detail.getTmpImagePath() + detail.getTmpImageName();
				//当状态为1(已完成)时，临时文件不等于确认文件时可以删除
				//当状态为0(编辑)时，临时文件可以删除
				if (("1".equals(detail.getStatus()) && !detail.getTmpImageName().equals(detail.getImageName())) 
						|| "0".equals(detail.getStatus())){
					File tmpFile = new File(tmpFileStr);
					if(tmpFile.exists()) {
						tmpFile.delete();
					}					
				}				
				detail.setUpdateUserId(userModule.getCurrentUser().getId());
				detail.setUpdateDateTime(new Date());
				detail.setTmpImagePath(imagePath);
				detail.setTmpImageName(imageName);
				detail.setStatus("0");
				reportDetailDao.update(detail);
			} else {
				CatalogElement element = catalogTreeModule.getCatalogElementById(resId);
				String alias = element.getAlias();
				String type = element.getType();
				String desc = element.getDesc();
				detail = new ReportDetail();
				detail.setReportId(resId);
				detail.setReportAlias(alias);
				detail.setReportType(type);
				detail.setReportDesc(desc);
				detail.setCreateUserId(userModule.getCurrentUser().getId());
				detail.setCreateDateTime(new Date());
				detail.setTmpImagePath(imagePath);
				detail.setTmpImageName(imageName);
				detail.setUpdateUserId(userModule.getCurrentUser().getId());
				detail.setUpdateDateTime(new Date());
				detail.setRemark(srcImangeName);
				reportDetailDao.save(detail);
			}
		}
	}
	
	/**
	 * 获取正确的提醒html
	 * @param fileName
	 * @return
	 */
	private String getSuccessMsgHtml(String fileName) {
		StringBuffer sb = new StringBuffer();
		sb.append("<head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/></head>");
		sb.append("<script>");
		sb.append("parent.alert('" + StringUtil.getLanguageValue("Uploadsuccessful")
				+ StringUtil.getLanguageValue("Exclamation") + "')");
		sb.append("</script>");
		sb.append("<body>");
		sb.append("<span id='fileName'>");
		sb.append(StringUtil.replaceHTML2(fileName));
		sb.append("</span>");
		sb.append("</body>");
		return sb.toString();
	}
	
	/**
	 * 获取错误的提醒html
	 * @param msg
	 * @return
	 */
	private String getFailMsgHtml(String msg) {
		StringBuffer sb = new StringBuffer();
		sb.append("<head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/></head>");
		sb.append("<script>");
		sb.append("parent.alert('" + msg + "')");
		sb.append("</script>");
		return sb.toString();
	}
	
	
	/**
	 * 下载或预览文件
	 * @param request
	 * @param response
	 * @param type
	 * @throws ServletException
	 * @throws IOException
	 */
	private void downloadImage(HttpServletRequest request,HttpServletResponse response,String type) 
			throws ServletException, IOException {
		FileInputStream in = null;
		try {
			JSONObject obj = this.getImageContent(request);
			
			if (obj != null) {
				String name = obj.optString("downFileName");
				name = URLEncoder.encode(name, "UTF-8");
				
				if (StringUtil.equals(type, "download")) {
					response.setHeader("Content-Disposition","attachment;filename=" + name);
				}
				String mimeType = request.getSession().getServletContext().getMimeType("gif");

				if (mimeType == null){
					mimeType = "application/octet-stream";
				}
				response.setContentType(mimeType);
				BufferedOutputStream buff = new BufferedOutputStream(response.getOutputStream());
				String reportFileName = obj.optString("imageFilePath");
				if(StringUtil.isNullOrEmpty(reportFileName)) {
					buff.write(new byte[0]);
				}else {
					File imgFile = new File(reportFileName);
					in = new FileInputStream(imgFile);
					int l = -1;
					byte[] tmp = new byte[1024];
					while((l = in.read(tmp, 0, 1024)) != -1) {
						buff.write(tmp, 0, l);
					}
				}
				buff.flush();
				buff.close();
			} else
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}finally {
			if (in != null) {
				in.close();
			}
		}
	}
	
	/**
	 * 获取图片信息
	 * @param request
	 * @return
	 */
	private JSONObject getImageContent(HttpServletRequest request){
		String resId = request.getParameter("resid");
		ReportDetail detail = null;
		if(null != resId) {
			detail = ReportDetailDAO.getInstance().load(resId);
		}
		JSONObject object = new JSONObject();
		if(null != detail) {
			String path = detail.getImagePath();
			String imageName = detail.getImageName();
			String reportId = detail.getReportId();
			String reportAlias = detail.getReportAlias();
			
			if((!StringUtil.isNullOrEmpty(path)) && (path.substring(path.length() - 1) != File.separator)) {
				path += File.separator;
			}
			//临时请求时
			if(request.getParameter("tempimage") != null) {
				path = detail.getTmpImagePath();
				imageName = detail.getTmpImageName();
			}
			String suffix = imageName.substring(imageName.lastIndexOf("."));
			object.put("reportId", reportId);
			object.put("reportAlias", reportAlias);
			object.put("imageFilePath", path + imageName);			
			object.put("downFileName", reportAlias + suffix);
		}
		return object;
	}

} 