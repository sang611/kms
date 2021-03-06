<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/admin-style.css" />
  <link rel="stylesheet" type="text/css" href="../renew/css/layout-admin.css" />
  <link rel="stylesheet" type="text/css" href="../renew/fonts/glyphicons/glyphicons.css" />
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <title>Search indexes</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="utilities.jsp">Utilities</a>
        </li>
        <li class="path">List indexes</li>
        <li class="action">
          <a href="ListIndexes?action=list">
            <span class="glyphicons glyphicons-list" style="padding: 2px 0px 0 0; font-size: 12px; color: #27B45F" title="List"></span>
            List indexes
          </a>
        </li>
      </ul>
      <br/>
      <table class="results-old" width="60%">
        <thead>
          <tr class="fuzzy">
            <td colspan="5" align="right">
              <form action="ListIndexes">
                <input type="hidden" name="action" value="search"/>
                <input type="text" name="exp" value="${exp}" style="width: 80%"/>
                <input type="submit" value="Search" class="searchButton btn btn-primary"/>
              </form>
            </td>
          </tr>
          <tr><th>Score</th><th>UUID</th><th>Name</th><th>Type</th><th>Action</th></tr>
        </thead>
        <tbody>
          <c:forEach var="res" items="${results}" varStatus="row">
            <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
              <td>${res.score}</td>
              <td>${res.uuid}</td>
              <td>${res.name}</td>
              <td>${res.type}</td>
              <td align="center">
                <a href="ListIndexes?action=list&id=${res.docId}">
                  <img src="img/action/table.png" alt="List" title="List" style="vertical-align: middle;"/>
                </a>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>