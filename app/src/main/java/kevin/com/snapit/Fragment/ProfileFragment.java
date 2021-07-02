package kevin.com.snapit.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import kevin.com.snapit.Adapter.SettingAdapter;
import kevin.com.snapit.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements AdapterView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView nameLbl, emailLbl;
    private ListView listSetting;

    private String[] setting_items;
    private int[] icons = {R.drawable.privacy,R.drawable.logout};


    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        setting_items = getResources().getStringArray(R.array.setting_array);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_profile,null);
            nameLbl = view.findViewById(R.id.profile_name);
            emailLbl = view.findViewById(R.id.profile_email);
            listSetting = view.findViewById(R.id.list_setting);
            return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        SettingAdapter settingAdapter = new SettingAdapter(getContext(),R.layout.setting_adapterr,setting_items,icons);
        listSetting.setAdapter(settingAdapter);
        listSetting.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int len = icons.length;
        switch (position){
            case 0:
                //privacy policy
                break;
            case 1:
                //logout
                break;
        }
    }
}