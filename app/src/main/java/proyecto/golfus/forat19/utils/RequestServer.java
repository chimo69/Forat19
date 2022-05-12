package proyecto.golfus.forat19.utils;

import android.util.Log;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


import java.io.IOException;
import java.util.Observable;

import Forat19.*;
import proyecto.golfus.forat19.Global;
import proyecto.golfus.forat19.*;

/**
 * Clase encargada de realizar la conexión, enviar y recibir mensajes
 *
 * @author Antonio Rodríguez Sirgado
 */
public class RequestServer extends Observable {

    private final int PORT = 5050;
    private final String IP = "54.216.204.8";
    //private final String IP = "192.168.1.33";

    private Socket socket;
    private Object input;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private Boolean connectionOK = true;
    private Reply reply;

    /**
     * Recibe un objeto Message, inicia la conexión y la transacción
     *
     * @param message Mensaje recibido
     * @author Antonio Rodríguez Sirgado
     */
    public void request(Message message) {
        if (message.getCommand() != null) {
            Thread thread = new Thread(() -> {
                initializeConnection(IP, PORT);
                initializeTransaction(message);
            });
            thread.start();
        }
    }

    /**
     * Envia el objeto Message al servidor y recibe una respuesta
     *
     * @param message mensaje recibido
     * @author Antonio Rodríguez Sirgado
     */
    public void initializeTransaction(Message message) {
        if (connectionOK) {
            send(message);
        }
        if (connectionOK) {
            retrieveData();
        }

    }

    /**
     * Inicia la conexión
     *
     * @param ip   dirección del servidor
     * @param port puerto del servidor
     * @author Antonio Rodríguez Sirgado
     */
    public void initializeConnection(String ip, int port) {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), Global.TIME_OUT_LIMIT);
            Log.d(Global.TAG, "Connect to :" + socket.getInetAddress().getHostName());
            connectionOK = true;
        } catch (Exception e) {
            Log.d(Global.TAG, "Exception on connection innitialization: " + e.getMessage());
            reply = new Reply(true, R.string.it_was_impossible_to_make_connection);
            this.setChanged();
            this.notifyObservers(reply);
            this.clearChanged();
            connectionOK = false;
        }
    }

    /**
     * Envía el objeto al servidor
     *
     * @param o Objeto Message
     * @author Antonio Rodríguez Sirgado
     */
    public void send(Object o) {
        try {
            Log.d(Global.TAG, "Enviando mensaje...");
            Log.d(Global.TAG, "Token enviado: " + ((Message) o).getToken());
            Log.d(Global.TAG, "Parametro enviado: " + ((Message) o).getParameters());
            Log.d(Global.TAG, "Comando enviado: " + ((Message) o).getCommand());
            Log.d(Global.TAG,"-------------------------------------------------");
            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(o);


        } catch (IOException e) {
            Log.d(Global.TAG, "IOException on send: " + e.getMessage());
            reply = new Reply(true, R.string.it_was_impossible_to_make_connection);
            this.setChanged();
            this.notifyObservers(reply);
            this.clearChanged();
        }
    }

    /**
     * Cierra la conexión
     *
     * @author Antonio Rodríguez Sirgado
     */
    public void closeConnection() {
        try {
            out.close();
            in.close();
            socket.close();
            Log.d(Global.TAG, "Connection ended");
        } catch (IOException e) {
            Log.d(Global.TAG, "IOException on closeConnection()");
            reply = new Reply(true, R.string.it_was_impossible_to_make_connection);
            this.setChanged();
            this.notifyObservers(reply);
            this.clearChanged();
        } finally {

        }
    }


    /**
     * Recoge del servidor un obeto Message y lo registra en el observador
     *
     * @return Mensaje recibido
     * @author Antonio Rodriguez Sirgado
     */
    public Message retrieveData() {
        try {
            in = new ObjectInputStream(socket.getInputStream());
            input = in.readObject();
            if (input instanceof Message) {
                Message returnMessage = (Message) input;

                Log.d(Global.TAG, "Mensaje recibido:");
                Log.d(Global.TAG, "Token: " + returnMessage.getToken());
                Log.d(Global.TAG, "Parametros: " + returnMessage.getParameters());
                Log.d(Global.TAG, "Comando: " + returnMessage.getCommand());
                Log.d(Global.TAG, "Respuesta: " + returnMessage.getMessageText());
                Log.d(Global.TAG,"-------------------------------------------------");

                this.setChanged();
                this.notifyObservers(returnMessage);
                this.clearChanged();
                return returnMessage;
            } else {
                closeConnection();
            }
        } catch (IOException e) {
            Log.d(Global.TAG, "IOException on retrieveData: " + e.toString());
            Reply reply = new Reply(true, R.string.it_was_impossible_to_make_connection);
            this.setChanged();
            this.notifyObservers(reply);
            this.clearChanged();
        } catch (ClassNotFoundException e) {
            Log.d(Global.TAG, "ClassNotFoundException: " + e.toString());
            reply = new Reply(true, R.string.it_was_impossible_to_make_connection);
            this.setChanged();
            this.notifyObservers(reply);
            this.clearChanged();
        }
        return null;
    }

}
