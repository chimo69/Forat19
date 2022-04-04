package proyecto.golfus.forat19.utils;


import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Forat19.*;


public class Comunicaciones extends AsyncTask<Message, Integer, Message> {

    private Object input;
    private int PORT = 5050;
    private String IP = "192.168.1.33"; //PC

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("INFO: ", "Empieza assync");
    }

    @Override
    protected Message doInBackground(Message... messages) {

        synchronized (this){
            try {
                Socket sk = new Socket(IP, PORT);
                Message message = messages[0];
                Log.d("INFO: ", "ENVIANDO COMANDO: " + message.getCommand());

                ObjectOutputStream os = new ObjectOutputStream(sk.getOutputStream());
                os.writeObject(message);

                ObjectInputStream is = new ObjectInputStream(sk.getInputStream());

                try {
                    input = is.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                Log.d("INFO: ", "RESPUESTA: " + ((Message) input).getToken());

                sk.close();
                is.close();
                os.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return (Message) input;
        }

    }

    @Override
    protected void onPostExecute(Message message) {
        super.onPostExecute(message);
        Log.d("INFO: ", "Acaba assync");
    }


}
