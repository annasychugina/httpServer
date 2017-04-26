/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.anna.httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.apache.commons.cli.*;

/**
 *
 * @author Админ
 */
public class HttpServer {

    private static final int DEFAULT_PORT = 80;

    public static void main(String[] args) {
        /* Если аргументы отсутствуют, порт принимает значение поумолчанию */

        final Options options = new Options();
        options.addOption(
                Option.builder("c").hasArg().required().desc("count cpu")
                        .type(Number.class).build());
        options.addOption(
                Option.builder("r").hasArg().required()
                        .desc("root dir").build());
        options.addOption(
                Option.builder("p").hasArg().desc("port").build());

        int port = DEFAULT_PORT;
        int n;
        try {
            final CommandLine cmd = new DefaultParser().parse(options, args);
            n = ((Number) cmd.getParsedOptionValue("c")).intValue();
            final String rootPath = cmd.getOptionValue("r");
            FileContent.setRootPath(rootPath);
            if (cmd.hasOption("p")) {
                port = ((Number) cmd.getParsedOptionValue("c")).intValue();
            }

        } catch (ParseException | NumberFormatException e) {
            System.out.println(e.getMessage());
            new HelpFormatter().printHelp("httpd", options);
            System.exit(1);
            return;
        }

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port: "
                    + serverSocket.getLocalPort() + "\n");
        } catch (IOException e) {
            System.out.println("Port " + port + " is blocked.");
            System.exit(-1);
        }

        Executor ex = Executors.newFixedThreadPool(n);

        while (true) {
            try {

                ex.execute(new ClientSession(serverSocket.accept()));

            } catch (IOException e) {
                System.out.println("Failed to establish connection.");
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }
    }

}
