package org.invokeproject.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class UserHandler extends Thread{
    private ArrayList<UserHandler> users;
    private Socket socket;
    private BufferedReader inReader;
    private PrintWriter outWriter;

    public UserHandler(Socket socket, ArrayList<UserHandler> users) {
        try {
            this.socket = socket;
            this.users = users;
            this.inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.outWriter = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String msg;
            while ((msg = inReader.readLine()) != null) {
                if (msg.equalsIgnoreCase( "exit")) {
                    break;
                }
                for (UserHandler ur : users) {
                    ur.outWriter.println(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                inReader.close();
                outWriter.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeToServer(String input) {
        outWriter.println(input);
    }
}

