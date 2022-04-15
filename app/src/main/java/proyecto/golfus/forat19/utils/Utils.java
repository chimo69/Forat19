package proyecto.golfus.forat19.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.provider.Settings;
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

import proyecto.golfus.forat19.*;

public class Utils extends AppCompatActivity {
    /**
     * Comprueba si el dispositivo es una tablet
     *
     * @param context
     * @return true si es dispositivo es una tablet
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
     * @param activity actividad donde ha de mostrarse
     * @param text texto a mostrar
     * @param time tiempo mostrandose
     */
    public static void showCustomToast(Activity activity, int text, int time) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = new Toast(activity);
                View toast_layout = activity.getLayoutInflater().inflate(R.layout.custom_toast,activity.findViewById(R.id.lytLayout));
                toast.setView(toast_layout);
                TextView textView = toast_layout.findViewById(R.id.toastMessage);
                textView.setText(text);
                toast.setDuration(time);
                toast.show();
            }
        });
    }

    /**
     * @param activity activity donde hay que mostrar el mensaje
     * @param text     texto a mostrar en formato recurso
     * @param time     tiempo mostrandose
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
     * @param activity activity donde hay que mostrar el mensaje
     * @param text     texto a mostrar en formato texto
     * @param i        tiempo mostrado
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
     * Muestra un snackBar
     * @param view vista donde se mostrará
     * @param text texto a mostrar en formato texto
     * @param time tiempo mostrandose
     */
    public static void showSnack(View view, String text, int time){
        Snackbar.make(view,text,time)
                .setTextColor(Color.BLACK)
                .setBackgroundTint(view.getResources().getColor(R.color.green))
                .show();

    }
    /**
     * Muestra un snackBar
     * @param view vista donde se mostrará
     * @param text texto a mostrar en formato recurso
     * @param time tiempo mostrandose
     */
    public static void showSnack(View view, int text, int time){
        Snackbar.make(view,text,time)
                .setTextColor(Color.BLACK)
                .setBackgroundTint(view.getResources().getColor(R.color.green))
                .show();

    }

    /**
     * Devuelve el dispositivo donde esta la aplicaciçon
     * @author Antonio Rodríguez Sirgado
     * @param context
     * @return dispositivo
     */
    public static String getDevice(Context context){
        String device  = Settings.Global.getString(context.getContentResolver(), Settings.Global.DEVICE_NAME);
        return device;
    }


}


