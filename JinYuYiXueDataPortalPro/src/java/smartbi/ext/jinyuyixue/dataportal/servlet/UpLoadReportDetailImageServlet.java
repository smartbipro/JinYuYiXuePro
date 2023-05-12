package smartbi.ext.jinyuyixue.dataportal.servlet;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Date;
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

import smartbi.config.SystemConfigService;
import smartbi.net.sf.json.JSONObject;
import smartbi.uploadimg.repository.UploadImage;
import smartbi.uploadimg.repository.UploadImageDAO;
import smartbi.util.FileUtil;
import smartbi.util.StringUtil;
import smartbi.util.UUIDGenerator;
import sun.misc.BASE64Decoder;

/**
 * 
 * 说明：图片上传下载<br>
 * @author:meteor
 * 创建时间:2014-9-5<br>
 * 修改记录:
 */
public class UpLoadReportDetailImageServlet extends HttpServlet {
	
	/**
	 * 日志对象
	 */
    private static final Logger LOG = LoggerFactory.getLogger(UpLoadReportDetailImageServlet.class);

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
					
					String filePath = SystemConfigService.getInstance().getLongValue("JYYX_REPORT_DETAIL_PATH");
					if(StringUtil.isNullOrEmpty(filePath)) {
						String info = getFailMsgHtml("当前没有配置报表缩略图路径，无法操作，请设置正确的路径。");
						response.getOutputStream().write(info.getBytes("UTF-8"));
						return;
					}
					if(filePath.substring(filePath.length() - 1) != File.separator) {
						filePath += File.separator;
					}
					String reportFileName = filePath + resId +  fileName.substring(fileName.lastIndexOf("."));
					FileOutputStream fileOutputstream = new FileOutputStream(reportFileName);
					
					
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					(new sun.misc.BASE64Encoder()).encode(file.getInputStream(), baos);

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
					String imageContent = baos.toString();
					
					String id = this.saveImageContent(itemList, fileName, imageContent, resId);
					String info = getSuccessMsgHtml(fileName);
//					StringBuffer sb = new StringBuffer();
//					sb.append("<head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/></head>");
//					sb.append("<script>");
//					sb.append("parent.alert('" + StringUtil.getLanguageValue("Uploadsuccessful")
//							+ StringUtil.getLanguageValue("Exclamation") + "')");
//					sb.append("</script>");
//					sb.append("<body>");
//					sb.append("<span id='fileId'>");
//					sb.append(id);
//					sb.append("</span>");
//					sb.append("<span id='fileName'>");
//					sb.append(StringUtil.replaceHTML2(fileName));
//					sb.append("</span>");
//					sb.append("</body>");
//					info = sb.toString();
					response.getOutputStream().write(info.getBytes("UTF-8"));
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			String info = getFailMsgHtml(StringUtil.getLanguageValue("Uploadfailed")
					+ StringUtil.getLanguageValue("Exclamation") );
//			StringBuffer sb = new StringBuffer();
//			sb.append("<head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/></head>");
//			sb.append("<script>");
//			sb.append("parent.alert('" + StringUtil.getLanguageValue("Uploadfailed")
//					+ StringUtil.getLanguageValue("Exclamation") + "')");
//			sb.append("</script>");
//			info = sb.toString();
			response.getOutputStream().write(info.getBytes("UTF-8"));
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
		BASE64Decoder decoder = new BASE64Decoder();
	
		try {
			JSONObject obj = this.getImageContent(request);
			
			if (obj != null) {
				byte[] fileContent;
				String content = obj.optString("imageFile");
				
				if (content == null)
					fileContent = new byte[0];
				else
					fileContent = decoder.decodeBuffer(content);
				String name = obj.optString("imageFileName");
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
				buff.write(fileContent);
				
				buff.flush();
				buff.close();
			} else
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * 获取图片信息
	 * @param request
	 * @return
	 */
	private JSONObject getImageContent(HttpServletRequest request){
		String imageId = request.getParameter("downloadId");
		UploadImage upImg = null;
		if(null != imageId){
			upImg = UploadImageDAO.getInstance().load(imageId);
		}
		
		JSONObject object = new JSONObject();
		if(null != upImg){
			String bgImage = upImg.getContent();
			String bgImageName = upImg.getName();
			object.put("imageFile", bgImage);
			object.put("imageFileName", bgImageName);
		}
		return object;
	}
	
	/**
	 * 保存图片信息
	 * @param itemList
	 * @param fileName
	 * @param imageContent
	 * @param resId
	 * @param imgMark
	 */
	private String saveImageContent(List<FileItem> itemList,String fileName,String imageContent,String imageId){
		if(null == imageContent){
			return "";
		}
		UploadImage upImg = null;
		if(null != imageId && !imageId.equals("")){
			upImg = UploadImageDAO.getInstance().load(imageId);
		}
		boolean isExists = true;
		if(null == upImg){
			isExists = false;
			upImg = new UploadImage();
			imageId = UUIDGenerator.generate();
			upImg.setId(imageId);
		}
		upImg.setLastModifiedDate(new Date(0));
		upImg.setContent(imageContent);
		upImg.setName(fileName);
		if(isExists){
			UploadImageDAO.getInstance().update(upImg);
		}else{
			upImg.setCreatedDate(new Date(0));
			UploadImageDAO.getInstance().save(upImg);
		}
		return imageId;
	}

} 