package com.endless;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig
@WebServlet("/FileLoads")
public class FileLoads extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		res.setContentType("text/html;charset=utf-8");
		Collection<Part> xx = req.getParts();
		for (Part file : xx) {
			String SavePath = System.getProperty("user.home") + "\\Documents\\";
			String FileName = SavePath + file.getSubmittedFileName();
			String type = file.getContentType();
			int x = type.indexOf("image");
			if (x != -1) {// 指定文件类型
				file.write(FileName);
			}
			res.getWriter().println(x == -1 ? "类型不符合!" : "上传成功！");
		}
	}
}
