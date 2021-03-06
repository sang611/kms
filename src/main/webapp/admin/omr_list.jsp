<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.openkm.core.Config"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn'%>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="Shortcut icon" href="favicon.ico" />
<link rel="stylesheet" type="text/css" href="../css/dataTables-1.10.10/jquery.dataTables-1.10.10.min.css" />
<link rel="stylesheet" type="text/css" href="css/admin-style.css" />
  <link rel="stylesheet" type="text/css" href="../renew/css/layout-admin.css" />
  <link rel="stylesheet" type="text/css" href="../renew/fonts/glyphicons/glyphicons.css" />
<script type="text/javascript" src="../js/utils.js"></script>
<script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
<script type="text/javascript" src="../js/jquery.dataTables-1.10.10.min.js"></script>
<script type="text/javascript">
  $(document).ready(function() {
    $('#results').dataTable({
      "bStateSave" : true,
      "iDisplayLength" : 10,
      "lengthMenu" : [ [ 10, 15, 20 ], [ 10, 15, 20 ] ],
      "fnDrawCallback" : function(oSettings) {
        dataTableAddRows(this, oSettings);
      }
    });
  });
</script>
<title>OMR Template</title>
</head>
<body>
	<c:set var="isAdmin"><%=request.isUserInRole(Config.DEFAULT_ADMIN_ROLE)%></c:set>
	<c:choose>
		<c:when test="${isAdmin}">
			<ul id="breadcrumb">
				<li class="path"><a href="Omr">OMR Template</a></li>
			</ul>
			<br />
			<div style="width: 90%; margin-left: auto; margin-right: auto;">
				<table id="results" class="results">
					<thead>
						<tr>
							<th>Id</th>
							<th>Name</th>
							<th>Template</th>
							<th>Asc</th>
							<th>Config</th>
							<th>Fields</th>
							<th>Active</th>
							<th width="100px">
							<c:url value="Omr" var="urlCreate">
								<c:param name="action" value="create" />
							</c:url>
							<a href="${urlCreate}"><span class="glyphicons glyphicons-plus-sign" style="font-size: 15px; color: #27B45F" title="New template"></span></a></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="om" items="${omr}" varStatus="row">
							<c:url value="Omr" var="urlEdit">
								<c:param name="action" value="edit" />
								<c:param name="om_id" value="${om.id}" />
							</c:url>
							<c:url value="Omr" var="urlDelete">
								<c:param name="action" value="delete" />
								<c:param name="om_id" value="${om.id}" />
							</c:url>
							<c:url value="Omr" var="urlCheck">
								<c:param name="action" value="check" />
								<c:param name="om_id" value="${om.id}" />
							</c:url>
							<c:url value="Omr" var="urlDownload">
								<c:param name="action" value="downloadFile" />
								<c:param name="om_id" value="${om.id}" />
							</c:url>
							<tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
								<td>${om.id}</td>
								<td>${fn:escapeXml(om.name)}</td>
								<td><a href="${urlDownload}&type=1">${om.templateFileName}</a></td>
								<td><a href="${urlDownload}&type=2">${om.ascFileName}</a></td>
								<td><a href="${urlDownload}&type=3">${om.configFileName}</a></td>
								<td>
									<c:if test="${om.fieldsFileName != null && om.fieldsFileName ne ''}">
										<a href="${urlDownload}&type=4">${om.fieldsFileName}</a>
									</c:if>
								</td>
								<td align="center">
									<c:choose>
										<c:when test="${om.active}">
											<span class="glyphicons glyphicons-ok" style="font-size: 15px; color: #27B45F;" title="Active"></span>
										</c:when>
										<c:otherwise>
											<span class="glyphicons glyphicons-remove" style="font-size: 15px; color: red;" title="Inactive"></span>
										</c:otherwise>
									</c:choose></td>
								<td align="center">
									<a href="${urlEdit}"><span class="glyphicons glyphicons-pen" style="font-size: 15px; color: #27B45F;" title="Edit"></span></a>
									&nbsp;
									<a href="${urlDelete}"><span class="glyphicons glyphicons-bin" style="font-size: 15px; color: red;" title="Delete"></span></a>
									&nbsp;
									<c:if test="${om.fieldsFileName!=null && om.fieldsFileName ne ''}">
										<a href="${urlCheck}"><span class="glyphicons glyphicons-ok-sign" style="font-size: 15px; color: #27B45F;" title="Check"></span></a>
									</c:if>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</c:when>
		<c:otherwise>
			<div class="error">
				<h3>Only admin users allowed</h3>
			</div>
		</c:otherwise>
	</c:choose>
</body>
</html>
