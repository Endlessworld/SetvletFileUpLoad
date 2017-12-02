<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<form action="FileLoads" enctype="multipart/form-data" method="post">
写入路径：<input type="text" name="path"><br/>
	<input type="file" name="file1" value="请选择文件" size="999"> <br/>
	<input type="file" name="file2" value="请选择文件" size="999"> <br/>
	<input type="file" name="file3" value="请选择文件" size="999"> <br/>
	<input type="file" name="file4" value="请选择文件" size="999"> <br/>
 	<input type="submit" value="提交">
</form>
</body>
</html>