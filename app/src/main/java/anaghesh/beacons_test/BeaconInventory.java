package anaghesh.beacons_test;


import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeaconInventory extends AppCompatActivity {
    private static final String TAG = BeaconInventory.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<BeaconData> beaconList;
    private BeaconAdapter beaconAdapter;
    private SearchView searchView;
    String BeaconPublicId,MacId,UUID, Battery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_inventory);
        Toolbar toolbar = findViewById(R.id.btoolbar);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Beacon Inventory");

        recyclerView = findViewById(R.id.mybRecyclerView);


        // transparent background notification bar
        NotificationBar(recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        beaconList = new ArrayList<>();
        getBeaconDetailsApi();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_collapse, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                beaconAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                beaconAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void NotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void getBeaconDetailsApi(){
        String Url = "http://ec2-18-216-80-229.us-east-2.compute.amazonaws.com:3000/becon/latestBecon";

        StringRequest rq = new StringRequest(Request.Method.GET, Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response Text", response);
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getInt("Code")==1) {
                        JSONArray document = obj.getJSONArray("Document");
                        Log.e("Response", "1");
                        Log.e("Inside","checkBeacon");

                        for (int i = 0; i < document.length(); i++) {
                            //getting the json object of the particular index inside the array
                            JSONObject Object = document.getJSONObject(i);
                            BeaconPublicId = Object.getString("BeconPublicID");
                            MacId=Object.getString("BeconMacID");
                            UUID=Object.getString("BeconUUID");
                            Battery=Object.getString("BeconBatteryLife");
                            if(Battery==null) Battery = "0";
                            Log.e("Beacon Details",""+BeaconPublicId+"\n"+MacId+"\n"+UUID+"\n"+Battery);
                            beaconList.add(new BeaconData("Beacon Public ID: " + BeaconPublicId,"Beacon Mac Id: "+MacId,"Beacon UUID: "+UUID,"Battery Life:"+Battery+"%"));

                        }
                        beaconAdapter = new BeaconAdapter(getApplicationContext(), beaconList);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(beaconAdapter);


                    } else if(obj.getInt("Code")==0) {
                        Log.e("Response","0");
                        Toast.makeText(BeaconInventory.this, "Beacon is not registered", Toast.LENGTH_SHORT).show();
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

        });
        Singleton.getInstance(getApplicationContext()).addToRequestQueue(rq);
    }

}