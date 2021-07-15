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
import android.widget.ImageView;
import android.widget.TextView;
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
import com.huawei.hms.maps.MapsInitializer;
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
import java.util.HashMap;
import java.util.List;

import kevin.com.snapit.Model.LoadingDialog;


public class CategoryOnMapActivity extends AppCompatActivity implements OnMapReadyCallback, HuaweiMap.OnMarkerClickListener {

    private static final String TAG = CategoryOnMapActivity.class.getSimpleName();
    private final int SPLASH_SCREEN = 5000;

    private SupportMapFragment mSupportMapFragment;
    private HuaweiMap hMap;
    private Marker mMarker;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private LatLng currPosition;

    private SearchService searchService;
    private List<Site> sites = new ArrayList<Site>();

//    private BaseSearchResponse<List<ImageItem>> response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.setApiKey("CgB6e3x9a4NNICDnGnFCV8+aBktmeoZWbiCIcGNQgzzmkzM2oPozCF5/YlX0DsOMAdd+6rsKevlDLTYy5ROFchTz");
        setContentView(R.layout.activity_category_on_map);

        Log.d(TAG, "onCreate: SUCCESS");
        dynamicPermission();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();

        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mSupportMapFragment.getMapAsync(this);

        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startDialog();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                searchNearby(getIntent().getStringExtra("CATEGORY"));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        moveCameraAndAddMarker(getIntent().getStringExtra("CATEGORY"));
                        loadingDialog.dismissDialog();
                    }
                }, 3000);
            }
        }, 1000);


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
        Log.d(TAG, "searchNearby: " + location.getLat() + " " + location.getLng());
        Log.d(TAG, "searchNearby: " + query);
        Log.d(TAG, "searchNearby: " + getPoiType(query));
        request.setLocation(location);
        request.setQuery(query);
        request.setRadius(10);
        request.setHwPoiType(getPoiType(query));
        request.setLanguage("en");
        request.setPageIndex(1);
        request.setPageSize(10); // Buat nentuin berapa banyak result
        request.setStrictBounds(false);
        SearchResultListener<NearbySearchResponse> resultListener = new SearchResultListener<NearbySearchResponse>() {
            // Return search results upon a successful search.
            @Override
            public void onSearchResult(NearbySearchResponse results) {
                Log.d(TAG, "Getting your Site");
                if (results == null || results.getTotalCount() <= 0) {
                    return;
                }
                sites = results.getSites();
                if (sites == null || sites.size() == 0) {
                    return;
                }
                for (Site site : sites) {
                    Log.d(TAG, String.format("siteId: '%s', name: %s\r\n", site.getSiteId(), site.getName()));
                }
            }

            // Return the result code and description upon a search exception.
            @Override
            public void onSearchError(SearchStatus status) {
                Log.d(TAG, "Error : " + status.getErrorCode() + " " + status.getErrorMessage());
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
                                        Log.d(TAG, "Getting your location");
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

    private void moveCameraAndAddMarker(String category) {
        LatLng pos = new LatLng(currPosition.latitude, currPosition.longitude);
        Log.d(TAG, "moveCameraAndAddMarker: " + pos);

        for (Site site : sites) {
            Log.d(TAG, "Site Marked " + " " + site.getName());
            MarkerOptions options = new MarkerOptions()
                    .position(new LatLng(site.getLocation().getLat(), site.getLocation().getLng()))
                    .title(site.getName())
                    .snippet(category);
            mMarker = hMap.addMarker(options);
        }

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, 14f);
        hMap.moveCamera(cameraUpdate);
        hMap.setOnMarkerClickListener(this);
    }

    private HwLocationType getPoiType(String key) {
        HwLocationType type = HwLocationType.ADDRESS;

        switch (key) {
            case "Restaurant":
                type = HwLocationType.RESTAURANT;
                break;
            case "Petrol Station":
                type = HwLocationType.PETROL_STATION;
                break;
            case "Shop":
                type = HwLocationType.SHOP;
                break;
            case "Museum":
                type = HwLocationType.MUSEUM;
                break;
            case "Hotel":
                type = HwLocationType.HOTEL;
                break;
            case "Hospital":
                type = HwLocationType.GENERAL_HOSPITAL;
                break;
            case "Cinema":
                type = HwLocationType.CINEMA;
                break;
            case "Bank":
                type = HwLocationType.BANK;
                break;
            case "Amusement Park":
                type = HwLocationType.AMUSEMENT_PARK;
                break;
        }

        return type;
    }

    //TODO method buat munculin image di detail, dipake sementara sampe bisa pake search kit
    private int getTempImage(String key) {
        int result = 0;

        switch (key) {
            case "Restaurant":
                result = R.drawable.restaurant;
                break;
            case "Petrol Station":
                result = R.drawable.petrol;
                break;
            case "Shop":
                result = R.drawable.onlineshopping;
                break;
            case "Museum":
                result = R.drawable.museum;
                break;
            case "Hotel":
                result = R.drawable.hotel;
                break;
            case "Hospital":
                result = R.drawable.hospital;
                break;
            case "Cinema":
                result = R.drawable.cinema;
                break;
            case "Bank":
                result = R.drawable.bank;
                break;
            case "Amusement Park":
                result = R.drawable.amusementpark;
                break;
        }

        return result;
    }

    private void dynamicPermission() {
        // Dynamically apply for required permissions if the API level is 28 or smaller.
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

    //TODO method buat pake search kit, tapi gajadi dipake karena time constraint
//    private void imageSearch(String query) {
//        Log.d(TAG, "imageSearch: ");
//        CommonSearchRequest commonSearchRequest = new CommonSearchRequest();
//        // Set the search keyword.
//        commonSearchRequest.setQ(query);
//        // Set the language for search.
//        commonSearchRequest.setLang(Language.ENGLISH);
//        // Set the region for search.
//        commonSearchRequest.setSregion(Region.SINGAPORE);
//        // Set the number of search results returned on a page.
//        commonSearchRequest.setPs(10);
//        // Set the page number.
//        commonSearchRequest.setPn(1);
//
//        SearchKitInstance.getInstance().getImageSearcher().setCredential(TOKEN);
//
//        response = SearchKitInstance.getInstance().getImageSearcher().search(commonSearchRequest);
//    }

    private String getApi() {
        String API = "CgB6e3x9a4NNICDnGnFCV8+aBktmeoZWbiCIcGNQgzzmkzM2oPozCF5/YlX0DsOMAdd+6rsKevlDLTYy5ROFchTz";

        try {
            return URLEncoder.encode(API, "utf-8");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClick: IN");

        TextView textView = (TextView) findViewById(R.id.detail_title);
        textView.setText(marker.getTitle());

        ImageView imageView = (ImageView) findViewById(R.id.detail_image);
        imageView.setImageResource(getTempImage(marker.getSnippet()));

        return false;
    }
    }
