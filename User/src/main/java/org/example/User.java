package org.example;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class User {
    public static void main(String[] args) throws InterruptedException {


        Thread nameWrite = new Thread(() -> {
            String host = "127.0.0.1";
            String messageForUser = "Введите имя пользователя:";
            System.out.println(messageForUser);
            Scanner sc = new Scanner(System.in);
            String name = sc.next();

            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                try (
                        Socket clientSocket = new Socket(host, readFileForGetPort());
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
                ) {
                    out.println(name);

                    String resp = in.readLine();
                    System.out.println(resp);


                    //for (int i = 0; i < 10; i++) {
                    out.println("user's message  " + i);
                    //    Thread.sleep(500);
                    //}
                    out.println("/exit");

                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
                }
            }
        });

        nameWrite.join();
        nameWrite.start();


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
}