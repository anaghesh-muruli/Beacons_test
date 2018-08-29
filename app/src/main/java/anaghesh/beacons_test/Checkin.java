package anaghesh.beacons_test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

/* This activity is deprecated */

public class Checkin extends AppCompatActivity {
    private CardView cardView;
    private Button search, qrScan;
    private EditText beacon_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
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
        qrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(Checkin.this, ScanQR.class));
            }
        });
    }
    void setupUI(){
        cardView = findViewById(R.id.card_view);
        search = findViewById(R.id.btn_search);
        qrScan = findViewById(R.id.btn_qrscan);
        beacon_num = findViewById(R.id.et_beacon_num);
    }
    void toolbarSetup(){
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

}
