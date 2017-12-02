package com.endless;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/FileDownload")
public class DownLoad extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

	try {

	    // String url = req.getParameter("url");
	    String path = "H:\\JAVA教程\\9AjaxJquery\\images\\4.png";
	    // System.out.println(url);
	    System.out.println("要下载的文件路径" + path);
	    downLoad(path, res, true);
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    public static void downLoad(String filePath, HttpServletResponse response, boolean isOnLine) throws Exception {
	File f = new File(filePath);
	if (!f.exists()) {
	    response.sendError(404, "File not found!快点找吧老铁！");
	    return;
	}
	BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
	byte[] buf = new byte[1024];
	int len = 0;

	response.reset();// 去除空白行
	if (isOnLine) { // 在线打开方式
	    URL u = new URL("file:///" + filePath);
	    response.setContentType(u.openConnection().getContentType());
	    response.setHeader("Content-Disposition", "inline; filename=" + f.getName());
	    // 文件名应该编码成UTF-8
	} else { // 纯下载方式
	    response.setContentType("application/x-msdownload");
	    response.setHeader("Content-Disposition", "attachment; filename=" + f.getName());
	}
	OutputStream out = response.getOutputStream();
	while ((len = br.read(buf)) > 0)
	    out.write(buf, 0, len);
	br.close();
	out.close();
    }
}
