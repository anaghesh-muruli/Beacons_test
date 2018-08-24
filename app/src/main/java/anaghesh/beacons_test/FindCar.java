package anaghesh.beacons_test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FindCar extends AppCompatActivity {
    private Button search;
    private EditText beaconNum;

    public static SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_car);
        toolbarSetup();
        setupUI();
        sharedpreferences = getSharedPreferences("Database",
                Context.MODE_PRIVATE);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
    }
    void setupUI(){
    search = findViewById(R.id.search_fc);
    beaconNum = findViewById(R.id.et_beacon_num);
    }
    void toolbarSetup(){
        Toolbar toolbar = findViewById(R.id.toolbar_findcar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    void validate(){
        sharedpreferences = getSharedPreferences("Database",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if((beaconNum.getText().toString().equalsIgnoreCase(sharedpreferences.getString(ScanQR.BEACON_NUM, "")))||beaconNum.getText().toString().equalsIgnoreCase(sharedpreferences.getString(ScanQR.VIN_NUM, "")))
        {
           startActivity(new Intent(this, FindcarMaps.class));
        }
        else
            Toast.makeText(getApplicationContext(), "This Car Number is Not Assigned", Toast.LENGTH_SHORT).show();
    }
}
