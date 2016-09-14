package abc.com.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import abc.com.util.HelperUtil;

/**
 * Created by Lenovo on 27-06-2016.
 */

    public class AsyncResponseClass extends AsyncTask<String, String, String> {
    Context context;
    ProgressDialog progressDialog;
    String appVersionInfo;
    String latitude;
    String longitude;
    String searchAddressString;

    public AsyncResponse delegate = null;

    public AsyncResponseClass() {
    }

    public AsyncResponseClass(Context context, String latitude,String longitude,String searchAddressString) {
        this.context = context;
        this.appVersionInfo = appVersionInfo;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();


        this.latitude=latitude;
        this.longitude=longitude;
        this.searchAddressString=searchAddressString;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... params) {
        String response = null;
        try {
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("email", HelperUtil.userName));
            params1.add(new BasicNameValuePair("lat", String.valueOf(latitude)));
            params1.add(new BasicNameValuePair("lon", String.valueOf(longitude)));
            params1.add(new BasicNameValuePair("keyword",searchAddressString));


            DefaultHttpClient httpClient = new DefaultHttpClient();
            String paramString = URLEncodedUtils.format(params1, "utf-8");
            String url = "http://env-1318921.cloud.cms500.com/lbs/PassToLBS";
            url += "?" + paramString;
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);

            response = readResponse(httpResponse);
        } catch (Exception exception) {
        }

        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);

        progressDialog.hide();
        if (response != null && response != "" && !response.equals(null) && !response.equals("")) {
            delegate.processFinish(response);
        } else {
            Toast.makeText(context, "Hey, our servers are busy at the moment. Please try again in sometime.", Toast.LENGTH_LONG).show();
        }
    }

    public String readResponse(HttpResponse httpResponse) {
        InputStream inputStream = null;
        StringBuilder output = null;
        try {
            output = new StringBuilder();
            inputStream = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
        } catch (Exception e) {
            System.out.println("Exception =" + e);
        }
        return output.toString();

    }
}

