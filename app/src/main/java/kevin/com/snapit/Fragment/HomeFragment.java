package kevin.com.snapit.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.VideoOperator;
import com.huawei.hms.ads.nativead.DislikeAdListener;
import com.huawei.hms.ads.nativead.MediaView;
import com.huawei.hms.ads.nativead.NativeAd;
import com.huawei.hms.ads.nativead.NativeAdConfiguration;
import com.huawei.hms.ads.nativead.NativeAdLoader;
import com.huawei.hms.ads.nativead.NativeView;

import java.util.ArrayList;

import kevin.com.snapit.Adapter.ArticelAdapter;
import kevin.com.snapit.Adapter.HomeIconAdapter;
import kevin.com.snapit.Model.Articel;
import kevin.com.snapit.Model.Icon;
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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recycler_site,recycler_articel;
    private ArrayList<Icon> iconList = new ArrayList<Icon>();
    private ArrayList<Articel> articelList = new ArrayList<Articel>();

    private NativeAd globalNativeAd;
    private ScrollView scrollView;




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
        //loadAd(String.valueOf(R.string.ad_id_native));
        init();
        Log.d("HOME","SINI");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,null);
        recycler_site = view.findViewById(R.id.recycle_site);
        recycler_articel = view.findViewById(R.id.recycle_articel);
        scrollView = view.findViewById(R.id.scroll_layout);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        LinearLayoutManager iconLayout = new LinearLayoutManager(getActivity());
        iconLayout.setOrientation(LinearLayoutManager.HORIZONTAL);

        HomeIconAdapter iconAdapter = new HomeIconAdapter(getActivity(),iconList);
        recycler_site.setAdapter(iconAdapter);
        recycler_site.setLayoutManager(iconLayout);

        LinearLayoutManager articelLayout = new LinearLayoutManager(getActivity());
        articelLayout.setOrientation(RecyclerView.HORIZONTAL);

        ArticelAdapter articelAdapter = new ArticelAdapter(getActivity(),articelList);
        recycler_articel.setAdapter(articelAdapter);
        recycler_articel.setLayoutManager(articelLayout);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void init(){
        int[] iconImages = {R.drawable.restaurant,R.drawable.petrol,R.drawable.onlineshopping,R.drawable.museum,R.drawable.hotel,R.drawable.hospital,R.drawable.cinema,R.drawable.bank,R.drawable.amusementpark};
        String[] iconName = getResources().getStringArray(R.array.icon);

        for(int i=0 ; i<iconImages.length ; i++){
            Icon icon = new Icon(iconImages[i],iconName[i]);
            iconList.add(icon);
        }

        int[] articelImages = {R.drawable.articel1,R.drawable.articel2};
        String[] articelTitle = getResources().getStringArray(R.array.articelTitle);
        String[] articelContain = getResources().getStringArray(R.array.articelContain);
        String[] articelAuthor = getResources().getStringArray(R.array.articelAuthor);
        Log.d("RECYCLE","MASUK");


        for(int  j=0 ; j<articelImages.length ; j++){
            Articel articel = new Articel(articelImages[j],articelContain[j],articelTitle[j],articelAuthor[j]);
            articelList.add(articel);
        }
    }

    private void loadAd(String adId) {
        Log.d("HOME","Load ads");
        NativeAdLoader.Builder builder = new NativeAdLoader.Builder(getContext(), adId);

        builder.setNativeAdLoadedListener(new NativeAd.NativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                Log.d("HOME","Loading ads");
                // Display a native ad.
                showNativeAd(nativeAd);

                nativeAd.setDislikeAdListener(new DislikeAdListener() {
                    @Override
                    public void onAdDisliked() {
                        // Called when an ad is closed.
                        globalNativeAd.destroy();
                    }
                });
            }
        }).setAdListener(new AdListener() {
            @Override
            public void onAdFailed(int errorCode) {
                Log.d("HOME","Error: "+errorCode);
            }
        });

        NativeAdConfiguration adConfiguration = new NativeAdConfiguration.Builder()
                // Set custom attributes.
                .setChoicesPosition(NativeAdConfiguration.ChoicesPosition.BOTTOM_RIGHT)
                .build();

        NativeAdLoader nativeAdLoader = builder.setNativeAdOptions(adConfiguration).build();

        nativeAdLoader.loadAd(new AdParam.Builder().build());
    }

    private void showNativeAd(NativeAd nativeAd) {
        // Destroy the original native ad.
        if (null != globalNativeAd) {
            globalNativeAd.destroy();
        }
        globalNativeAd = nativeAd;

        // Create NativeView.
        NativeView nativeView = (NativeView) getLayoutInflater().inflate(R.layout.native_video_template, null);
        Log.d("HOME","Showing add");
        // Populate NativeView.
        initNativeAdView(globalNativeAd, nativeView);

        // Add NativeView to the app UI.
        scrollView.addView(nativeView);
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

}