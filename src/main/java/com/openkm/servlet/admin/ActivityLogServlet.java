/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017  Paco Avila & Josep Llort
 * <p>
 * No bytes were intentionally harmed during the development of this application.
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.servlet.admin;

import com.openkm.api.OKMAuth;
import com.openkm.bean.ActivityLogExportBean;
import com.openkm.core.DatabaseException;
import com.openkm.dao.ActivityDAO;
import com.openkm.dao.UserDAO;
import com.openkm.dao.bean.ActivityFilter;
import com.openkm.dao.bean.OrganizationVTX;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.util.DownloadReportUtils;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;
import com.spire.doc.*;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Activity log servlet
 */
public class ActivityLogServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ActivityLogServlet.class);
	String actions[] = {
			"Auth",
			"LOGIN", "LOGOUT", "SESSION_EXPIRATION",
			"GRANT_USER", "REVOKE_USER", "GRANT_ROLE", "REVOKE_ROLE",

			//---------------------------------
			"Document",
			"CANCEL_DOCUMENT_CHECKOUT", "CHECKIN_DOCUMENT", "CHECKOUT_DOCUMENT", "CREATE_DOCUMENT",
			"DELETE_DOCUMENT", "GET_CHILDREN_DOCUMENTS", "GET_DOCUMENT_CONTENT",
			"GET_DOCUMENT_CONTENT_BY_VERSION", "GET_DOCUMENT_PROPERTIES",
			"GET_DOCUMENT_VERSION_HISTORY", "GET_PROPERTY_GROUP_PROPERTIES",
			"LOCK_DOCUMENT", "MOVE_DOCUMENT", "PURGE_DOCUMENT", "RENAME_DOCUMENT",
			"SET_DOCUMENT_PROPERTIES", "UNLOCK_DOCUMENT",

			//---------------------------------
			"Folder",
			"COPY_FOLDER", "CREATE_FOLDER", "DELETE_FOLDER", "GET_CHILDREN_FOLDERS",
			"GET_FOLDER_CONTENT_INFO", "GET_FOLDER_PROPERTIES", "MOVE_FOLDER", "PURGE_FOLDER",
			"RENAME_FOLDER",

			//---------------------------------
			"Mail",
			"CREATE_MAIL", "GET_MAIL_PROPERTIES", "DELETE_MAIL", "PURGE_MAIL", "RENAME_MAIL",
			"MOVE_MAIL", "COPY_MAIL", "GET_CHILDREN_MAILS",

			//---------------------------------
			"Repository",
			"PURGE_TRASH",

			//---------------------------------
			"Admin",
			"ADMIN_ACTIVITY_LOG", "ADMIN_ACTIVE_SESSIONS",
			"ADMIN_USER_CREATE", "ADMIN_USER_EDIT", "ADMIN_USER_DELETE", "ADMIN_USER_ACTIVE",
			"ADMIN_ROLE_CREATE", "ADMIN_ROLE_EDIT", "ADMIN_ROLE_DELETE", "ADMIN_ROLE_ACTIVE",
			"ADMIN_CHECK_EMAIL",
			"ADMIN_CONFIG_CREATE", "ADMIN_CONFIG_EDIT", "ADMIN_CONFIG_DELETE",
			"ADMIN_CRONTAB_CREATE", "ADMIN_CRONTAB_EDIT", "ADMIN_CRONTAB_DELETE", "ADMIN_CRONTAB_EXECUTE",
			"ADMIN_DATABASE_QUERY", "ADMIN_DATABASE_UPDATE",
			"ADMIN_LANGUAGE_CREATE", "ADMIN_LANGUAGE_EDIT", "ADMIN_LANGUAGE_DELETE", "ADMIN_LANGUAGE_IMPORT",
			"ADMIN_LOGCAT_LIST", "ADMIN_LOGCAT_VIEW",
			"ADMIN_LOGGED_USERS",
			"ADMIN_MAIL_ACCOUNT_CREATE", "ADMIN_MAIL_ACCOUNT_EDIT", "ADMIN_MAIL_ACCOUNT_DELETE", "ADMIN_MAIL_ACCOUNT_CHECK",
			"ADMIN_MAIL_FILTER_CREATE", "ADMIN_MAIL_FILTER_EDIT", "ADMIN_MAIL_FILTER_DELETE",
			"ADMIN_MAIL_FILTER_RULE_CREATE", "ADMIN_MAIL_FILTER_RULE_EDIT", "ADMIN_MAIL_FILTER_RULE_DELETE",
			"ADMIN_MIME_TYPE_CREATE", "ADMIN_MIME_TYPE_EDIT", "ADMIN_MIME_TYPE_DELETE",
			"ADMIN_USER_PROFILE_CREATE", "ADMIN_USER_PROFILE_EDIT", "ADMIN_USER_PROFILE_DELETE",
			"ADMIN_PROPERTY_GROUP_REGISTER", "ADMIN_PROPERTY_GROUP_LIST",
			"ADMIN_REPORT_CREATE", "ADMIN_REPORT_EDIT", "ADMIN_REPORT_DELETE", "ADMIN_REPORT_EXECUTE",
			"ADMIN_REPOSITORY_SEARCH", "ADMIN_REPOSITORY_REINDEX",
			"ADMIN_REPOSITORY_UNLOCK", "ADMIN_REPOSITORY_CHECKIN",
			"ADMIN_REPOSITORY_EDIT", "ADMIN_REPOSITORY_SAVE", "ADMIN_REPOSITORY_LIST",
			"ADMIN_REPOSITORY_REMOVE_CONTENT", "ADMIN_REPOSITORY_REMOVE_CURRENT", "ADMIN_REPOSITORY_REMOVE_MIXIN",
			"ADMIN_WORKFLOW_REGISTER",
			"ADMIN_PROCESS_DEFINITION_DELETE",
			"ADMIN_PROCESS_INSTANCE_DELETE", "ADMIN_PROCESS_INSTANCE_END", "ADMIN_PROCESS_INSTANCE_RESUME",
			"ADMIN_PROCESS_INSTANCE_SUSPEND", "ADMIN_PROCESS_INSTANCE_ADD_COMMENT",
			"ADMIN_PROCESS_INSTANCE_VARIABLE_DELETE", "ADMIN_PROCESS_INSTANCE_VARIABLE_ADD",
			"ADMIN_TASK_INSTANCE_SET_ACTOR", "ADMIN_TASK_INSTANCE_START", "ADMIN_TASK_INSTANCE_END",
			"ADMIN_TASK_INSTANCE_SUSPEND", "ADMIN_TASK_INSTANCE_ADD_COMMENT",
			"ADMIN_TASK_INSTANCE_VARIABLE_DELETE", "ADMIN_TASK_INSTANCE_VARIABLE_ADD",
			"ADMIN_TASK_INSTANCE_RESUME", "ADMIN_TOKEN_SUSPEND", "ADMIN_TOKEN_RESUME", "ADMIN_TOKEN_END",
			"ADMIN_TOKEN_SET_NODE", "ADMIN_TOKEN_SIGNAL",
			"ADMIN_STAMP_IMAGE_CREATE", "ADMIN_STAMP_IMAGE_EDIT", "ADMIN_STAMP_IMAGE_DELETE", "ADMIN_STAMP_IMAGE_ACTIVE",
			"ADMIN_STAMP_TEXT_CREATE", "ADMIN_STAMP_TEXT_EDIT", "ADMIN_STAMP_TEXT_DELETE", "ADMIN_STAMP_TEXT_ACTIVE",
			"ADMIN_TWITTER_ACCOUNT_CREATE", "ADMIN_TWITTER_ACCOUNT_EDIT", "ADMIN_TWITTER_ACCOUNT_DELETE",
			"ADMIN_USER_CONFIG_EDIT", "ADMIN_SCRIPTING",
			"ADMIN_OMR_CREATE", "ADMIN_OMR_EDIT", "ADMIN_OMR_DELETE", "ADMIN_OMR_EXECUTE", "ADMIN_OMR_CHECK_TEMPLATE",

			//---------------------------------
			"Misc",
			"MISC_OPENKM_START", "MISC_OPENKM_STOP",
			"MISC_STATUS", "MISC_TEXT_EXTRACTION_FAILURE"
	};

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		ServletContext sc = getServletContext();
		request.setCharacterEncoding("UTF-8");
		String dbegin = WebUtils.getString(request, "dbegin");
		String dend = WebUtils.getString(request, "dend");
		String user = WebUtils.getString(request, "user");
		String action = WebUtils.getString(request, "action");
		String item = WebUtils.getString(request, "item");
		String action_ = WebUtils.getString(request, "action_");
		String typeReport = WebUtils.getString(request, "type_report");

		try {
			if (!dbegin.equals("") && !dend.equals("")) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				ActivityFilter filter = new ActivityFilter();
				Calendar begin = Calendar.getInstance();
				begin.setTime(sdf.parse(dbegin));
				begin.set(Calendar.HOUR, 0);
				begin.set(Calendar.MINUTE, 0);
				begin.set(Calendar.SECOND, 0);
				begin.set(Calendar.MILLISECOND, 0);
				filter.setBegin(begin);
				Calendar end = Calendar.getInstance();
				end.setTime(sdf.parse(dend));
				end.add(Calendar.DAY_OF_MONTH, 1);
				end.set(Calendar.HOUR, 0);
				end.set(Calendar.MINUTE, 0);
				end.set(Calendar.SECOND, 0);
				end.set(Calendar.MILLISECOND, 0);
				filter.setEnd(end);
				filter.setUser(user);
				filter.setAction(action);
				filter.setItem(item);

				if ("Export".equals(action_)) {
					OrganizationVTX orgUser = UserDAO.getInstance().getOrgByUserId(request.getRemoteUser());
					List<ActivityLogExportBean> exportBeanList = ActivityDAO.exportByFilter(filter);
					List<ActivityLogExportBean> exportBeanGeneralList = ActivityDAO.exportByFilterGeneral(filter);
					if ("DOC".equals(typeReport)) {
						doExportDOC(filter, response, orgUser, exportBeanGeneralList, exportBeanList);
					}
					if ("XLS".equals(typeReport)) {
						doExportXLS(filter, response, orgUser, exportBeanGeneralList, exportBeanList);
					}

				} else {
					sc.setAttribute("results", ActivityDAO.findByFilter(filter));

					// Activity log
					UserActivity.log(request.getRemoteUser(), "ADMIN_ACTIVITY_LOG", null, null, filter.toString());
				}


			} else {
				sc.setAttribute("results", null);
			}
			if (!"Export".equals(action_)) {
				sc.setAttribute("dbeginFilter", dbegin);
				sc.setAttribute("dendFilter", dend);
				sc.setAttribute("userFilter", user);
				sc.setAttribute("actionFilter", action);
				sc.setAttribute("itemFilter", item);
				sc.setAttribute("actions", actions);
				sc.setAttribute("users", OKMAuth.getInstance().getUsers(null));


				sc.getRequestDispatcher("/admin/activity_log.jsp").forward(request, response);
			}


		} catch (ParseException e) {
			sendErrorRedirect(request, response, e);
		} catch (DatabaseException e) {
			sendErrorRedirect(request, response, e);
		} catch (PrincipalAdapterException e) {
			sendErrorRedirect(request, response, e);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void doExportDOC(ActivityFilter filter, HttpServletResponse response, OrganizationVTX orgUser,
							List<ActivityLogExportBean> exportBeanGeneralList, List<ActivityLogExportBean> exportBeanList)
			throws IOException, URISyntaxException, DatabaseException, ServletException {

		URL res = getClass().getClassLoader().getResource("template/BC_ACTIVITY_DOCUMENT.doc");
		File file = Paths.get(res.toURI()).toFile();
		String absolutePath = file.getAbsolutePath();

		Document docSpire = new Document(absolutePath);
		Map<String, String> map = new HashMap<String, String>();
		SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
		map.put("fromDate", format1.format(filter.getBegin().getTime()));
		map.put("toDate", format1.format(DateUtils.addDays(filter.getEnd().getTime(), -1)));
		map.put("orgName", orgUser.getName());

		Table table = docSpire.getSections().get(0).getTables().get(2);

		int index1 = 1;

		for (ActivityLogExportBean elb : exportBeanGeneralList) {
			List arrList = new ArrayList();
			arrList.add(index1);
			arrList.add(elb.getOrgName());
			arrList.add(elb.getDocumentName());
			arrList.add(elb.getAction());
			TableRow dataRow = table.addRow();
			for (int col = 0; col < arrList.size(); ++col) {
				dataRow.getCells().get(col).addParagraph().appendText(String.valueOf(arrList.get(col)));
			}

			index1++;

		}

		TableRow dataRow = table.addRow();
		dataRow.getCells().get(0).addParagraph().appendText("T???NG");
		dataRow.getCells().get(2).addParagraph().appendText(String.valueOf(
				exportBeanGeneralList
						.stream()
						.map(object -> object.getDocumentName())
						.collect(Collectors.toList())
						.stream()
						.distinct().count()
		));


		index1 = 1;
		Table table2 = docSpire.getSections().get(0).getTables().get(4);
		for (ActivityLogExportBean elb : exportBeanList) {
			List arrList = new ArrayList();
			arrList.add(index1);
			arrList.add(elb.getOrgName());
			arrList.add(elb.getFullName());
			arrList.add(elb.getEmployeeCode());
			arrList.add(elb.getDocumentName());
			arrList.add(elb.getAction());
			arrList.add(elb.getDateTime());
			TableRow dataRow2 = table2.addRow();
			for (int col = 0; col < arrList.size(); ++col) {
				dataRow2.getCells().get(col).addParagraph().appendText(String.valueOf(arrList.get(col)));

			}

			index1++;
		}

		for (Map.Entry<String, String> entry : map.entrySet()) {
			docSpire.replace("${" + entry.getKey() + "}", entry.getValue(), false, true);
		}


		URL res_ = getClass().getClassLoader().getResource("download/BC_ACTIVITY_DOCUMENT.doc");
		File tmpFile = Paths.get(res_.toURI()).toFile();
		String absoluteTmpPath = tmpFile.getAbsolutePath();
		docSpire.saveToFile(absoluteTmpPath, FileFormat.Doc);

		OutputStream outputStream = response.getOutputStream();
		docSpire.saveToStream(outputStream, FileFormat.Doc);
		new DownloadReportUtils().downloadReport("BC_ACTIVITY_DOCUMENT.doc", response, (ByteArrayOutputStream) outputStream);

		/*InputStream is = null;
		OutputStream os = null;
		try {
			 is = new FileInputStream(tmpFile);
			 os = response.getOutputStream();

			ServletContext context = getServletContext();

			String mimeType = context.getMimeType(absoluteTmpPath);
			if (mimeType == null) {
				mimeType = "application/octet-stream";
			}

			response.setContentType(mimeType);
			response.setContentLength((int) tmpFile.length());


			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", tmpFile.getName());
			response.setHeader(headerKey, headerValue);


			int len = -1;
			byte[] buffer = new byte[4096000];
			while ((len = is.read(buffer, 0, buffer.length)) != -1) {
				os.write(buffer, 0, len);
			}

		} catch (IOException ioe) {
			throw new ServletException(ioe.getMessage());
		} finally {
			if (is != null)
				is.close();
			if (os != null)
				os.close();
		}*/

	}

	public void doExportXLS(ActivityFilter filter, HttpServletResponse response, OrganizationVTX orgUser,
							List<ActivityLogExportBean> exportBeanGeneralList, List<ActivityLogExportBean> exportBeanList)
			throws IOException, URISyntaxException, DatabaseException, ServletException {


		URL res = getClass().getClassLoader().getResource("template/BC_ACTIVITY_DOCUMENT.xlsx");
		File file = Paths.get(res.toURI()).toFile();
		String absolutePath = file.getAbsolutePath();

		InputStream is = new BufferedInputStream(new FileInputStream(absolutePath));

		Map<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
		map.put("fromDate", format1.format(filter.getBegin().getTime()));
		map.put("toDate", format1.format(DateUtils.addDays(filter.getEnd().getTime(), -1)));
		map.put("orgName", orgUser.getName());
		map.put("generalBeans", exportBeanGeneralList);
		map.put("detailBeans", exportBeanList);
		map.put("totalDoc", String.valueOf(
				exportBeanGeneralList
						.stream()
						.map(object -> object.getDocumentName())
						.collect(Collectors.toList())
						.stream()
						.distinct().count()
				)
		);

		XLSTransformer transformer = new XLSTransformer();
		Workbook resultWorkbook = null;
		try {
			resultWorkbook = transformer.transformXLS(is, map);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		resultWorkbook.write(byteArrayOutputStream);


		new DownloadReportUtils().downloadReport("/BC_ACTIVITY_DOCUMENT.xlsx", response, byteArrayOutputStream);
		/*InputStream is = null;
		OutputStream os = null;
		try {
			 is = new FileInputStream(tmpFile);
			 os = response.getOutputStream();

			ServletContext context = getServletContext();

			String mimeType = context.getMimeType(absoluteTmpPath);
			if (mimeType == null) {
				mimeType = "application/octet-stream";
			}

			response.setContentType(mimeType);
			response.setContentLength((int) tmpFile.length());


			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", tmpFile.getName());
			response.setHeader(headerKey, headerValue);


			int len = -1;
			byte[] buffer = new byte[4096000];
			while ((len = is.read(buffer, 0, buffer.length)) != -1) {
				os.write(buffer, 0, len);
			}

		} catch (IOException ioe) {
			throw new ServletException(ioe.getMessage());
		} finally {
			if (is != null)
				is.close();
			if (os != null)
				os.close();
		}*/

	}
}
