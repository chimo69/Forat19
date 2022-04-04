package proyecto.golfus.forat19.utils;

import android.content.Context;
import android.content.res.Configuration;

public class Services {
    /**
     * Comprueba si el dispositivo es una tablet
     * @param context
     * @return true si es dispositivo es una tablet
     */
    public static boolean esTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
