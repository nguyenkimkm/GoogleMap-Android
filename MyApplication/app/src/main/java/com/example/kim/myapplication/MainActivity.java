package com.example.kim.myapplication;


import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kim.myapplication.api.ConstLinkApi;
import com.example.kim.myapplication.foursquare.JSonParserFoursquare;
import com.example.kim.myapplication.foursquare.ObjectLatLng;
import com.example.kim.myapplication.foursquare.ObjectLocation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;

import org.json.JSONException;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements LocationListener {

    private ArrayList<ObjectLocation> arrLocation = new ArrayList<ObjectLocation>();
    private JSonParserFoursquare JSonFoursquare;
    private ConstLinkApi linkFoursquareApi;
    private GoogleMap mMap;
    private ListView listlocation;
    private LocationAdapter adapter;
    private String[] nameVenues;
    private LocationManager locationManager;
    private String provider;
    private boolean enabledGPS;
    private View infoWindow;
    private myLocationListener mLocationListener;
    private LocationManager service;
    private ImageView bgBack;
    private ImageView enabledLV;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        checkWifiAndGPS();

        infoWindow = getLayoutInflater().inflate(R.layout.info_window_layout,null);
        linkFoursquareApi = new ConstLinkApi();
        mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(false);
        mMap.setInfoWindowAdapter(new CustomInfoAdapter());
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria,false);
        Location location = locationManager.getLastKnownLocation(provider);
        mLocationListener = new myLocationListener();

        if (location == null && isOnline()){
            Location location_wifi = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            mLocationListener.onLocationChanged(location_wifi);
        } else if (location == null && enabledGPS){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10, this);
            Location location_gps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location_gps != null){
                focusMyLocation(location_gps);
            } else{
                Toast.makeText(getBaseContext(), "khong co Internet hoac chua bat GPS", Toast.LENGTH_SHORT).show();
            }
        }

        enabledLV = (ImageView)findViewById(R.id.imgSearch);
        enabledLV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullScreenMap(1);
            }
        });

        bgBack = (ImageView)findViewById(R.id.imgBack);
        bgBack.setVisibility(View.VISIBLE);
        bgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullScreenMap(0);
            }
        });

    }

    public  void checkWifiAndGPS(){

        if (isOnline()){
            Toast.makeText(getApplicationContext(), "CO MANG", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "KHONG CO MANG", Toast.LENGTH_SHORT).show();
        }
        service = (LocationManager)getSystemService(LOCATION_SERVICE);
        enabledGPS = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabledGPS){
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, RESULT_CANCELED);
            Toast.makeText(getApplicationContext(), "KHONG CO GPS", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "CO GPS", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()){
            return true;
        }
        return  false;
    }

    @Override
    protected void onActivityResult(int arg0, int result, Intent arg2) {
        // TODO Auto-generated method stub
        super.onActivityResult(arg0, result, arg2);
        if (result == RESULT_CANCELED) {
            if (!enabledGPS) {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.gps),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void focusMyLocation(Location location){
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(userLocation.latitude, userLocation.longitude));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
        mMap.addMarker(new MarkerOptions()
                        .position(userLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation))
                        .snippet("address").title("My location"));

        getDataFromMyLocation(userLocation);
    }

    public void getDataFromMyLocation(LatLng userLocation){
        JSonFoursquare = new JSonParserFoursquare();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(linkFoursquareApi.getApiLocation(new ObjectLatLng(userLocation.latitude, userLocation.longitude)),
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            arrLocation = JSonFoursquare.getDataFourSquare(response);
                            nameVenues = new String[50];
                            for (int i = 0; i < arrLocation.size(); i++) {
                                nameVenues[i] = arrLocation.get(i).getNameLocation();
                            }
                            fillDataToListView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void sendFailureMessage(Throwable arg0, String arg1) {
                        super.sendFailureMessage(arg0, arg1);
                        Log.e("VCC", "sendFailureMessage(arg0, arg1)" + arg1);
                    }
                });
    }

    public int getIdDKM(String currentName){
        int id = 0;
        for (int i = 0; i < nameVenues.length; i++){
            if (nameVenues[i].equalsIgnoreCase(currentName)){
                id = i;
            }
        }
        return id;
    }

    public  void fillDataToListView(){
        listlocation = (ListView)findViewById(R.id.listViewLocation);
        if (arrLocation.size() != 0){
            adapter = new LocationAdapter(getApplicationContext(),R.layout.items_location, arrLocation);
            listlocation.setAdapter(adapter);
        }
    }


    public class LocationAdapter extends ArrayAdapter<ObjectLocation>{
        Context context;
        int layoutResourceId;
        ArrayList<ObjectLocation> objects = null;

        public LocationAdapter(Context context, int layoutResourceId, ArrayList<ObjectLocation> objects){
            super(context, layoutResourceId, objects);
            this.context = context;
            this.layoutResourceId = layoutResourceId;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            View row = convertView;
            LocationHolder objLoc = null;

            if (row == null){
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                objLoc = new LocationHolder();
                objLoc.txtName = (TextView)row.findViewById(R.id.txtName);
                objLoc.txtAddress = (TextView)row.findViewById(R.id.txtAddress);
                objLoc.txtDistance = (TextView)row.findViewById(R.id.txtDistance);
                row.setTag(objLoc);
            } else {
                objLoc = (LocationHolder)row.getTag();
            }

            ObjectLocation obj = objects.get(position);
            objLoc.txtName.setText(obj.getNameLocation());
            objLoc.txtAddress.setText(obj.getAddVenues());
            objLoc.txtDistance.setText("Khoảng cách: " + obj.getDistance() + " m");
            row.setId(position);
            row.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    bgBack.setVisibility(View.VISIBLE);
                    return true;
                }
            });

            return row;
        }
        class LocationHolder{
            TextView txtName;
            TextView txtAddress;
            TextView txtDistance;
        }
    }

    public void fullScreenMap(int i){
        LinearLayout mapLinear = (LinearLayout)findViewById(R.id.linearMap);
        if (i == 0){
            LinearLayout.LayoutParams mapFrame = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            mapLinear.setLayoutParams(mapFrame);
            if (arrLocation.size() != 0){
                addMarkertoMap(arrLocation, mMap, 8);
            }
        } else {
            LinearLayout.LayoutParams mapFrame = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 600);
            mapLinear.setLayoutParams(mapFrame);

        }
    }

    private void addMarkertoMap(ArrayList<ObjectLocation> arrayLoc, GoogleMap mapGoogle, int limit){
        for(int i = 0; i < limit; i++){
            LatLng MakerPos = new LatLng(arrayLoc.get(i).getmObjLatLng().getmLat(), arrayLoc.get(i).getmObjLatLng().getmLng());
            Marker makerCustom = mapGoogle.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_map_1))
                            .position(MakerPos)
                            .snippet(arrayLoc.get(i).getAddVenues())
                            .title(arrayLoc.get(i).getNameLocation())
            );
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        locationManager.removeUpdates(mLocationListener);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        locationManager.requestLocationUpdates(provider, 500, 50, mLocationListener);
    }

     public class myLocationListener implements LocationListener{

         @Override
         public void onLocationChanged(Location location) {
             if (location != null)
                 focusMyLocation(location);
             else
                 Toast.makeText(getBaseContext(), "ban chua bat GPS hoac khong co INTERNET",Toast.LENGTH_SHORT).show();
         }

         @Override
         public void onStatusChanged(String provider, int status, Bundle extras) {

         }

         @Override
         public void onProviderEnabled(String provider) {

         }

         @Override
         public void onProviderDisabled(String provider) {

         }
     }

    public void displayView(Marker mark){
        ((TextView)infoWindow.findViewById(R.id.nameVenues)).setText(mark.getTitle());
        UrlImageViewHelper.setUrlDrawable(((ImageView) infoWindow
                        .findViewById(R.id.avatar)),
                        arrLocation.get(getIdDKM(mark.getTitle())).getLinkIcon(),
                        R.drawable.loading);
        ((TextView) infoWindow.findViewById(R.id.addressVenues)).setText(mark
                .getSnippet());
    }

    class CustomInfoAdapter implements InfoWindowAdapter {

        @Override
        public View getInfoContents(Marker mark) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public View getInfoWindow(Marker mark) {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), mark.getId(),
                    Toast.LENGTH_SHORT).show();
            displayView(mark);
            return infoWindow;
        }
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
