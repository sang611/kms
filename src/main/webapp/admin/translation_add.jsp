<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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
  <script type="text/javascript" src="../js/vanadium-min.js"></script>
  <title>Add translation term</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="experimental.jsp">Experimental</a>
        </li>
        <li class="path">Add translation term</li>
      </ul>
      <br/>
      <form action="Language" method="post">
        <input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <table class="form" width="372px">
          <tr>
            <td>Language</td>
            <td><input size="5" class=":required :only_on_blur" name="lg_id" value="${lang.id}" readonly="readonly"/></td>
          </tr>
          <tr>
            <td>Module</td>
            <td>
              <select name="tr_module">
                <c:forEach var="tr_module" items="${tr_module}" varStatus="row">
                  <option value="${tr_module}">${tr_module}</option>
                </c:forEach>
              </select>
            </td>
          </tr>
          <tr>
            <td>key</td>
            <td><input size="60" class=":required :only_on_blur" name="tr_key" value="${tr_key}"/></td>
          </tr>
          <tr>
            <td>Text</td>
            <td><input size="60" class=":required :only_on_blur" name="tr_text" value="${tr_text}"/></td>
          </tr>
          <tr>
            <td colspan="2" align="right">
              <input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton btn btn-warning"/>
              <input type="submit" value="Add" class="yesButton btn btn-success"/>
            </td>
          </tr>
        </table>
      </form>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>