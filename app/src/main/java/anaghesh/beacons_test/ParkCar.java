package anaghesh.beacons_test;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(ParkCar.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(ParkCar.this);
                }
                builder.setTitle("Parking Successful")
                        .setMessage("Your vehicle has been parked Successfully")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(ParkCar.this, Navigation_home.class));
                            }
                        })

                        // .setIcon(R.drawable.ic_launcher)
                        .show();
            }
        });
    }
    void setupUI(){
        confirmPark = findViewById(R.id.btn_confirmpark);
    }
}
