package project.fariya_and_srujana.my_places_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap gMap;

    //for current location
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_REQUEST_CODE = 111111;


    //for city search
    private EditText etInputKeyword;
    private Button btnSearch;
    private Geocoder geocoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etInputKeyword = findViewById(R.id.et_input);
        btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fetchLastLocation();


    }


    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null)
                {
                    currentLocation = location;
                    Toast.makeText(MainActivity.this, "Current Location => " + location.getLatitude() + "  " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
                    mapFragment.getMapAsync(MainActivity.this);
                }
                else {
                    Toast.makeText(MainActivity.this, "Failed to get last location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;

        //  17.387140, 78.491684

        LatLng hyderabad = new LatLng(17.387140, 78.491684);

        MarkerOptions hydMarkerOptions = new MarkerOptions();
        hydMarkerOptions.title("Hyderabad");
        hydMarkerOptions.position(hyderabad);
        hydMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        gMap.addMarker(hydMarkerOptions);
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(hyderabad, 16));
        //GUI elements
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.getUiSettings().setCompassEnabled(true);
        gMap.getUiSettings().setAllGesturesEnabled(true);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        gMap.setMyLocationEnabled(true);


        // current Location Marker
        LatLng currentLocationLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions currentLocationMarkerOptions = new MarkerOptions();
        currentLocationMarkerOptions.title("Current Location");
        currentLocationMarkerOptions.position(currentLocationLatLng);
        gMap.addMarker(currentLocationMarkerOptions);
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatLng, 14));




    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_search:
            {
                searchLocation();
            }
        }
    }

    private void searchLocation() {

        List<Address> addressList = null;

        String locationKeyword = etInputKeyword.getText().toString().trim();

        if (locationKeyword.isEmpty())
        {
            return;
        }

        geocoder = new Geocoder(MainActivity.this);
        try {
            addressList = geocoder.getFromLocationName(locationKeyword, 1);
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();;
        }


        LatLng searchedPlaceLatlng = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());

        MarkerOptions searchedPlaceMarker = new MarkerOptions();
        searchedPlaceMarker.title(locationKeyword);
        searchedPlaceMarker.position(searchedPlaceLatlng);
        gMap.clear();
        gMap.addMarker(searchedPlaceMarker);
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchedPlaceLatlng, 12));



    }
}