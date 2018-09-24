package anaghesh.beacons_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RePark extends AppCompatActivity {

    EditText carVin, beaconPublicId;
    Button rePark;
    ImageView bcnVerify,vinVerify;
    Boolean flag1 =false,flag2 = false;
    private int beaconID, carID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_park);

        //toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //decleration of variables
        carVin = (EditText)findViewById(R.id.carVin);
        beaconPublicId = (EditText)findViewById(R.id.beaconPublicId);
        rePark = (Button)findViewById(R.id.rePark);
        bcnVerify = (ImageView)findViewById(R.id.bcnVerify);
        vinVerify = (ImageView)findViewById(R.id.vinVerify);


        //text watcher for beacon public id
        TextWatcher bcnPid = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkBeaconApi();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        beaconPublicId.addTextChangedListener(bcnPid);
        //text watcher for car vin
        TextWatcher vin= new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkCarApi();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        carVin.addTextChangedListener(vin);
        //re_park button on click
      /*  rePark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((carVin.getText().toString().isEmpty())&&(beaconPublicId.getText().toString().isEmpty())){
                    Toast.makeText(RePark.this, "VIN and Beacon cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if(beaconPublicId.getText().toString().isEmpty()){
                    Toast.makeText(RePark.this, "Please enter Beacon number", Toast.LENGTH_SHORT).show();
                }else if(carVin.getText().toString().isEmpty()){
                    Toast.makeText(RePark.this, "Please enter VIN", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Thread thread1 =  new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Log.e("Assign thread","Upload to database");
                            if(flag1 && flag2){
                                reParkApi();
                            }
                        }
                    });
                    thread1.start();

                    //Volley POST to HTTP


                }


            }
        });
*/

    }
        //to check if the beacon is present in the database
        private void checkBeaconApi() {
            String Url = "http://ec2-18-216-80-229.us-east-2.compute.amazonaws.com:3000/becon/verifyBecon";

            StringRequest rq = new StringRequest(Request.Method.POST, Url , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.d("Response Text", response);
                    try {
                        JSONObject obj = new JSONObject(response);

                        if (obj.getInt("Code")==1) {
                            JSONArray document = obj.getJSONArray("Document");
                            Log.e("Response", "1");
                            flag1 = true;
                            bcnVerify.setImageDrawable(getDrawable(R.drawable.greentick));
                            bcnVerify.setVisibility(View.VISIBLE);
                            Log.e("Inside","checkBeaconAPI");
                            for (int i = 0; i < document.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject Object = document.getJSONObject(i);
                                beaconID = Object.getInt("BeconID");
                                Log.e("BeaconID",""+beaconID);

                            }


                        } else if(obj.getInt("Code")==0) {
                            Log.e("Response","0");
                            flag1 = false;
                            bcnVerify.setVisibility(View.INVISIBLE);

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
                    params.put("BeconPublicID", beaconPublicId.getText().toString());
                    Log.d("BeconPublicID", beaconPublicId.getText().toString());

                    return params;
                }
            };
            Singleton.getInstance(getApplicationContext()).addToRequestQueue(rq);
        }
        //to check if the car is present in the database
        private void checkCarApi() {
            String Url = "http://ec2-18-216-80-229.us-east-2.compute.amazonaws.com:3000/car/verifyCar";

            StringRequest rq = new StringRequest(Request.Method.POST, Url , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.d("Response Text", response);
                    try {
                        JSONObject obj = new JSONObject(response);

                        if (obj.getInt("Code")==1) {
                            JSONArray document = obj.getJSONArray("Document");
                            Log.e("Response", "1");
                            Log.e("Inside","checkCar");
                            flag2 =true;
                            vinVerify.setImageDrawable(getDrawable(R.drawable.greentick));
                            vinVerify.setVisibility(View.VISIBLE);
                            for (int i = 0; i < document.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject Object = document.getJSONObject(i);
                                carID = Object.getInt("CarID");
                                Log.e("CarID",""+carID);

                            }


                        } else if(obj.getInt("Code")==0) {
                            flag2 =false;
                            Log.e("Response","0");
                            vinVerify.setVisibility(View.INVISIBLE);
                            // Toast.makeText(ScanQR.this, "Vehicle is not registered", Toast.LENGTH_SHORT).show();
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
                    params.put("CarVIN", carVin.getText().toString());
                    Log.d("CarVIN", carVin.getText().toString());

                    return params;
                }
            };
            Singleton.getInstance(getApplicationContext()).addToRequestQueue(rq);
        }
        //to repark an already parked car
        private void reParkApi() {
        String zone;
        String Url = "http://ec2-18-216-80-229.us-east-2.compute.amazonaws.com:3000/becon_car_map/add";

        StringRequest rq = new StringRequest(Request.Method.POST, Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response Text", response);
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getInt("Code")==1) {
                      startActivity(new Intent(getApplicationContext(),Parking.class));

                    } else if(obj.getInt("Code")==0) {
                        flag1 =false;

                        Log.e("Response","0");
                        Toast.makeText(RePark.this, "Vehicle already assigned", Toast.LENGTH_SHORT).show();
                    }
                    else if(obj.getInt("Code")==2) {
                        flag1 =false;

                        Log.e("Response","0");
                        Toast.makeText(RePark.this, "Beacon already assigned", Toast.LENGTH_SHORT).show();
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
                params.put("CarID", ""+carID );
                params.put("BeconID", ""+beaconID);
                // params.put("MappingUpdatedBy", ""+1);
                params.put("MappingCreatedBy", ""+1);
                Log.e("CarID", ""+carID );
                Log.e("BeconID", ""+beaconID);

                return params;
            }
        };
        Singleton.getInstance(getApplicationContext()).addToRequestQueue(rq);
    }


}
