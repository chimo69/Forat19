package proyecto.golfus.forat19.utils;

import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Forat19.Message;

public class RequestServer {

    private final int PORT = 5050;
    private final String IP = "192.168.1.33";
    //private final String IP ="54.155.165.31";
    private Socket socket;
    private Object input;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private Message message=null;

    public Message getMessage() {
        return message;
    }

    public void request(Message message) {
        Thread thread = new Thread(() -> {
            initializeConnection(IP, PORT);
            initializeTransaction(message);
        });
        thread.start();
    }

    public void initializeTransaction(Message message) {
        send(message);
        this.message = retrieveData();
    }


    public void initializeConnection(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            Log.d("INFO", "Connect to :" + socket.getInetAddress().getHostName());
        } catch (Exception e) {
            Log.d("INFO:", "Exception on connection innitialization: " + e.getMessage());
            //System.exit(0);
        }
    }

    public void send(Object o) {
        try {
            Log.d("INFO", "Sending message");
            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(o);
        } catch (IOException e) {
            Log.d("INFO", "IOException on send: " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            out.close();
            in.close();
            socket.close();
            Log.d("INFO", "Connection ended");
        } catch (IOException e) {
            Log.d("INFO", "IOException on closeConnection()");
        } finally {
            System.exit(0);
        }
    }


    public Message retrieveData() {
        try {
            Log.d("INFO", "Receiving message");
            in = new ObjectInputStream(socket.getInputStream());
            input = in.readObject();
            if (input instanceof Message) {
                Message returnMessage = (Message) input;
                Log.d("INFO", "Received message: "+returnMessage.getToken());
                return returnMessage;
            } else {
                closeConnection();
            }
        } catch (IOException e) {
            Log.d("INFO", "IOExceptin on retrieveData: " + e.toString());
        } catch (ClassNotFoundException e) {
            Log.d("INFO", "ClassNotFoundException: " + e.toString());
        }
        return null;
    }

}
