package proyecto.golfus.forat19.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import proyecto.golfus.forat19.Token;

public class Comunicaciones extends AsyncTask<Token,Void,Void> {
Socket s;
PrintWriter salida;



    @Override
    protected Void doInBackground(Token... Voids) {

        Token token=Voids[0];

        try {
            Log.d ("ERROR","Enviando dato: "+token.getUser() +" : "+token.getPassword());

            Socket sk = new Socket("192.168.1.33", 7000);

            ObjectOutputStream ous = new ObjectOutputStream(sk.getOutputStream());
            ous.writeObject(token);
            ous.close();
            sk.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
