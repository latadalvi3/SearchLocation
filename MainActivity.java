package abc.com.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import abc.com.searchlocation.R;
import abc.com.util.ConnectionDetector;
import abc.com.util.HelperUtil;

/**
 * Created by Lenovo on 12-06-2016.
 */
public class MainActivity extends Activity implements AsyncResponse, LocationListener {
    TextView tvAddressResponse;
    TextView tvStatus;
    EditText edtSearchAddress;
    Button btnSearch;
    Button btnAdvSearch;
    JSONParser jParser;
    Context context;
    Location location = null;
    JSONObject json;
    double longitude;
    double latitude;
    String searchAddressString;
    LinearLayout lLayoutBackPress;
    AddressVo addressVo;
    LocationManager lm;
    double min=0;
    double dist;
    ArrayList<AddressVo> responceVoArrayList;

    @Override
    public void onLocationChanged(Location pLocation) {
        Log.v("LocationCH", "}" + pLocation);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this,"The app does not have permission to get location",Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            Toast.makeText(this,"Got Location !",Toast.LENGTH_SHORT).show();
            //pLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

           // System.out.println("location="+pLocation);
            longitude = pLocation.getLongitude();
            latitude = pLocation.getLatitude();
          //  System.out.println("latitude="+latitude+", longitude="+longitude);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        Log.v("StatusCH", "}" + provider+" - "+status);
    }

    public void onProviderEnabled(String provider)
    {
        Log.v("onProvEn", "}" + provider);
    }
    public void onProviderDisabled(String provider)
    {
        Log.v("onProvDis", "}" + provider);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_main);
        lm=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        findViewById();
        getLocation();
        Criteria c=new Criteria();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtSearchAddress.getWindowToken(), 0);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        jParser = new JSONParser();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAddressString = edtSearchAddress.getText().toString();
                if (searchAddressString.length() > 0) {
                    new AsyncTask<String, String, String>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            responceVoArrayList = null;
                            tvStatus.setText("");
                            tvAddressResponse.setText("");
                        }

                        @Override
                        protected void onPostExecute(String s) {

                            super.onPostExecute(s);
                            if(responceVoArrayList !=null && !responceVoArrayList.isEmpty())
                            {
                                String status = new String();
                                String address = new String();
                                for(int i=0; i<responceVoArrayList.size(); i++)
                                {
                                    AddressVo tempObject = responceVoArrayList.get(i);
                                    status = tempObject.getStatus();
                                    address +="\n"+tempObject.getAddress();

                                }
                                tvStatus.setText(status);
                                tvAddressResponse.setText(address);
                            } else {
                                tvStatus.setText("");
                                tvAddressResponse.setText("");
                                if(responceVoArrayList.isEmpty()) {
                                    Toast.makeText(getApplicationContext(),"No records found...",Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Hey, our servers are busy at the moment. Please try again in sometime.", Toast.LENGTH_LONG).show();
                                }
                            }


                        }
                        @Override
                        protected String doInBackground(String... args) {

                            // Getting username and password from user input
                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            params.add(new BasicNameValuePair("email", HelperUtil.userName));
                            params.add(new BasicNameValuePair("lat", String.valueOf(latitude)));
                            params.add(new BasicNameValuePair("lon", String.valueOf(longitude)));
                            params.add(new BasicNameValuePair("keyword", searchAddressString));
                            try {
                                DefaultHttpClient httpClient = new DefaultHttpClient();
                                String paramString = URLEncodedUtils.format(params, "utf-8");
                                String url = "http://env-1318921.cloud.cms500.com/loc/PassToLBS";
                                url += "?" + paramString;
                                System.out.println("request uel = "+url);
                                HttpGet httpGet = new HttpGet(url);

                                HttpResponse httpResponse = httpClient.execute(httpGet);
                                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                                String infoString = reader.readLine();
                                System.out.println("infoString=" + infoString+" url = "+url);
                                responceVoArrayList = new Gson().fromJson(infoString, new TypeToken<ArrayList<AddressVo>>(){}.getType());
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                return null;
                            } catch (ClientProtocolException e) {
                                e.printStackTrace();
                                return null;
                            } catch (IOException e) {
                                e.printStackTrace();
                                return null;
                            }
                            return null;
                        }
                    }.execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Keyword id required..!", Toast.LENGTH_LONG).show();
                }                abc();
            }
        });

        btnAdvSearch.setOnClickListener(new View.OnClickListener() {
                @Override
            public void onClick(View v) {
                searchAddressString = edtSearchAddress.getText().toString();
                if (searchAddressString.length() > 0) {
                    AsyncTask<String, String, String> execute = new AsyncTask<String, String, String>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            responceVoArrayList = null;
                            tvStatus.setText("");
                            tvAddressResponse.setText("");
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);

                            if (responceVoArrayList != null && !responceVoArrayList.isEmpty()) {
                                String status = new String();
                                String address = new String();
                                for (int i = 0; i < responceVoArrayList.size(); i++) {
                                    AddressVo tempObject = responceVoArrayList.get(i);
                                    status = tempObject.getStatus();
                                    address += "\n" + tempObject.getAddress();
                                }
                                tvStatus.setText(status);
                                tvAddressResponse.setText(address);
                            } else {
                                tvStatus.setText("");
                                tvAddressResponse.setText("");
                                if (responceVoArrayList.isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "No records found...", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Hey, our servers are busy at the moment. Please try again in sometime.", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        protected String doInBackground(String... args) {

                            // Getting username and password from user input
                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            params.add(new BasicNameValuePair("email", HelperUtil.userName));
                            params.add(new BasicNameValuePair("lat", String.valueOf(latitude)));
                            final boolean lon = params.add(new BasicNameValuePair("lon", String.valueOf(longitude)));
                            boolean keyword = params.add(new BasicNameValuePair("keyword", searchAddressString));

                            try {
                                DefaultHttpClient httpClient = new DefaultHttpClient();
                                String paramString = URLEncodedUtils.format(params, "utf-8");
                                String url = "http://env-1318921.cloud.cms500.com/lbs/LoginServlet";
                                url += "?" + paramString;
                                HttpGet httpGet = new HttpGet(url);
                                HttpResponse httpResponse = httpClient.execute(httpGet);

                                BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
                                String infoString = reader.readLine();
                                System.out.println("infoString=" + infoString+ " url = "+url);
                                responceVoArrayList = new Gson().fromJson(infoString, new TypeToken<ArrayList<AddressVo>>(){}.getType());
                                System.out.println("responceVoArrayList==" + new Gson().toJson(responceVoArrayList));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                return null;
                            } catch (ClientProtocolException e) {
                                e.printStackTrace();
                                return null;
                            } catch (IOException e) {
                                e.printStackTrace();
                                return null;
                            }
                            return null;
                        }
                    }.execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Keyword id required..!", Toast.LENGTH_LONG).show();
                }
                abc();
            }
        });

        lLayoutBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void findViewById(){
        tvAddressResponse = (TextView)findViewById(R.id.tvAddressResponse);
        tvStatus = (TextView)findViewById(R.id.tvStatus);
        edtSearchAddress = (EditText)findViewById(R.id.edtSearchAddress);
        btnSearch = (Button)findViewById(R.id.btnSearch);
        btnAdvSearch = (Button)findViewById(R.id.btnAdvSearch);
        lLayoutBackPress = (LinearLayout)findViewById(R.id.lLayoutBackPress);

    }

    public void setResponse(AddressVo addressVo){
        tvStatus.setText(addressVo.getStatus());
        tvAddressResponse.setText(addressVo.getAddress());
    }

    public void getLocation() {
        if (new ConnectionDetector(this).isConnectingToInternet()) {
            try {
                lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                System.out.println("lm="+lm);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);
            } catch (SecurityException e) {
                System.out.println("Security error....");
            }

        } else {
            Toast.makeText(this, "Internet Connection Error..!!!", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
        finish();
    }

    @Override
    public void processFinish(String output) {
        System.out.println("output="+output);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtSearchAddress.getWindowToken(), 0);
    }

    public void abc(){
        searchAddressString = edtSearchAddress.getText().toString();
        if(searchAddressString.length()>0) {
            AsyncResponseClass asyncResponseClass = new AsyncResponseClass(context,String.valueOf(latitude),String.valueOf(longitude),String.valueOf(searchAddressString));
            asyncResponseClass.delegate = this;
            asyncResponseClass.execute();
            edtSearchAddress.setText("");
        }else {
            Toast.makeText(getApplicationContext(),"Keyword id required..!",Toast.LENGTH_SHORT).show();
        }
    }
}