package anaghesh.beacons_test;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import ng.max.slideview.SlideView;

public class Checkout extends AppCompatActivity {
    private Button search;
    private CardView cardView;
    private EditText vinNum;
    private TextView beacon_result,vin_result;
    public static SharedPreferences sharedpreferences;
    private int beaconID, carID;
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
                checkoutFindAPI();
                InputMethodManager inputManager = (InputMethodManager) //auto hide keyboard
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

               
            }
        });

        //slide to checkout feature
        SlideView slideView = findViewById(R.id.slideview);
        slideView.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                checkoutAPI();



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
    private void checkoutFindAPI() {
        String Url = "http://ec2-18-216-80-229.us-east-2.compute.amazonaws.com:3000/car/findCarWithCarVIN";

        StringRequest rq = new StringRequest(Request.Method.POST, Url , new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                       Log.d("Response Text", response);
                         try {
                             JSONObject obj = new JSONObject(response);
                             if (obj.getInt("Code")==1) {
                                 JSONArray document = obj.getJSONArray("Document");

                                     cardView.setVisibility(View.VISIBLE);
                                     Log.e("Response", "1");

                                     for (int i = 0; i < document.length(); i++) {
                                         //getting the json object of the particular index inside the array
                                         JSONObject Object = document.getJSONObject(i);
                                         carID = Object.getInt("CarID");
                                         beaconID = Object.getInt("BeconID");
                                         vin_result.setText(""+Object.getInt("CarVIN"));
                                         beacon_result.setText(Object.getString("BeconPublicID"));
                                         Log.e("BeaconId", beacon_result.getText().toString());

                                     }

                                 } else if(obj.getInt("Code")==0) {
                                 Log.e("Inside","else if");
                                 cardView.setVisibility(View.INVISIBLE);
                                 Toast.makeText(Checkout.this, "VIN does not exist", Toast.LENGTH_SHORT).show();
                                 }

                         }
                        catch (JSONException e) {
                            Toast.makeText(Checkout.this, "Please try again", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                cardView.setVisibility(View.INVISIBLE);
                Log.d("Response Error", error.toString());
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }

        }) {

            @Override
            protected Map<String, String> getParams() {
                Log.e("Inside","getParams");
                Log.e("CarVIN", vinNum.getText().toString());
                Map<String, String> params = new HashMap<String, String>();
                params.put("CarVIN", vinNum.getText().toString());
                Log.d("CarVIN", vinNum.getText().toString());
                return params;
            }
        };
        Singleton.getInstance(getApplicationContext()).addToRequestQueue(rq);
    }
    private void checkoutAPI() {
        String Url = "http://ec2-18-216-80-229.us-east-2.compute.amazonaws.com:3000/becon_car_map/updateCheckedOut";

        StringRequest rq = new StringRequest(Request.Method.POST, Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response Text", response);
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getInt("Code")==1) {
                        Log.e("Response","1");
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



                    } else {
                        Toast.makeText(getApplicationContext(), "Checkout unsuccessful", Toast.LENGTH_LONG).show();

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
                Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
            }

        }) {

            @Override
            protected Map<String, String> getParams() {
                Log.e("Inside","getParams");
                Log.e("CarVIN", vinNum.getText().toString());
                Map<String, String> params = new HashMap<String, String>();
                params.put("checkedOutStatus", "1");
                params.put("BeconID", ""+beaconID);
                params.put("CarID", ""+carID);

                Log.d("BeconID", ""+beaconID);
                return params;
            }
        };
        Singleton.getInstance(getApplicationContext()).addToRequestQueue(rq);
    }

}
