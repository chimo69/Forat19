package proyecto.golfus.forat19.utils;

import android.util.Log;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;


import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Observable;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

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
    private Key clientKey;

    /**
     * Recibe un objeto Message, inicia la conexión y la transacción
     *
     * @param message Mensaje recibido
     * @author Antonio Rodríguez Sirgado
     */
    public void request(Message message) {
        if (message.getCommand() != null) {
            Thread thread = new Thread(() -> {
                if (message.getCommand().equals(Global.LOGIN) || message.getCommand().equals(Global.VALIDATE_TOKEN)) {
                    newLogin();
                    message.setObject(clientKey);
                }
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
        Log.d(Global.TAG, "La conexión esta: " + connectionOK);
        if (connectionOK) {
            try {
                send(message);
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | InvalidKeyException e) {
                e.printStackTrace();
            }
        }
        if (connectionOK) {
            try {
                retrieveData();
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * Crea una clave para enviar al servidor y mantener comunicaciones
     *
     * @author Antonio Rodríguez Sirgado
     */
    private void newLogin() {

        try {
            initializeConnection(IP, PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject("GetKey");
            in = new ObjectInputStream(socket.getInputStream());
            input = in.readObject();
            Log.d(Global.TAG, "Respuesta del servidor: " + input);
            KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");
            clientKey = keyGenerator.generateKey();
            Log.d(Global.TAG, "Enviando al servidor la key: " + clientKey);
            out.writeObject(clientKey);
            in = new ObjectInputStream(socket.getInputStream());
            input = in.readObject();
            SealedObject sealed = (SealedObject) input;
            Cipher dcipher = Cipher.getInstance("Blowfish");
            dcipher.init(Cipher.DECRYPT_MODE, clientKey);
            Global.serverKey = (Key) sealed.getObject(dcipher);
            Log.d(Global.TAG, "Recibida llave del server: " + Global.serverKey.getEncoded());
            Log.d(Global.TAG, "-------------------------------------------------");
            out.writeObject("Ok");
            closeConnection();

        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
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
            Log.d(Global.TAG, "Conectado a :" + socket.getInetAddress().getHostName());
            Log.d(Global.TAG, "-------------------------------------------------");
            connectionOK = true;
        } catch (Exception e) {
            Log.d(Global.TAG, "Excepción iniciando la conexión" + e.getMessage());
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
    public void send(Message o) throws
            NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException {
            Log.d(Global.TAG, "Envio tipo objeto: "+o.getClass().getName());
        try {

            initializeConnection(IP, PORT);
            out = new ObjectOutputStream(socket.getOutputStream());

            out.writeObject(encryptMessage(o));

            Log.d(Global.TAG, "Enviando mensaje...");
            Log.d(Global.TAG, "Token enviado: " + o.getToken());
            Log.d(Global.TAG, "Parametro enviado: " + o.getParameters());
            Log.d(Global.TAG, "-------------------------------------------------");

        } catch (IOException e) {
            Log.d(Global.TAG, "IOException en send: " + e.getMessage());
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
            Log.d(Global.TAG, "Conexión finalizada");
            Log.d(Global.TAG, "-------------------------------------------------");
        } catch (IOException e) {
            Log.d(Global.TAG, "IOException en closeConnection()");
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
    public void retrieveData() throws
            NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        try {
            in = new ObjectInputStream(socket.getInputStream());
            input = in.readObject();

            if (input instanceof SealedObject) {
                Message returnMessage = desencryptMessage((SealedObject) input);

                Log.d(Global.TAG, "Mensaje recibido encriptado:");
                Log.d(Global.TAG, "Token: " + returnMessage.getToken());
                Log.d(Global.TAG, "Parametros: " + returnMessage.getParameters());
                Log.d(Global.TAG, "Comando: " + returnMessage.getCommand());
                if (!returnMessage.getParameters().equals(Global.OK)){Log.d(Global.TAG, "Respuesta: " + returnMessage.getMessageText());}
                Log.d(Global.TAG, "-------------------------------------------------");

                this.setChanged();
                this.notifyObservers(returnMessage);
                this.clearChanged();
                closeConnection();

            } else if (input instanceof Message) {
                Message returnMessage = (Message) input;

                Log.d(Global.TAG, "Mensaje recibido sin encriptar:");
                Log.d(Global.TAG, "Token: " + returnMessage.getToken());
                Log.d(Global.TAG, "Parametros: " + returnMessage.getParameters());
                Log.d(Global.TAG, "Comando: " + returnMessage.getCommand());
                if (!returnMessage.getParameters().equals(Global.OK)){Log.d(Global.TAG, "Respuesta: " + returnMessage.getMessageText());}
                Log.d(Global.TAG, "-------------------------------------------------");

                this.setChanged();
                this.notifyObservers(returnMessage);
                this.clearChanged();
                closeConnection();

            } else {
                closeConnection();
            }
        } catch (IOException e) {
            Log.d(Global.TAG, "IOException en retrieveData: " + e);
            Reply reply = new Reply(true, R.string.it_was_impossible_to_make_connection);
            this.setChanged();
            this.notifyObservers(reply);
            this.clearChanged();
        } catch (ClassNotFoundException e) {
            Log.d(Global.TAG, "Excepcion Clase no encontrada: " + e);
            reply = new Reply(true, R.string.it_was_impossible_to_make_connection);
            this.setChanged();
            this.notifyObservers(reply);
            this.clearChanged();
        }
    }

    /**
     * Encripta el mensaje recibido con el algorito AES
     *
     * @param me mensaje a encriptar
     * @return devuelve el mensaje encriptado
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @author Antonio Rodríguez Sirgado
     */
    private SealedObject encryptMessage(Message me) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, IllegalBlockSizeException {
        Log.d(Global.TAG,"Encriptando mensaje... "+ Global.serverKey.getEncoded());
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, Global.serverKey);
        SealedObject sealed = new SealedObject(me, cipher);
        Log.d(Global.TAG,"Mensaje encriptado");
        Log.d(Global.TAG, "-------------------------------------------------");
        return sealed;
    }

    /**
     * Desencripta un mensaje en formato SealedObject
     *
     * @param so mensaje a desencriptar
     * @return devuelve el mensaje desencriptado.
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws IOException
     * @throws ClassNotFoundException
     * @author Antonio Rodríguez Sirgado
     */
    private Message desencryptMessage(SealedObject so) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, ClassNotFoundException {
        Log.d(Global.TAG,"Desencriptando mensaje...");
        SealedObject sealed = so;
        Cipher dcipher = Cipher.getInstance("AES");
        dcipher.init(Cipher.DECRYPT_MODE, Global.serverKey);
        input = sealed.getObject(dcipher);
        Message returnMessage = (Message) input;
        Log.d(Global.TAG,"Mensaje desencriptado");
        Log.d(Global.TAG, "-------------------------------------------------");
        return returnMessage;
    }

}
