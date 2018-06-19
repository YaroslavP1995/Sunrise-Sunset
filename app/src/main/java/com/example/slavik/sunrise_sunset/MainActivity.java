package com.example.slavik.sunrise_sunset;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends Activity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private static final int REQUEST_LOCATION = 1;

    private TextView textViewResultLocation;
    private TextView textViewResultSun;

    private Button showYourResultLoc;
    private Button showOtherResultLoc;

    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MyResponse";


    private static final String ROOT_URL = "https://api.sunrise-sunset.org";
    private SunriseSunsetApi sunriseSunsetApi;
    private Retrofit retrofit;

    private LatLng latLng;
    private double lat;
    private double lng;
    private String places;
    Dialog dialog ;
    PlaceAutocompleteFragment autoCompleteFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(MainActivity.this, "Turn on geolocation on your smartphone.", Toast.LENGTH_LONG).show();

        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.activity_search);

        textViewResultLocation = (TextView) findViewById(R.id.textView_result_location);
        textViewResultSun = (TextView) findViewById(R.id.textView_reusult_sun);

        showOtherResultLoc = (Button) findViewById(R.id.call_other_result);
        showYourResultLoc = (Button) findViewById(R.id.call_your_result);

        autoCompleteFragment = (PlaceAutocompleteFragment) getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);

        retrofit = new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        showYourResultLoc.setOnClickListener(this);
        showOtherResultLoc.setOnClickListener(this);

        sunriseSunsetApi = retrofit.create(SunriseSunsetApi.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_other_result:
                dialog.show();
                showOtherPlace(textViewResultLocation);
                break;
            case R.id.call_your_result:
                onPlaceLikelihood(textViewResultLocation);
                break;
        }
    }

    public void showResult(double lat,double lng){

            sunriseSunsetApi.getData(lat, lng, 0).enqueue(new Callback<Results>() {
                @Override
                public void onResponse(Call<Results> call, Response<Results> response) {
                    Log.i(TAG, "onResponse is on");

                    textViewResultSun.setText(response.body().getResults().getSunrise()+
                            " Sunrise"+"\n"+response.body().getResults().getSunset()+" Sunset" );
                }
                @Override
                public void onFailure(Call<Results> call, Throwable t) {
                    Log.i(TAG, "onResponse of");
                }
            });
    }
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "onConnectionSuspended", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "onConnectionFailed", Toast.LENGTH_LONG).show();
    }

    public void onPlaceLikelihood(View view){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }else
        if (mGoogleApiClient != null){

            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient,null);

            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {
                    if (placeLikelihoods.getCount() <= 0){
                         Toast.makeText(MainActivity.this, "Turn on geolocation on your smartphone and try again.", Toast.LENGTH_LONG).show();
                    }
                    for (PlaceLikelihood placeLikelihood: placeLikelihoods){
                        places = placeLikelihood.getPlace().getName()+ "\n"
                                +placeLikelihood.getPlace().getAddress().toString()+ "\n";
                        latLng = placeLikelihood.getPlace().getLatLng();

                        lat =  latLng.latitude;
                        lng =  latLng.longitude;
                        textViewResultLocation.setText(places+"\n"+ latLng);
                        showResult(lat,lng);

                    }
                    placeLikelihoods.release();
                }
            });
        }else {
            Toast.makeText(MainActivity.this, "No GoogleApiClient", Toast.LENGTH_SHORT).show();

        }
    }
    public void showOtherPlace(View view){
        autoCompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {

                String placeDetailsStr = place.getName() + "\n"
                        + place.getAddress() + "\n"
                        + place.getAttributions() + "\n";
                latLng = place.getLatLng();

                textViewResultLocation.setText(placeDetailsStr + "\n" + latLng);

                lat = latLng.latitude;
                lng = latLng.longitude;
                showResult(lat,lng);

            }
            @Override
            public void onError(Status status) {
                String st = status.getStatusMessage();
                Toast.makeText(MainActivity.this, st, Toast.LENGTH_SHORT).show();

            }
        });
    }

}
