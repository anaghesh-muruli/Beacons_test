package anaghesh.beacons_test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FindCar extends AppCompatActivity {
    private Button search;
    private EditText beaconNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_car);
        setupUI();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FindCar.this, FindcarMaps.class));
            }
        });
    }
    void setupUI(){
    search = findViewById(R.id.search_fc);
    beaconNum = findViewById(R.id.et_beacon_num);
    }
}
