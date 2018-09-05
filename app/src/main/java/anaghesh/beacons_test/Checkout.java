package anaghesh.beacons_test;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import ng.max.slideview.SlideView;

public class Checkout extends AppCompatActivity {
    private Button search;
    private CardView cardView;
    private EditText vinNum;
    private TextView beacon_result,vin_result;
    public static SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        toolbarSetup();
        setupUI();
        sharedpreferences = getSharedPreferences("Database", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager) //auto hide keyboard
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                if((vinNum.getText().toString().equalsIgnoreCase(sharedpreferences.getString(ScanQR.BEACON_NUM, "")))||vinNum.getText().toString().equalsIgnoreCase(sharedpreferences.getString(ScanQR.VIN_NUM, "")))
                {
                    beacon_result.setText(sharedpreferences.getString(ScanQR.BEACON_NUM, ""));
                    vin_result.setText(sharedpreferences.getString(ScanQR.VIN_NUM, ""));
                    cardView.setVisibility(View.VISIBLE);
                }
                else
                    Toast.makeText(Checkout.this, "Invalid input", Toast.LENGTH_SHORT).show();
               
            }
        });

        //slide to checkout feature
        SlideView slideView = findViewById(R.id.slideview);
        slideView.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                // vibrate the device
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(200);
                File sharedPreferenceFile = new File("/data/data/"+ getPackageName()+ "/shared_prefs/");
                File[] listFiles = sharedPreferenceFile.listFiles();
                for (File file : listFiles) {
                    file.delete();
                }
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Checkout.this);
                builder.setTitle("Checkout Successful");
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setMessage("Handover the beacon at the exit counter")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
                android.app.AlertDialog alert = builder.create();
                alert.show();


            }
        });

    }
    void setupUI(){

        search = findViewById(R.id.btn_search_checkout);
        cardView = findViewById(R.id.cv_checkout);
        vinNum = findViewById(R.id.et_vin);
        beacon_result = findViewById(R.id.bcn_result);
        vin_result = findViewById(R.id.v_result);

    }
    void toolbarSetup(){
        Toolbar toolbar = findViewById(R.id.toolbar_checkout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
