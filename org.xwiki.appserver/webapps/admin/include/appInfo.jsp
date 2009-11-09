<%@page import="com.cogniumsystems.appserver.*"%>
<h3>Web Application Info</h3>
<%
  WebServer.IWebAppDescriptor[] array =TomcatPlugin.getDefault().getWebServer().getInstalledApps();
%>

<table border='1'>
<tr><th>Plugin name</th><th>Web Application Name</th><th>Plugin Local Path</th></tr>
<% 
  for (int i=0; i<array.length; i++) {
%>
<tr>
<td><%=array[i].getPluginName()%></td>
<td><a href="/<%=array[i].getName()%>"><%=array[i].getName()%></a></td>
<td><%=array[i].getPath()%></td>
</tr>
<%
 }
%>
</table>
