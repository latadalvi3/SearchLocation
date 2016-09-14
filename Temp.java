package abc.com.activity;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import abc.com.searchlocation.R;

/**
 * Created by Lenovo on 27-06-2016.
 */
public class Temp extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        try {
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            System.out.println(longitude+",,"+latitude);
        } catch (SecurityException e) {
            System.out.println("Security Error");
        }
    }
}
