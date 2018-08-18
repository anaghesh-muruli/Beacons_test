package anaghesh.beacons_test;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import ng.max.slideview.SlideView;

public class Checkout extends AppCompatActivity {
    private Button search;
    private CardView cardView;
    private EditText vinNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        toolbarSetup();
        setupUI();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                cardView.setVisibility(View.VISIBLE);
            }
        });
        SlideView slideView = findViewById(R.id.slideview);
        slideView.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                // vibrate the device
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(200);

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
    }
    void toolbarSetup(){
        Toolbar toolbar = findViewById(R.id.toolbar_checkout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
