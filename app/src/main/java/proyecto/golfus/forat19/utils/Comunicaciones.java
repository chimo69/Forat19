package proyecto.golfus.forat19.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Comunicaciones extends AsyncTask<String,Void,Void> {
Socket s;
PrintWriter salida;



    @Override
    protected Void doInBackground(String... Voids) {

        String mensaje=Voids[0];
        PrintWriter pw;
        try {
            Log.d ("ERROR","Enviando dato: "+mensaje);
            Socket sk= new Socket("192.168.1.33",7000);

            pw = new PrintWriter(sk.getOutputStream());
            pw.write(mensaje);
            pw.flush();
            pw.close();

            //BufferedReader entrada = new BufferedReader(new InputStreamReader(sk.getInputStream()));
            //PrintWriter salida = new PrintWriter(new OutputStreamWriter(sk.getOutputStream()),true);
            //salida.println(mensaje);
            //Log.d("ERROR","Recibiendo mensaje: "+entrada.readLine());

            sk.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
