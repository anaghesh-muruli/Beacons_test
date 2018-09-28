package anaghesh.beacons_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static anaghesh.beacons_test.ScanQR.BEACON_NUM;
import static anaghesh.beacons_test.ScanQR.VIN_NUM;

public class Repark extends AppCompatActivity {
   private EditText vin;
   Button search;
    String BeaconPublicID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repark);
        vin = findViewById(R.id.vin);
        search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FindcarAPI();
            }
        });

    }
    private void FindcarAPI() {
        String Url = "http://ec2-18-216-80-229.us-east-2.compute.amazonaws.com:3000/car/findCarWithCarVIN";

        StringRequest rq = new StringRequest(Request.Method.POST, Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response Text", response);
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getInt("Code")==1) {
                        JSONArray document = obj.getJSONArray("Document");
                        Log.e("Response", "1");
                        for (int i = 0; i < document.length(); i++) {
                            //getting the json object of the particular index inside the array
                            JSONObject Object = document.getJSONObject(i);
                             BeaconPublicID = Object.getString("BeconPublicID");
                        }
                        SharedPreferences sharedPreferences = getSharedPreferences("Database", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(BEACON_NUM,BeaconPublicID);
                        editor.putString(VIN_NUM,vin.getText().toString());
                        //vinString = vin.getText().toString();
                        editor.apply();
                        startActivity(new Intent(Repark.this, Parking.class));

                    } else if(obj.getInt("Code")==0) {
                        Log.e("Response","0");

                        Toast.makeText(Repark.this, "VIN does not exist", Toast.LENGTH_SHORT).show();
                    }

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("Response Error", error.toString());
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }

        }) {

            @Override
            protected Map<String, String> getParams() {
                Log.e("Inside","getParams");

                Map<String, String> params = new HashMap<String, String>();
                params.put("CarVIN", vin.getText().toString());
                Log.d("CarVIN", vin.getText().toString());

                return params;
            }
        };
        Singleton.getInstance(getApplicationContext()).addToRequestQueue(rq);
    }
}
