/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.anna.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 *
 * @author Админ
 */
public class Request {

    private String protocol = null;
    private String method = null;
    private String uri = null;
    private boolean badRequest = false;

    public Request(InputStream in) throws IOException {

        String request = readRequest(in);
        request = request.substring(0, Math.max(0, request.indexOf("\n")));

        String[] tokens = request.split(" ", 3);
        if (tokens.length != 3) {
            this.badRequest = true;
            return;
        }

        String encURL = tokens[1];
        this.method = tokens[0];
        this.protocol = tokens[2];

        try {
            encURL = URLDecoder.decode(encURL, "utf-8");
        } catch (UnsupportedEncodingException e) {
        }
        int end = encURL.indexOf('#');
        if (end != -1) {
            encURL = encURL.substring(0, end);
        }
        end = encURL.indexOf('?');
        if (end != -1) {
            encURL = encURL.substring(0, end);
        }
        this.uri = encURL;

    }

    public boolean isBadRequest() {
        return badRequest;
    }

    private String readRequest(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String ln;
        while (true) {
            ln = reader.readLine();
            if (ln == null || ln.isEmpty()) {
                break;
            }
            builder.append(ln).append(System.getProperty("line.separator"));
        }
        return builder.toString();
    }

    public boolean isAllowableMethod() {
        return (method != null) && (method.equals("GET") || method.equals("HEAD"));
    }

    public boolean isAllowableProtocol() {
        return (protocol != null) && protocol.equals("HTTP/1.1");
    }

    public String getProtocol() {
        return protocol;
    }

    public boolean isGet() {

        return "GET".equals(method);

    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

}
