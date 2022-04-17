package proyecto.golfus.forat19.utils;

import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


import java.io.IOException;
import java.util.Observable;

import Forat19.Message;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.*;

/**
 * Clase encargada de realizar la conexión, enviar y recibir mensajes
 * @author Antonio Rodríguez Sirgado
 */
public class RequestServer extends Observable {

    private final int PORT = 5050;
    //private final String IP = "54.216.204.8";
    private final String IP = "192.168.1.33";

    private Socket socket;
    private Object input;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private Boolean connectionOK = true;


    /**
     * Recibe un objeto Message, inicia la conexión y la transacción
     * @author Antonio Rodríguez Sirgado
     * @param message Mensaje recibido
     */
    public void request(Message message) {
        Thread thread = new Thread(() -> {
            initializeConnection(IP, PORT);
            initializeTransaction(message);
        });
        thread.start();
    }

    /**
     * Envia el objeto Message al servidor y recibe una respuesta
     * @author Antonio Rodríguez Sirgado
     * @param message mensaje recibido
     */
    public void initializeTransaction(Message message) {
        if (connectionOK) {
            Log.d("INFO","Send:"+message.getCommand());
            send(message);
        }
        if (connectionOK) {
            retrieveData();
        }

    }

    /**
     * Inicia la conexión
     * @author Antonio Rodríguez Sirgado
     * @param ip dirección del servidor
     * @param port puerto del servidor
     */
    public void initializeConnection(String ip, int port) {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), Global.TIME_OUT_LIMIT);
            Log.d("INFO", "Connect to :" + socket.getInetAddress().getHostName());
            connectionOK = true;
        } catch (Exception e) {
            Log.d("INFO:", "Exception on connection innitialization: " + e.getMessage());
            Reply reply = new Reply(true, R.string.it_was_impossible_to_make_connection);
            connectionOK = false;
            this.setChanged();
            this.notifyObservers(reply);
            this.clearChanged();

        }
    }

    /**
     * Envía el objeto al servidor
     * @author Antonio Rodríguez Sirgado
     * @param o Objeto Message
     */
    public void send(Object o) {
        try {
            Log.d("INFO", "Sending message");
            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(o);
        } catch (IOException e) {
            Log.d("INFO", "IOException on send: " + e.getMessage());
        }
    }

    /**
     * Cierra la conexión
     * @author Antonio Rodríguez Sirgado
     */
    public void closeConnection() {
        try {
            out.close();
            in.close();
            socket.close();
            Log.d("INFO", "Connection ended");
        } catch (IOException e) {
            Log.d("INFO", "IOException on closeConnection()");
        } finally {

        }
    }


    /**
     * Recoge del servidor un obeto Message y lo registra en el observador
     * @author Antonio Rodriguez Sirgado
     * @return Mensaje recibido
     */
    public Message retrieveData() {
        try {
            in = new ObjectInputStream(socket.getInputStream());
            input = in.readObject();
            if (input instanceof Message) {
                Message returnMessage = (Message) input;
                Log.d("INFO", "Received message: " + returnMessage.getToken());
                this.setChanged();
                this.notifyObservers(returnMessage);
                this.clearChanged();
                return returnMessage;
            } else {
                closeConnection();
            }
        } catch (IOException e) {
            Log.d("INFO", "IOException on retrieveData: " + e.toString());
        } catch (ClassNotFoundException e) {
            Log.d("INFO", "ClassNotFoundException: " + e.toString());
        }
        return null;
    }

}
