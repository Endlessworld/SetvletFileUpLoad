<%@page import="java.util.Arrays"%>
<%@page import="java.io.File"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
<head>
 <title>消息提示</title>
</head>
 
<body>
  ${message}<br/>当前文件：<br/>
  <%
  String url="FileDownload?url=";
  String[] x=((File) request.getAttribute("SavePath")).list() ;
  for(String z:x){
	  out.append("<a href='"+url+z+"'"+">"+z+"</a><br/>\r\n");
  }
  %>
 
</body>
</html>