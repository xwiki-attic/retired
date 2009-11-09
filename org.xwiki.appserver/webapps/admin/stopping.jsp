<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="com.cogniumsystems.appserver.*"%>
<%
	String title = "Server stopping...";
	request.setAttribute("title", title);
%>
<%@include file="include/header.jsp"%>

<h1><%=title%></h1>
Now the server will be stopped...

<%@ include file="include/bottom.jsp"%>
