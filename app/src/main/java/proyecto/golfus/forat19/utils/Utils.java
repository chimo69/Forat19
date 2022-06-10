package proyecto.golfus.forat19.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Forat19.Message;
import Forat19.Users;
import proyecto.golfus.forat19.*;

/**
 * Clase con utilidades varias para el proyecto
 *
 * @author Antonio Rodríguez Sirgado
 */
public class Utils extends AppCompatActivity {

    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;


    /**
     * Comprueba si el dispositivo es una tablet
     *
     * @param context contexto
     * @return true si el dispositivo es una tablet
     * @author Antonio Rodríguez Sirgado
     */
    public static boolean esTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * Oculta el teclado de la actividad recibida
     *
     * @param activity Actividad donde hay que ocultar el teclado
     * @author Antonio Rodríguez Sirgado
     */
    public static void hideKeyboard(@NonNull Activity activity) {

        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Muestras un Toast personalizado
     *
     * @param activity actividad donde ha de mostrarse
     * @param text     texto a mostrar
     * @param time     tiempo mostrandose
     * @author Antonio Rodríguez Sirgado
     */
    public static void showCustomToast(Activity activity, int text, int time) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = new Toast(activity);
                View toast_layout = activity.getLayoutInflater().inflate(R.layout.custom_toast, activity.findViewById(R.id.lytLayout));
                toast.setView(toast_layout);
                TextView textView = toast_layout.findViewById(R.id.toastMessage);
                textView.setText(text);
                toast.setDuration(time);
                toast.show();
            }
        });
    }

    /**
     * Muestra un Toast con el recurso Int y tiempo que recibe
     *
     * @param activity activity donde hay que mostrar el mensaje
     * @param text     texto a mostrar en formato recurso
     * @param time     tiempo mostrandose
     * @author Antonio Rodríguez Sirgado
     */
    public static void showToast(Activity activity, int text, int time) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, text, time).show();
            }
        });
    }


    /**
     * Muestra un Toast con el texto que recibe y tiempo
     *
     * @param activity activity donde hay que mostrar el mensaje
     * @param text     texto a mostrar en formato texto
     * @param i        tiempo mostrado
     * @author Antonio Rodríguez Sirgado
     */
    public static void showToast(Activity activity, String text, int i) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, text, i).show();
            }
        });
    }

    /**
     * Muestra un snackBar con el texto y el tiempo recibido
     *
     * @param view vista donde se mostrará
     * @param text texto a mostrar en formato texto
     * @param time tiempo mostrandose
     * @author Antonio Rodríguez Sirgado
     */
    public static void showSnack(View view, String text, int time) {
        Snackbar.make(view, text, time)
                .setTextColor(Color.BLACK)
                .setBackgroundTint(view.getResources().getColor(R.color.green))
                .show();

    }

    /**
     * Muestra un snackBar con el recurso Int y el tiempo recibido
     *
     * @param view vista donde se mostrará
     * @param text texto a mostrar en formato recurso
     * @param time tiempo mostrandose
     * @author Antonio Rodríguez Sirgado
     */
    public static void showSnack(View view, int text, int time) {
        Snackbar.make(view, text, time)
                .setTextColor(Color.BLACK)
                .setBackgroundTint(view.getResources().getColor(R.color.green))
                .show();

    }

    /**
     * Devuelve el dispositivo donde esta la aplicaciçon
     *
     * @param context contexto
     * @return dispositivo
     * @author Antonio Rodríguez Sirgado
     */
    public static String getDevice(Context context) {
        String device = Settings.Global.getString(context.getContentResolver(), Settings.Global.DEVICE_NAME);
        return device;
    }

    public static void sendRequest(Activity activity, String command, String parameter, Object object) {

        String token = null;
        if (!command.equals(Global.LOGIN)) {
            token = getActiveToken(activity);
        }

        Message message = new Message(token + "¬" + getDevice(activity), command, parameter, object);

        RequestServer request = new RequestServer();
        request.request(message);
        request.addObserver((Observer) activity);



    }


    /**
     * Devuelve el usuario activo guardado en la aplicacion
     *
     * @param activity activity donde se llama
     * @return usuario activo
     */
    public static String getActiveUser(Activity activity) {
        preferences = activity.getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        String activeUser = preferences.getString(Global.PREF_ACTIVE_USER, null);
        return activeUser;
    }

    /**
     * Devuelve si esta definido mantener sesion abierta
     *
     * @param activity activity donde se llama
     * @return true si esta activo
     */
    public static Boolean getSessionStatus(Activity activity) {
        preferences = activity.getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        boolean status = preferences.getBoolean(Global.PREF_OPEN_KEEP_SESSION_OPEN, false);
        return status;
    }

    /**
     * Devuelve el id Activo guardado en la aplicacion
     *
     * @param activity activity donde se llama
     * @return id Activo
     */
    public static String getActiveId(Activity activity) {
        preferences = activity.getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        int activeID = preferences.getInt(Global.PREF_ACTIVE_ID, 0);
        return String.valueOf(activeID);
    }

    /**
     * Devuelve el Token Activo guardado en la aplicacion
     *
     * @param activity activity donde se llama
     * @return Token Activo
     */
    public static String getActiveToken(Activity activity) {
        preferences = activity.getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        String activeToken = preferences.getString(Global.PREF_ACTIVE_TOKEN, null);
        return String.valueOf(activeToken);
    }

    /**
     * Devuelve el Tipo de Usuario Activo guardado en la aplicacion
     *
     * @param activity activity donde se llama
     * @return Tipo de Usuario Activo
     */
    public static int getActiveTypeUser(Activity activity) {
        preferences = activity.getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        int activeTypeUser = preferences.getInt(Global.PREF_TYPE_USER, 1);
        return activeTypeUser;
    }

    /**
     * Guarda en el movil el tipo de usuario
     *
     * @param activity actividad desde donde es llamado
     * @param typeUser tipo de usuario activo
     */
    public static void setActiveTypeUser(Activity activity, int typeUser) {
        preferences = activity.getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putInt(Global.PREF_TYPE_USER, typeUser);
        editor.apply();
    }

    /**
     * Guarda en el movil el token activo
     *
     * @param activity actividad desde donde es llamado
     * @param token    tipo de usuario activo
     */
    public static void setActiveToken(Activity activity, String token) {
        preferences = activity.getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(Global.PREF_ACTIVE_TOKEN, token);
        editor.apply();
    }

    /**
     * Guarda en el movil el usuario activo
     *
     * @param activity actividad desde donde es llamado
     * @param user     usuario activo
     */
    public static void setActiveUser(Activity activity, String user) {
        preferences = activity.getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(Global.PREF_ACTIVE_USER, user);
        editor.apply();
    }

    /**
     * Guarda en el movil el estado de la sesion
     *
     * @param activity actividad desde donde es llamado
     * @param status   estado de la sesion
     */
    public static void setSessionStatus(Activity activity, boolean status) {
        preferences = activity.getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean(Global.PREF_OPEN_KEEP_SESSION_OPEN, status);
        editor.apply();
    }

    /**
     * Guarda en el movil el id del usuario activo
     *
     * @param activity actividad desde donde es llamado
     * @param id       id del usuario activo
     */
    public static void setActiveId(Activity activity, int id) {
        preferences = activity.getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putInt(Global.PREF_ACTIVE_ID, id);
        editor.apply();
    }

    /**
     * Guarda en el movil el id del usuario activo
     *
     * @param activity actividad desde donde es llamado
     * @param status       estado del switch de mostrar partidos finalizados
     */
    public static void setShowEndedStatus(Activity activity, boolean status) {
        preferences = activity.getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean(Global.PREF_SHOW_ENDED_STATUS, status);
        editor.apply();
    }

    /**
     * Devuelve si esta definido mostrar los partidos acabados
     *
     * @param activity activity donde se llama
     * @return true si esta activo
     */
    public static Boolean getShowEndedStatus(Activity activity) {
        preferences = activity.getSharedPreferences("Credentials", Context.MODE_PRIVATE);
        boolean status = preferences.getBoolean(Global.PREF_SHOW_ENDED_STATUS, false);
        return status;
    }

    /**
     * Cambia el formato entero americano a texto español
     * @param date fecha a formatear
     * @return fecha formateada
     */
    public static String changeDateFormat (int date) {
        String dateGame = String.valueOf(date);
        return dateGame.substring(6,8)+"/" +dateGame.substring(4,6) + "/" + dateGame.substring(0,4);
    }

    /**
     * Cambia el formato entero americano a texto español
     * @param date fecha a formatear
     * @return fecha formateada
     */
    public static int changeDateFormat (String date) {
        String day = date.substring(0,2);
        String month = date.substring(3,5);
        String year = date.substring(6,10);

        Log.d(Global.TAG, date);
        Log.d(Global.TAG, day);
        Log.d(Global.TAG, month);
        Log.d(Global.TAG, year);

        String dateInt = year+month+day;
        return Integer.parseInt(dateInt);
    }

    /**
     * Convierte en Int la fecha pasada por argumento
     * @param date fecha en formato string
     * @param t parte de la fecha a coger (d=day;m=month,y=year)
     * @return fecha convertida a int
     * @author Antonio Rodríguez Sirgado
     */
    public static int dateToInt (String date, String t){
        switch (t){
            case "d":
                String day = date.substring(0,2);
                return Integer.parseInt(day);
            case "m":
                String month = date.substring(3,5);
                return Integer.parseInt(month);
            case "y":
                String year = date.substring(6,10);
                return Integer.parseInt(year);
        }
        return 0;
    }


    /**
     * Convierte en Int la fecha pasada por argumento
     * @param date fecha en formato int
     * @param t parte de la fecha a coger (d=day;m=month,y=year)
     * @return fecha convertida a int
     * @author Antonio Rodríguez Sirgado
     */
    public static int dateToInt (int date, String t){
        String dateInt = String.valueOf(date);
        switch (t){
            case "d":
                String day = dateInt.substring(6,8);
                return Integer.parseInt(day);
            case "m":
                String month = dateInt.substring(4,6);
                return Integer.parseInt(month);
            case "y":
                String year = dateInt.substring(0,4);
                return Integer.parseInt(year);
        }
        return 0;
    }
    /**
     * Convierte en Int la hora pasada por argumento
     * @param time hora en formato string
     * @param t parte de la hora a coger (h=hour, m=minute)
     * @return hora convertida a int
     * @author Antonio Rodríguez Sirgado
     */
    public static int timeToInt (String time, String t){

        switch (t){
            case "h":
                String hour = time.substring(0,2);
                return Integer.parseInt(hour);
            case "m":
                String minute = time.substring(3,5);
                return Integer.parseInt(minute);
        }
        return 0;
    }

    /**
     * Calcula el tiempo transcurrido y lo pasa a formato String
     * @param initialTime tiempo inicial
     * @param endTime tiempo final
     * @return tiempo en formato string
     * @author Antonio Rodriguez Sirgado
     */
    public static String calculeTime(long initialTime, long endTime, long elapsedTime) {

        long tiempoTranscurrido = ( endTime - initialTime )+ elapsedTime;

        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
        formatoHora.setTimeZone(TimeZone.getTimeZone("UTC"));

        return formatoHora.format(tiempoTranscurrido);
    }

    public static String formatTime(long elapsedTime){
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
        formatoHora.setTimeZone(TimeZone.getTimeZone("UTC"));

    return formatoHora.format(elapsedTime);
    }



}


