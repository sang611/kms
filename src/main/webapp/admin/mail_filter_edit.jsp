<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/admin-style.css" />
  <link rel="stylesheet" type="text/css" href="../renew/css/layout-admin.css" />
  <link rel="stylesheet" type="text/css" href="../renew/fonts/glyphicons/glyphicons.css" />
  <script type="text/javascript" src="../js/jquery-1.11.3.min.js"></script>
  <script type="text/javascript" src="../js/vanadium-min.js"></script>
  <script type="text/javascript" src="js/jquery.DOMWindow.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
      $dm = $('.ds').openDOMWindow({
        height : 300,
        width : 400,
        eventType : 'click',
        overlayOpacity : '57',
        windowSource : 'iframe',
        windowPadding : 0
      });
    });
  
    function dialogClose() {
      $dm.closeDOMWindow();
    }
  </script>
  <title>Mail filter</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url value="MailAccount" var="urlMailAccountList">
        <c:param name="ma_user" value="${ma_user}"/>
      </c:url>
      <c:url value="MailAccount" var="urlMailFilterList">
        <c:param name="action" value="filterList"/>
        <c:param name="ma_user" value="${ma_user}"/>
        <c:param name="ma_id" value="${ma_id}"/>
      </c:url>
      <ul id="breadcrumb">
        <li class="path">
          <a href="Auth">User list</a>
        </li>
        <li class="path">
          <a href="${urlMailAccountList}">Mail accounts</a>
        </li>
        <li class="path">
          <a href="${urlMailFilterList}">Mail filters</a>
        </li>
        <li class="path">
          <c:choose>
            <c:when test="${action == 'filterCreate'}">Create mail filter</c:when>
            <c:when test="${action == 'filterEdit'}">Edit mail filter</c:when>
            <c:when test="${action == 'filterDelete'}">Delete mail filter</c:when>
          </c:choose>
        </li>
      </ul>
      <br/>
      <form action="MailAccount" id="form">
        <input type="hidden" name="action" id="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <input type="hidden" name="ma_id" value="${ma_id}"/>
        <input type="hidden" name="ma_user" value="${ma_user}"/>
        <input type="hidden" name="mf_id" value="${mf.id}"/>
        <table class="form" width="345px" align="center">
          <tr>
            <td nowrap="nowrap">Folder</td>
            <td><input name="mf_path" id="mf_path" value="${mf.path}" size="48"/></td>
            <td><a class="ds" href="../extension/DataBrowser?action=repo&sel=fld&dst=mf_path"><span class="glyphicons glyphicons-folder-open" style="font-size: 12px; color: #27B45F"></span></a></td>
          </tr>
          <tr>
            <td>Grouping</td>
            <td>
              <c:choose>
                <c:when test="${mf.grouping}">
                  <input name="mf_grouping" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="mf_grouping" type="checkbox"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td>Active</td>
            <td>
              <c:choose>
                <c:when test="${mf.active}">
                  <input name="mf_active" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="mf_active" type="checkbox"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td colspan="3" align="right">
              <input type="button" onclick="javascript:window.history.back()" value="Cancel" class="noButton btn btn-warning"/>
              <c:choose>
                <c:when test="${action == 'filterCreate'}"><input type="submit" value="Create" class="yesButton btn btn-success"/></c:when>
                <c:when test="${action == 'filterEdit'}"><input type="submit" value="Edit" class="yesButton btn btn-success"/></c:when>
                <c:when test="${action == 'filterDelete'}"><input type="submit" value="Delete" class="yesButton btn btn-success"/></c:when>
              </c:choose>
            </td>
          </tr>
        </table>
      </form>
      <br/>
      <div class="warn" style="text-align: center;" id="dest"></div>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>