package abc.com.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;


import abc.com.searchlocation.R;
import abc.com.util.ConnectionDetector;
import abc.com.util.HelperUtil;

/**
 * Created by Lenovo on 12-06-2016.
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        HelperUtil.APP_IS_RUNNING = false;
        next();

    }

    public void next(){
        if(new ConnectionDetector(this).isConnectingToInternet()) {
            LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = false;
            boolean network_enabled = false;

            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }catch (SecurityException e){
                System.out.println("Security error....");
            }

            if(!gps_enabled && !network_enabled) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("Please enable your GPS..!!");
                dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }else{

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        Intent intent = new Intent(SplashActivity.this, Login.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                        finish();

                    }
                }, 5000);

            }



        } else {
            Toast.makeText(this, "Internet Connection Error..!!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                next();
                break;
        }
    }
}
