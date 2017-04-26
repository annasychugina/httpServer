/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.anna.httpserver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Админ
 */
public class FileContent {

    private static String rootPath = null;
    private static final Map<String, String> contentTypes = new HashMap<>();

    static {
        contentTypes.put(".html", "text/html");
        contentTypes.put(".txt", "text/html");
        contentTypes.put(".css", "text/css");
        contentTypes.put(".js", "text/javascript");
        contentTypes.put(".jpg", "image/jpeg");
        contentTypes.put(".jpeg", "image/jpeg");
        contentTypes.put(".png", "image/png");
        contentTypes.put(".gif", "image/gif");
        contentTypes.put(".swf", "application/x-shockwave-flash");

    }
    private InputStream is;
    private long length;
    private String contentType;

    private boolean notFound = false;
    private boolean forbidden = false;

    public static void setRootPath(String rootPath) {
        FileContent.rootPath = rootPath;
    }
    public boolean isForbidden() {
        return forbidden;
    }

    public boolean isNotFound() {

        return notFound;
    }

    public long getLength() {
        return length;
    }

    public String getContentType() {
        return contentType;
    }

    public FileContent load(String url) {
        forbidden = url.contains("../");

        File file = new File(rootPath + url);
        if (file.isDirectory()) {

            file = new File(rootPath + url + "index.html");
            if (!file.exists()) {
                forbidden = true;
                return this;
            }
        }
        notFound = !file.exists();
        forbidden = !file.canRead() || forbidden;

        if (notFound || forbidden) {
            return this;
        }

        this.length = file.length();

        String extension = file.getName().substring(Math.max(file.getName().lastIndexOf('.'), 0));
        contentType = contentTypes.get(extension);
        try {
            is = new BufferedInputStream(new FileInputStream(file));

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Responce.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this;
    }

    public void write(PrintStream ps) throws IOException {
        if (is == null) {
            return;
        }
        final byte[] buffer = new byte[1024];
        while (true) {
            final int read = is.read(buffer);
            if (read == -1) {
                break;
            }
            ps.write(buffer, 0, read);
        }
        is.close();

    }

}
