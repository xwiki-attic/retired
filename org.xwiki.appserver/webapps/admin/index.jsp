<%@page contentType="text/html; charset=UTF-8"%>

<%
	String title = "Admin interface";
	request.setAttribute("title", title);
%>
<%@include file="include/header.jsp"%>

<h1><%=title%></h1>

<h3>Commands</h3>
<li><a href="<%=request.getContextPath()%>/admin/stop">Stop the server</a></li>

<%@include file="include/appInfo.jsp"%>
<%@include file="include/requestInfo.jsp"%>

<%@include file="include/bottom.jsp"%>
