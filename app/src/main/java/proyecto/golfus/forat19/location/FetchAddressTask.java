package proyecto.golfus.forat19.location;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

/**
 * @author Antonio Rodríguez Sirgado
 */
public class FetchAddressTask extends AsyncTask<Location, Void, Location> {
    private Context mContext;
    private OnTaskCompleted mListener;

    FetchAddressTask(Context applicationContext, OnTaskCompleted listener) {
        mContext = applicationContext;
        mListener = listener;
    }

    @Override
    protected Location doInBackground(Location... params) {

        // Get the passed in location
        Location location = params[0];

        return location;
    }

    @Override
    protected void onPostExecute(Location address) {
        mListener.onTaskCompleted(address);
        super.onPostExecute(address);
    }

    interface OnTaskCompleted {
        void onTaskCompleted(Location result);
    }
}
