/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.anna.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

/**
 *
 * @author Админ
 */
public class ClientSession implements Runnable {

    private final Socket socket;
    private InputStream in = null;
    private OutputStream out = null;

    @Override
    public void run() {

        try {

            /* Получаем заголовок сообщения от клиента */
            Request req = new Request(in);
            if (req.isBadRequest()) {
                socket.close();
                return;
            }

            final Responce responce=new Responce(req);
            
            try {
                responce.write(new PrintStream(out));

                socket.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public ClientSession(Socket socket) throws IOException {
        this.socket = socket;
        in = socket.getInputStream();
        out = socket.getOutputStream();
    }

}
