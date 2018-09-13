package anaghesh.beacons_test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class FindCar extends AppCompatActivity {
    private Button search;
    private EditText beaconNum;
    public String BeaconPublicID;
    public int carVIN;
    public double lat,lng;
    String zone, MacId;
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
                FindcarAPI();
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
    //Validate the user input and upload it to BD
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
                            carVIN = Object.getInt("CarVIN");

                            lat = Object.getDouble("Latitude");
                            lng = Object.getDouble("Longitude");
                            zone = Object.getString("PzName");
                            MacId = Object.getString("BeconMacID");

                            Log.e("lat in FC",""+lat);
                            Log.e("Long in FC",""+lng);
                            Log.e("zone in FC",""+zone);
                            Log.e("Macid",""+MacId);



                        }
                        Intent i =new Intent(FindCar.this, FindcarMaps.class);
                        i.putExtra("BeaconPID", BeaconPublicID);
                        i.putExtra("carVIN",carVIN);
                        i.putExtra("lat",lat);
                        i.putExtra("lng",lng);
                        i.putExtra("pzName",zone);
                        i.putExtra("MacId",MacId);
                        startActivity(i);
                    } else if(obj.getInt("Code")==0) {
                        Log.e("Response","0");

                        Toast.makeText(FindCar.this, "VIN does not exist", Toast.LENGTH_SHORT).show();
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
                params.put("CarVIN", beaconNum.getText().toString());
                Log.d("CarVIN", beaconNum.getText().toString());

                return params;
            }
        };
        Singleton.getInstance(getApplicationContext()).addToRequestQueue(rq);
    }
}
