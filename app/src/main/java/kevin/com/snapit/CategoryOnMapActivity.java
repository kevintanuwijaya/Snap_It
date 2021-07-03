package kevin.com.snapit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;
import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.SupportMapFragment;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.HwLocationType;
import com.huawei.hms.site.api.model.NearbySearchRequest;
import com.huawei.hms.site.api.model.NearbySearchResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kevin.com.snapit.Model.Articel;
import kevin.com.snapit.Model.Icon;
import kevin.com.snapit.Model.Location;

public class CategoryOnMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = CategoryOnMapActivity.class.getSimpleName();
    private final int SPLASH_SCREEN = 5000;

    private SupportMapFragment mSupportMapFragment;
    private HuaweiMap hMap;
    private Marker mMarker;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private LatLng currPosition = new LatLng(48.893478,2.334595);

    private SearchService searchService;
    private List<Site> sites = new ArrayList<Site>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_on_map);

        dynamicPermission();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();

//        searchNearby(getIntent().getStringExtra("CATEGORY"));

        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mSupportMapFragment.getMapAsync(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                moveCameraAndAddMarker();
            }
        },SPLASH_SCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void searchNearby(String query) {
        // Instantiate the SearchService object.
        searchService = SearchServiceFactory.create(this, getApi());
        // Create a request body.
        NearbySearchRequest request = new NearbySearchRequest();
        Coordinate location = new Coordinate(currPosition.latitude, currPosition.longitude);
        request.setLocation(location);
        request.setQuery("");
        request.setRadius(5000);
        request.setLanguage("en");
        request.setPageIndex(1);
        request.setPageSize(5);
        request.setStrictBounds(false);
        request.setHwPoiType(HwLocationType.RESTAURANT);
        //TODO masih ke denied request nya
        // Create a search result listener.
        SearchResultListener<NearbySearchResponse> resultListener = new SearchResultListener<NearbySearchResponse>() {
            // Return search results upon a successful search.
            @Override
            public void onSearchResult(NearbySearchResponse results) {
                Log.d(TAG,"Getting your Site");
                if (results == null || results.getTotalCount() <= 0) {
                    return;
                }
                sites = results.getSites();
                if(sites == null || sites.size() == 0){
                    return;
                }
                for (Site site : sites) {
                    Log.i(TAG, String.format("siteId: '%s', name: %s\r\n", site.getSiteId(), site.getName()));
                }
            }
            // Return the result code and description upon a search exception.
            @Override
            public void onSearchError(SearchStatus status) {
                Log.i(TAG, "Error : " + status.getErrorCode() + " " + status.getErrorMessage());
            }
        };
        // Call the nearby place search API.
        searchService.nearbySearch(request, resultListener);
    }

    private void getCurrentLocation() {
        mLocationRequest = new LocationRequest();
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        mLocationRequest = new LocationRequest();
        // Set the location update interval (in milliseconds).
        mLocationRequest.setInterval(10000);
        // Set the location type.
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();
                    // Process the location callback result.
                    currPosition = new LatLng(latitude, longitude);
                    Log.d(TAG, "onLocationResult: " + currPosition);
                    stopTracking();
                }
            }
        };

        // Check the device location settings.
        settingsClient.checkLocationSettings(locationSettingsRequest)
                // Define callback for success in checking the device location settings.
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        // Initiate location requests when the location settings meet the requirements.
                        fusedLocationProviderClient
                                .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                                // Define callback for success in requesting location updates.
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG,"Getting your location");
                                    }
                                });
                    }
                })
                // Define callback for failure in checking the device location settings.
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Device location settings do not meet the requirements.
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    // Call startResolutionForResult to display a pop-up asking the user to enable related permission.
                                    rae.startResolutionForResult(CategoryOnMapActivity.this, 0);
                                } catch (IntentSender.SendIntentException sie) {
                                    // ...
                                }
                                break;
                        }
                    }
                });

        mLocationRequest.setInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void stopTracking() {
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
                // Define callback for success in stopping requesting location updates.
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // ...
                        Log.d(TAG, "Location update removes");
                    }
                })
                // Define callback for failure in stopping requesting location updates.
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // ...
                        Log.d(TAG, "Failed to remove location update");
                    }
                });
    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        hMap = huaweiMap;
        hMap.setMyLocationEnabled(true);
        hMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private void moveCameraAndAddMarker() {
        LatLng pos = new LatLng(currPosition.latitude, currPosition.longitude);
        Log.d(TAG, "moveCameraAndAddMarker: " + pos);

        for(Site site : sites) {
            Log.d(TAG,"Site Marked");
            MarkerOptions options = new MarkerOptions()
                    .position(new LatLng(site.getLocation().getLat(), site.getLocation().getLng()))
                    .title(site.getName())
                    .snippet(site.getFormatAddress() + " " + site.getDistance() + " away from your locaton");
            mMarker =  hMap.addMarker(options);
        }

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, 9f);
        hMap.moveCamera(cameraUpdate);
        searchNearby(getIntent().getStringExtra("CATEGORY"));
    }

    private void dynamicPermission(){
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "android sdk <= 28 Q");
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] strings =
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(this, strings, 1);
            }
        } else {
            // Dynamically apply for required permissions if the API level is greater than 28. The android.permission.ACCESS_BACKGROUND_LOCATION permission is required.
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION"};
                ActivityCompat.requestPermissions(this, strings, 2);
            }
        }
    }

    private String getApi(){
        String API = "CgB6e3x9a4NNICDnGnFCV8+aBktmeoZWbiCIcGNQgzzmkzM2oPozCF5/YlX0DsOMAdd+6rsKevlDLTYy5ROFchTz";

        try {
            String encodeApi = URLEncoder.encode(API,"utf-8");
            return encodeApi;
        }catch (Exception e){
            return null;
        }
    }
}
