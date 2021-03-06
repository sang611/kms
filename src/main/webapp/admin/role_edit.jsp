<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
  <script type="text/javascript">
    $(document).ready(function() {
      $('form').bind('submit', function(event) {
        var error = $('input[name="rol_id"] + span.vanadium-invalid');
  
        if (error == null || error.text() == '') {
          return true;
        } else {
          return false;
        }
      });
    });
  </script>
<title>Role edit</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="Auth?action=roleList">Role list</a>
        </li>
        <li class="path">
          <c:choose>
            <c:when test="${action == 'roleCreate'}">Create role</c:when>
            <c:when test="${action == 'roleEdit'}">Edit role</c:when>
            <c:when test="${action == 'roleDelete'}">Delete role</c:when>
          </c:choose>
        </li>
      </ul>
      <br/>
      <form action="Auth" method="post">
        <input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <input type="hidden" name="csrft" value="${csrft}"/>
        <table class="form" width="300px">
          <tr>
            <td>Id</td>
            <td width="100%">
              <c:choose>
                <c:when test="${action != 'roleCreate'}">
                  <input class=":required :only_on_blur" name="rol_id" size="45" value="${rol.id}" readonly="readonly"/>
                </c:when>
                <c:otherwise>
                  <input class=":required :only_on_blur :ajax;Auth?action=validateRole" name="rol_id" size="45" value=""/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td>Active</td>
            <td>
              <c:choose>
                <c:when test="${rol.active}">
                  <input name="rol_active" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="rol_active" type="checkbox"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td colspan="2" align="right">
              <input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton btn btn-warning"/>
              <c:choose>
                <c:when test="${action == 'roleCreate'}"><input type="submit" value="Create" class="yesButton btn btn-success"/></c:when>
                <c:when test="${action == 'roleEdit'}"><input type="submit" value="Edit" class="yesButton btn btn-success"/></c:when>
                <c:when test="${action == 'roleDelete'}"><input type="submit" value="Delete" class="yesButton btn btn-success"/></c:when>
              </c:choose>
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