package proyecto.golfus.forat19.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

import classes.Token;

public class Comunicaciones extends AsyncTask<Token,Void,Void> {


    @Override
    protected Void doInBackground(Token... Voids) {

        Token token=Voids[0];

        try {
            Log.d ("ERROR","Enviando dato: "+token.getUser() +" : "+token.getPassword());

            Socket sk = new Socket("192.168.1.33", 7000);

            ObjectOutputStream ous = new ObjectOutputStream(sk.getOutputStream());
            //BufferedReader entrada = new BufferedReader(new InputStreamReader(sk.getInputStream()));

            ous.writeObject(token);
            //Log.d("RECIBIENDO: ",entrada.readLine());
            ous.close();
            sk.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
