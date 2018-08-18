package anaghesh.beacons_test;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ParkCar extends AppCompatActivity {
private Button confirmPark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_car);
        setupUI();
        confirmPark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ParkCar.this);
                builder.setTitle("Parking Successful");
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setMessage("Your Vehicle has been parked successfully")
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
        confirmPark = findViewById(R.id.btn_confirmpark);
    }
}
