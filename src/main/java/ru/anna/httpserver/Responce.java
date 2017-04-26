/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.anna.httpserver;

import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Админ
 */
public class Responce {

    private final String status;
    private final static String E_405 = "405 Method Not Allowed";
    private final static String E_404 = "404 Not Found";
    private final static String E_403 = "403 Forbidden";
    private final static String E_200 = "200 OK";

    private final Map<String, String> headers = new HashMap<>();
    private final Request request;
    //private String rootPath;

    private FileContent fc = null;

    public Responce(Request request) {
        this.request = request;
        if (!request.isAllowableMethod()) {
            this.status = E_405;
            headers.put("Allow", "GET, HEAD");

        } else {

            fc = new FileContent().load(request.getUri());
            if (fc.isNotFound()) {
                status = E_404;
            } else if (fc.isForbidden()) {
                status = E_403;
            } else {
                status = E_200;

                headers.put("Content-Length", Long.toString(fc.getLength()));
                if (fc.getContentType() != null) {
                    headers.put("Content-Type", fc.getContentType());
                }

            }
        }
        headers.put("Date", new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US).format(new Date(System.currentTimeMillis())));
        headers.put("Server", "Java THP Server v 1.0");
        headers.put("Connection", "Close");

    }

    public void write(PrintStream ps) throws IOException {

        StringBuilder sb = new StringBuilder(request.getProtocol()).append(" ").append(status).append("\r\n");

        for (Map.Entry<String, String> e : headers.entrySet()) {
            sb.append(e.getKey()).append(": ").append(e.getValue()).append("\r\n");
        }
        ps.print(sb.toString());
        ps.print("\r\n");
        if (fc != null && request.isGet()) {
            fc.write(ps);
        }
        ps.flush();
        ps.close();
    }

}
