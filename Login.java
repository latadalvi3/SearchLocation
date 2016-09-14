package abc.com.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import abc.com.searchlocation.R;
import abc.com.util.HelperUtil;

/**
 * Created by Lenovo on 14-06-2016.
 */
public class Login extends Activity {
    EditText userName;
    Button submit;
    LinearLayout lLayoutBackPress;
    private Boolean exit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        userName = (EditText)findViewById(R.id.userName);
        submit = (Button)findViewById(R.id.submit);
        lLayoutBackPress = (LinearLayout)findViewById(R.id.lLayoutBackPress);


        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userName.getText().toString().length() > 0) {
                    HelperUtil.userName = userName.getText().toString();

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "User name required..!", Toast.LENGTH_LONG).show();
                }
            }
        });

        lLayoutBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (exit) {
            HelperUtil.APP_IS_RUNNING = true;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }
    @Override
    public void onResume() {
        super.onResume();

        if(HelperUtil.APP_IS_RUNNING){
            Intent i =new Intent(this,SplashActivity.class);
            startActivity(i);
            finish();
        }
    }
}
