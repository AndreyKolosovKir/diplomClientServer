package org.example;

import org.joda.time.DateTime;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static void main(String[] args) throws IOException, InterruptedException {
        Thread setFiles = new Thread(() -> {
            crateFileLog();
            crateFileSettings();
        });
        setFiles.start();
        setFiles.join();
        setFiles.isAlive();


        try (ServerSocket serverSocket = new ServerSocket(readFileForGetPort())) {
            while (true) {
                try (
                        Socket clientSocket = serverSocket.accept();
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                ) {
                    System.out.println("New connection accepted");
                    String name = in.readLine();

                    out.println(String.format("Hi %s, your port is %d, it is new your chat", name, clientSocket.getPort()));

                    try {
                        String message;
                        while ((message = in.readLine()) != null) {
                            if (message.equals("/exit"))
                                break;
                            System.out.println(name + " - " + message);
                            write(message, name);
                        }
                    } catch (IOException e) {
                        System.out.println(e);

                    } finally {
                        System.out.println("closing...");
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static void crateFileLog() {
        File file = new File("file.log");
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void crateFileSettings() {
        File file = new File("settings.txt");
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream("settings.txt");
            fileOutputStream.write("34555".getBytes());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static int readFileForGetPort() {
        File file = new File("settings.txt");
        List<Integer> portInt = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            int i;
            while ((i = fileInputStream.read()) != -1) {
                portInt.add(i - '0');
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        int result = 0;
        for (int i : portInt) {
            result = result * 10 + i;
        }
        return result;
    }

    public static void write(String message, String name) {
        DateTime dateTime = new DateTime();
        String messageResult = ("[" + dateTime + "//" + name + "//" + message + "]\r\n");
        byte[] buffer = messageResult.getBytes();
        File file = new File("file.log");
        try (FileOutputStream out = new FileOutputStream(file, true);
             BufferedOutputStream buf = new BufferedOutputStream(out)) {
            buf.write(buffer, 0, buffer.length);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}