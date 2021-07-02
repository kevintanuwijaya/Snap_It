package kevin.com.snapit.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import kevin.com.snapit.Adapter.HomeIconAdapter;
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

    private RecyclerView recycler_site;
    private ArrayList<Icon> iconList = new ArrayList<Icon>();



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
        init();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,null);
        recycler_site = view.findViewById(R.id.recycle_site);
        Log.d("RECYCLE","MASUK");
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

    }

    @Override
    public void onResume() {
        super.onResume();

        recycler_site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void init(){
        int[] images = {R.drawable.restaurant,R.drawable.petrol,R.drawable.onlineshopping,R.drawable.museum,R.drawable.hotel,R.drawable.hospital,R.drawable.cinema,R.drawable.bank,R.drawable.amusementpark};
        String[] iconName = getResources().getStringArray(R.array.icon);

        for(int i=0 ; i<images.length ; i++){
            Icon icon = new Icon(images[i],iconName[i]);
            iconList.add(icon);
        }

    }
}