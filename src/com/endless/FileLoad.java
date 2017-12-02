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

		// 1.�жϵ�ǰrequest��Ϣʵ����ܳ���
		int totalBytes = request.getContentLength();
		System.out.println("��ǰ�����ܳ���:" + totalBytes);
		// 2.����Ϣͷ�������ҳ��ֽ��,����:boundary=----WebKitFormBoundaryeEYAk4vG4tRKAlB6
		String contentType = request.getContentType();
		int position = contentType.indexOf("boundary=");

		String startBoundary = "--" + contentType.substring(position + "boundary=".length());
		System.out.println(startBoundary);
		String endBoundary = startBoundary + "--";
		// ��request�����������뵽bytes��
		InputStream inputStream = request.getInputStream();
		DataInputStream dataInputStream = new DataInputStream(inputStream);
		byte[] bytes = new byte[totalBytes];
		dataInputStream.readFully(bytes);
		dataInputStream.close();
		// ���ֽڶ��뵽�ַ�����
		BufferedReader reader = new BufferedReader(new StringReader(new String(bytes)));

		// ��ʼ��ȡreader(�ָ�form���ڵı�������:�ı������ļ�)

		// ��¼��ǰ�Ķ�ȡ�ж�Ӧ��bytes;
		int temPosition = 0;
		boolean flag = false;
		int end = 0;
		while (true) {
			// ����ȡһ���ļ���Ϣ��
			if (flag) {
				bytes = subBytes(bytes, end, totalBytes);
				temPosition = 0;
				reader = new BufferedReader(new StringReader(new String(bytes)));
			}
			// ��ȡһ�е���Ϣ:------WebKitFormBoundary5R7esAd459uwQsd5,��:lastBoundary
			String str = reader.readLine();
			System.out.println("this line is:" + str);
			// �����������ַ�
			temPosition += str.getBytes().length + 2;
			// endBoundary:����
			if (str == null || str.equals(endBoundary)) {
				break;
			}
			// ��ʾͷ��Ϣ�Ŀ�ʼ(һ����ǩ,input,select��)
			if (str.startsWith(startBoundary)) {
				// �жϵ�ǰͷ��Ӧ�ı�������

				str = reader.readLine(); // ��ȡ��ǰͷ��Ϣ����һ��:Content-Disposition��
				temPosition += str.getBytes().length + 2;

				int position1 = str.indexOf("filename="); // �ж��Ƿ����ļ��ϴ�
				// such as:Content-Disposition: form-data; name="fileName";
				// filename="P50611-162907.jpg"
				if (position1 == -1) {// ��ʾ����ͨ�ı����ϴ�

				} else {// position1!=-1,��ʾ���ļ��ϴ�
						// ������ǰ�ϴ����ļ���Ӧ��name(input��ǩ��name),�Լ�fieldname:�ļ���
					int position2 = str.indexOf("name=");
					// ȥ��name��filename֮���"��;�Լ��ո�

					String name = str.substring(position2 + "name=".length() + 1, position1 - 3);
					System.out.println(name);
					// ȥ������"
					String filename = str.substring(position1 + "filename=".length() + +1, str.length() - 1);
					System.out.println(filename);
					
					 
					
					// ��ȡ��,such as:Content-Type: image/jpeg,��¼�ֽ���,�˴����λ���
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
	 * ��λ��ǰͷ��Ϣ�Ľ���λ��
	 * 
	 * @param bytes
	 * @param start
	 *            :��ʼλ��
	 * @param end
	 *            :����λ��
	 * @param endStr
	 *            :�Ƚ��ַ���
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
				// ���ؽ������Ŀ�ʼλ��
				if (k == endByte.length) {
					return i;
				}
			}
		}

		return 0;
	}
}
