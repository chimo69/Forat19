package proyecto.golfus.forat19.utils;

import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Forat19.*;
import proyecto.golfus.forat19.LoginScreen;

public class Comunicaciones {

    private final String IP = "192.168.1.33";
    private final int PORT = 5050;

    private ObjectOutputStream os;
    private ObjectInputStream is;
    private Object input;
    private String respuesta;

    public boolean checkToken(Message message) {


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    Log.d("INFO: ", "ENVIANDO COMANDO: " + message.getCommand());

                    Socket sk = new Socket(IP, PORT);

                    os = new ObjectOutputStream(sk.getOutputStream());
                    os.writeObject(message);

                    is = new ObjectInputStream(sk.getInputStream());

                    try {
                        input = is.readObject();
                        Log.d("INFO: ", "RECIBIENDO: " + ((Message) input).getToken());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    respuesta = ((Message) input).getToken();
                    Log.d("INFO: ", "RESPUESTA: " + ((Message) input).getToken());

                    sk.close();


                } catch (IOException e) {
                    e.printStackTrace();
                }
           }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LoginScreen.loading.setVisibility(View.INVISIBLE);
        if (respuesta.equals("ValidToken")) {

            return true;
        }



        return false;

    }
}
