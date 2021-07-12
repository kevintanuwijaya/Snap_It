package kevin.com.snapit.Fragment;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.nativead.MediaView;
import com.huawei.hms.ads.nativead.NativeAd;
import com.huawei.hms.ads.nativead.NativeAdLoader;
import com.huawei.hms.ads.nativead.NativeView;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.SupportMapFragment;
import com.huawei.hms.maps.model.Marker;
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
import java.util.List;

import kevin.com.snapit.Adapter.ArticleAdapter;
import kevin.com.snapit.Adapter.HomeIconAdapter;
import kevin.com.snapit.Adapter.RecommendationAdapter;
import kevin.com.snapit.Model.Article;
import kevin.com.snapit.Model.Icon;
import kevin.com.snapit.Model.LoadingDialog;
import kevin.com.snapit.Model.Location;
import kevin.com.snapit.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = HomeFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recycler_site, recycler_articel, recycler_recommendation;
    private ArrayList<Icon> iconList = new ArrayList<Icon>();
    private ArrayList<Article> articelList = new ArrayList<Article>();
    private ArrayList<Location> locationList = new ArrayList<Location>();

    private NativeAd globalNativeAd;
    private ScrollView topScrollView, bottomScrollView;
    private FrameLayout adLayout;

    private SupportMapFragment mSupportMapFragment;
    private HuaweiMap hMap;
    private Marker mMarker;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Coordinate location;

    private SearchService searchService;
    private List<Site> sites = new ArrayList<Site>();


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        HwAds.init(getActivity());
        loadAd(getString(R.string.ad_id_native_small));
        init();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,null);
        recycler_site = view.findViewById(R.id.category_recycle_view);
        recycler_articel = view.findViewById(R.id.article_recycle_view);
        recycler_recommendation = view.findViewById(R.id.recommendation_recycle_view);
        topScrollView = view.findViewById(R.id.top_scroll_layout);
//        bottomScrollView = view.findViewById(R.id.bottom_scroll_layout);
        adLayout = view.findViewById(R.id.ad_frame_layout);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        LinearLayoutManager iconLayout = new LinearLayoutManager(getActivity());
        iconLayout.setOrientation(LinearLayoutManager.HORIZONTAL);

        HomeIconAdapter iconAdapter = new HomeIconAdapter(getActivity(), iconList);
        recycler_site.setAdapter(iconAdapter);
        recycler_site.setLayoutManager(iconLayout);

        LinearLayoutManager articelLayout = new LinearLayoutManager(getActivity());
        articelLayout.setOrientation(RecyclerView.HORIZONTAL);

        ArticleAdapter articelAdapter = new ArticleAdapter(getActivity(), articelList);
        recycler_articel.setAdapter(articelAdapter);
        recycler_articel.setLayoutManager(articelLayout);

        //TODO pikirin cara dapet si list nya dulu, terus kalo udah nanti baru dimasukin
        LinearLayoutManager recommendationLayout = new LinearLayoutManager(getActivity());
        recommendationLayout.setOrientation(RecyclerView.HORIZONTAL);

        RecommendationAdapter recommendationAdapter = new RecommendationAdapter(getActivity(), locationList);
        recycler_recommendation.setAdapter(recommendationAdapter);
        recycler_recommendation.setLayoutManager(recommendationLayout);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void init(){
        LoadingDialog loadingDialog = new LoadingDialog(getContext());
        loadingDialog.startDialog();

        // For Category
        int[] iconImages = {R.drawable.restaurant, R.drawable.petrol, R.drawable.onlineshopping,
                            R.drawable.museum, R.drawable.hotel, R.drawable.hospital,
                            R.drawable.cinema, R.drawable.bank, R.drawable.amusementpark};
        String[] iconName = getResources().getStringArray(R.array.icon);

        for(int index = 0; index < iconImages.length; index++){
            Icon icon = new Icon(iconImages[index], iconName[index]);
            iconList.add(icon);
        }

        // For Article
        int[] articleImage = {R.drawable.articel1, R.drawable.articel2};
        String[] articleTitle = getResources().getStringArray(R.array.articelTitle);
        String[] articelContain = getResources().getStringArray(R.array.articelContain);
        String[] articleAuthor = getResources().getStringArray(R.array.articelAuthor);
        Log.d("RECYCLE","MASUK");

        for(int index = 0; index < articleImage.length; index++){
            Article articel = new Article(articleImage[index],articelContain[index],articleTitle[index],articleAuthor[index]);
            articelList.add(articel);
        }

        //TODO bikin icon nya supaya bener
        // For Recommendation
        HwLocationType[] PoiLocationType = { HwLocationType.RESTAURANT, HwLocationType.PETROL_STATION, HwLocationType.SHOP,
                                                HwLocationType.MUSEUM, HwLocationType.HOTEL, HwLocationType.GENERAL_HOSPITAL,
                                                HwLocationType.CINEMA, HwLocationType.BANK,  HwLocationType.AMUSEMENT_PARK };

        dynamicPermission();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        getCurrentLocation();

        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    for(int index = 0; index < iconImages.length; index++) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                            }
                        }, 3000);
                        searchNearby(iconName[index], PoiLocationType[index]);
                    }
                }
            }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for(int index = 0; index < sites.size(); index++){
                    Location location = new Location(iconImages[index], sites.get(index).getName(), sites.get(index).getAddress().toString(),
                            sites.get(index).getAddress().getPostalCode(), sites.get(index).getAddress().getCountry(),
                            sites.get(index).getPoi().getPhone(), sites.get(index).getPoi().getWebsiteUrl(),
                            sites.get(index).getLocation().getLat(), sites.get(index).getLocation().getLng(),
                            sites.get(index).getDistance(), sites.get(index).getPoi().getRating());
                    Log.d(TAG, "run add location: " + location.getName());
                    locationList.add(location);
                }
            }
        }, 3000);

        loadingDialog.dismissDialog();
    }

    private void loadAd(String adId) {
        Log.d(TAG, adId);

        NativeAdLoader.Builder builder = new NativeAdLoader.Builder(getContext(), adId);

        builder.setNativeAdLoadedListener(nativeAd -> {
            Log.d(TAG,"Ads Loaded");
            // Display a native ad.
            showNativeAd(nativeAd);

            nativeAd.setDislikeAdListener(() -> {
                // Called when an ad is closed.
                globalNativeAd.destroy();
            });
        }).setAdListener(new AdListener() {
            @Override
            public void onAdFailed(int errorCode) {
                Log.d(TAG,"Error: "+ errorCode);
            }
        });

        NativeAdLoader nativeAdLoader = builder.build();

        nativeAdLoader.loadAd(new AdParam.Builder().build());
    }

    private void showNativeAd(NativeAd nativeAd) {
        // Destroy the original native ad.
        if (null != globalNativeAd) {
            globalNativeAd.destroy();
        }
        globalNativeAd = nativeAd;

        // Create NativeView.
        NativeView nativeView = (NativeView) getLayoutInflater().inflate(R.layout.native_small_template, null);
        Log.d(TAG,"Showing add");
        // Populate NativeView.
        initNativeAdView(globalNativeAd, nativeView);

        // Add NativeView to the app UI.
        adLayout.addView(nativeView);
    }

    private void initNativeAdView(NativeAd nativeAd, NativeView nativeView) {
        // Register a native ad asset view.
        nativeView.setTitleView(nativeView.findViewById(R.id.ad_title));
        nativeView.setMediaView((MediaView) nativeView.findViewById(R.id.ad_media));
        nativeView.setAdSourceView(nativeView.findViewById(R.id.ad_source));
        nativeView.setCallToActionView(nativeView.findViewById(R.id.ad_call_to_action));

        // Populate the native ad asset view. The native ad must contain the title and media assets.
        ((TextView) nativeView.getTitleView()).setText(nativeAd.getTitle());
        nativeView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        if (null != nativeAd.getAdSource()) {
            ((TextView) nativeView.getAdSourceView()).setText(nativeAd.getAdSource());
        }
        nativeView.getAdSourceView().setVisibility(null != nativeAd.getAdSource() ? View.VISIBLE : View.INVISIBLE);

        if (null != nativeAd.getCallToAction()) {
            ((Button) nativeView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        nativeView.getCallToActionView().setVisibility(null != nativeAd.getCallToAction() ? View.VISIBLE : View.INVISIBLE);


        // Register a native ad object.
        nativeView.setNativeAd(nativeAd);
    }

    private void getCurrentLocation() {
        mLocationRequest = new LocationRequest();
        SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
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
                    location = new Coordinate(latitude, longitude);
                    Log.d(TAG, "onLocationResult: " + location.getLat() + " " + location.getLng());
//                    currPosition = new LatLng(latitude, longitude);
                    stopTracking();
                }
            }
        };
        // Check the device location settings.
        settingsClient.checkLocationSettings(locationSettingsRequest)
                // Define callback for success in checking the device location settings.
                .addOnSuccessListener(locationSettingsResponse -> {
                    // Initiate location requests when the location settings meet the requirements.
                    fusedLocationProviderClient
                            .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                            // Define callback for success in requesting location updates.
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Getting your location"));
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
                                    rae.startResolutionForResult(getActivity(), 0);
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

    private void searchNearby(String query, HwLocationType PoiType) {
        // Instantiate the SearchService object.
        searchService = SearchServiceFactory.create(getActivity(), getApi());
        // Create a request body.
        NearbySearchRequest request = new NearbySearchRequest();
        Log.d(TAG, "searchNearby: " + query);
        Log.d(TAG, "searchNearby: " + PoiType);
        request.setLocation(location);
        request.setQuery(query);
        request.setRadius(10);
        request.setHwPoiType(PoiType);
        request.setLanguage("en");
        request.setPageIndex(1);
        request.setPageSize(1); // Buat nentuin berapa banyak result
        request.setStrictBounds(false);
        SearchResultListener<NearbySearchResponse> resultListener = new SearchResultListener<NearbySearchResponse>() {
            // Return search results upon a successful search.
            @Override
            public void onSearchResult(NearbySearchResponse results) {
                Log.d(TAG, "Getting your Site");
                if (results == null || results.getTotalCount() <= 0) {
                    return;
                }
                sites.add(results.getSites().get(0));
                Log.d(TAG, "onSearchResult: site added " + sites.size());
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

    private void dynamicPermission() {
        // Dynamically apply for required permissions if the API level is 28 or smaller.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "android sdk <= 28 Q");
            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] strings =
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(getActivity(), strings, 1);
            }
        } else {
            // Dynamically apply for required permissions if the API level is greater than 28. The android.permission.ACCESS_BACKGROUND_LOCATION permission is required.
            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(),
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION"};
                ActivityCompat.requestPermissions(getActivity(), strings, 2);
            }
        }
    }

    private String getApi() {
        String API = "CgB6e3x9a4NNICDnGnFCV8+aBktmeoZWbiCIcGNQgzzmkzM2oPozCF5/YlX0DsOMAdd+6rsKevlDLTYy5ROFchTz";

        try {
            return URLEncoder.encode(API, "utf-8");
        } catch (Exception e) {
            return null;
        }
    }

}