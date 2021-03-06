<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.openkm.servlet.admin.BaseServlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u"%>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="Shortcut icon" href="favicon.ico" />
<link rel="stylesheet" type="text/css" href="../css/dataTables-1.10.10/jquery.dataTables-1.10.10.min.css" />
<link rel="stylesheet" href="css/admin-style.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="../renew/css/layout-admin.css" />
<link rel="stylesheet" type="text/css" href="../renew/fonts/glyphicons/glyphicons.css" />
<script type="text/javascript" src="../js/utils.js"></script>
<script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
<script type="text/javascript" src="../js/jquery.dataTables-1.10.10.min.js"></script>
<script type="text/javascript">
  $(document).ready(function() {
    $('a.confirm').click(function(e) {
      e.preventDefault();

      if (confirm('Are you sure?')) {
        window.location.href = $(this).attr('href');
      }
    });
    $('#results').dataTable({
      "bStateSave" : true,
      "iDisplayLength" : 15,
      "lengthMenu" : [ [ 10, 15, 20 ], [ 10, 15, 20 ] ],
      "fnDrawCallback" : function(oSettings) {
        dataTableAddRows(this, oSettings);
      }
    });
  });
</script>
<title>Workflow Process Definition Browser</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <u:constantsMap className="com.openkm.core.Config" var="Config" />
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path"><a href="Workflow?action=processDefinitionList">Process definitions</a></li>
        <li class="action">
          <a href="Workflow?action=processDefinitionList"> 
            <span class="glyphicons glyphicons-refresh" style="padding: 2px 0px 0 0; font-size: 12px; color: #27B45F" title="Refresh"></span>
            Refresh
          </a>
        </li>
      </ul>
      <br />
      <c:choose>
        <c:when test="${Config.HIBERNATE_DIALECT == 'org.hibernate.dialect.HSQLDialect'}">
          <table border="0" cellpadding="5" cellspacing="3" align="center">
            <tr>
              <td style="background-color: #DE6611; font-weight: bold;">HSQL database is not compatible with workflow engine</td>
            </tr>
          </table>
        </c:when>
        <c:otherwise>
          <div style="width: 90%; margin-left: auto; margin-right: auto;">
            <table id="results" class="results">
              <thead>
                <tr>
                  <th>Process ID</th>
                  <th>Process Name</th>
                  <th>Process Description</th>
                  <th>Version</th>
                  <th width="50px">Actions</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="pd" items="${processDefinitions}" varStatus="row">
                  <c:url value="Workflow" var="urlProcessDefinitionView">
                    <c:param name="action" value="processDefinitionView" />
                    <c:param name="pdid" value="${pd.id}" />
                  </c:url>
                  <c:url value="Workflow" var="urlProcessDefinitionDelete">
                    <c:param name="action" value="processDefinitionDelete" />
                    <c:param name="pdid" value="${pd.id}" />
                  </c:url>
                  <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
                    <td>${pd.id}</td>
                    <td>${pd.name}</td>
                    <td>${pd.description}</td>
                    <td>${pd.version}</td>
                    <td width="50px" align="center">
                      <a href="${urlProcessDefinitionView}"><span class="glyphicons glyphicons-search" style="font-size: 15px; color: #27B45F;" title="Examine"></span></a> 
                      &nbsp; 
                      <a class="confirm" href="${urlProcessDefinitionDelete}"><span class="glyphicons glyphicons-bin" style="font-size: 15px; color: red;" title="Delete"></span></a>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
              <tfoot>
                <tr>
                  <td align="right" colspan="5">
                    <form action="RegisterWorkflow" method="post" enctype="multipart/form-data">
                      <input class=":required :only_on_blur" type="file" name="definition" /> 
                      <input type="submit" value="Register process definition" class="loadButton btn btn-success" />
                    </form>
                  </td>
                </tr>
              </tfoot>
            </table>
          </div>
        </c:otherwise>
      </c:choose>
    </c:when>
    <c:otherwise>
      <div class="error">
        <h3>Only admin users allowed</h3>
      </div>
    </c:otherwise>
  </c:choose>
</body>
</html>