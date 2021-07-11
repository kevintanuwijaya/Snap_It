package kevin.com.snapit.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.hms.support.account.result.AuthAccount;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import kevin.com.snapit.Adapter.SettingsAdapter;
import kevin.com.snapit.MainActivity;
import kevin.com.snapit.Model.Settings;
import kevin.com.snapit.R;
import kevin.com.snapit.SettingActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements Toolbar.OnMenuItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final String TAG = ProfileFragment.class.getSimpleName();

    private TextView nameLbl,emaillbl;
    private ImageView profilePic;
    private Toolbar toolbar;

    private AuthAccount authAccount = MainActivity.authAccount;


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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_profile,null);
            nameLbl = view.findViewById(R.id.profile_name);
            toolbar = view.findViewById(R.id.profile_toolbar);
            profilePic = view.findViewById(R.id.profile_image);
            emaillbl = view.findViewById(R.id.profile_email);

            nameLbl.setText(authAccount.getDisplayName()+" "+authAccount.getFamilyName());
            emaillbl.setText(authAccount.getEmail());

            Picasso.get().load(authAccount.getAvatarUri()).resize(300,300).centerCrop().into(profilePic);

            return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        toolbar.setOnMenuItemClickListener(this);

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings_btn:
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
    }
}