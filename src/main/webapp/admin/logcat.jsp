<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
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
        "bStateSave": true,
        "iDisplayLength": 15,
        "lengthMenu": [[10, 15, 20], [10, 15, 20]],
        "fnDrawCallback": function (oSettings) {
          dataTableAddRows(this, oSettings);
        }
      });
	});

    function showAlert(){
      alert("File không hợp lệ!");
    }
  </script>
  <title>LogCat</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:if test="${messages != null}">
    <script type="text/javascript">
      showAlert();
    </script>
  </c:if>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="utilities.jsp">Utilities</a>
        </li>
        <li class="path">LogCat</li>
      </ul>
      <br/>
      <div style="width: 80%; margin-left: auto; margin-right: auto;">
        <table id="results" class="results">
          <thead>
            <tr class="header">
              <td align="right" colspan="2">
                <form action="LogCat" method="get">
                  <input type="hidden" name="action" value="purge" /> <input type="submit" value="Purge" class="deleteButton btn btn-danger" />
                </form>
              </td>
            </tr>
            <tr>
              <th>File</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="log" items="${files}" varStatus="row">
              <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
                <td>${log.name}</td>
                <td align="center">
                  <a href="LogCat?action=download&file=${log.name}"><span class="glyphicons glyphicons-download-alt" style="font-size: 15px; color: #27B45F;" title="Download"></span></a>
                  &nbsp;
                  <a href="LogCat?action=view&file=${log.name}"><span class="glyphicons glyphicons-search" style="font-size: 15px; color: #27B45F;" title="Examine"></span></a>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>
