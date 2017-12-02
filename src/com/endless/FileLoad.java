package com.endless;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/FileLoad")
public class FileLoad extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static byte[] subBytes(byte[] b, int from, int end) {
		byte[] result = new byte[end - from];
		System.arraycopy(b, from, result, 0, end - from);
		return result;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 1.判断当前request消息实体的总长度
		int totalBytes = request.getContentLength();
		System.out.println("当前数据总长度:" + totalBytes);
		// 2.在消息头类型中找出分解符,例如:boundary=----WebKitFormBoundaryeEYAk4vG4tRKAlB6
		String contentType = request.getContentType();
		int position = contentType.indexOf("boundary=");

		String startBoundary = "--" + contentType.substring(position + "boundary=".length());
		System.out.println(startBoundary);
		String endBoundary = startBoundary + "--";
		// 将request的输入流读入到bytes中
		InputStream inputStream = request.getInputStream();
		DataInputStream dataInputStream = new DataInputStream(inputStream);
		byte[] bytes = new byte[totalBytes];
		dataInputStream.readFully(bytes);
		dataInputStream.close();
		// 将字节读入到字符流中
		BufferedReader reader = new BufferedReader(new StringReader(new String(bytes)));

		// 开始读取reader(分割form表单内的表单域类型:文本或者文件)

		// 记录当前的读取行对应的bytes;
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

				int position1 = str.indexOf("filename="); // 判断是否是文件上传
				// such as:Content-Disposition: form-data; name="fileName";
				// filename="P50611-162907.jpg"
				if (position1 == -1) {// 表示是普通文本域上传

				} else {// position1!=-1,表示是文件上传
						// 解析当前上传的文件对应的name(input标签的name),以及fieldname:文件名
					int position2 = str.indexOf("name=");
					// 去掉name与filename之间的"和;以及空格

					String name = str.substring(position2 + "name=".length() + 1, position1 - 3);
					System.out.println(name);
					// 去掉两个"
					String filename = str.substring(position1 + "filename=".length() + +1, str.length() - 1);
					System.out.println(filename);
					
					 
					
					// 读取行,such as:Content-Type: image/jpeg,记录字节数,此处两次换行
					temPosition += (reader.readLine().getBytes().length + 4);
					end = this.locateEnd(bytes, temPosition, totalBytes, endBoundary);
					String path = request.getSession().getServletContext().getRealPath("/");
					System.out.println(path);
					DataOutputStream dOutputStream = new DataOutputStream(
							new FileOutputStream(new File(path + "/test.jpg")));
					dOutputStream.write(bytes, temPosition, end - temPosition - 2);
					dOutputStream.close();

					flag = true;

				}
			}
		}
	}

	/**
	 * 定位当前头信息的结束位置
	 * 
	 * @param bytes
	 * @param start
	 *            :开始位置
	 * @param end
	 *            :结束位置
	 * @param endStr
	 *            :比较字符串
	 * @return TODO
	 */
	public int locateEnd(byte[] bytes, int start, int end, String endStr) {
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
				System.out.println(i);
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
}
