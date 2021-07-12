package kevin.com.snapit.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.agconnect.AGConnectInstance;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig;
import com.huawei.agconnect.cloud.database.CloudDBZoneObjectList;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;
import com.huawei.agconnect.cloud.database.CloudDBZoneSnapshot;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;
import com.huawei.agconnect.cloud.storage.core.AGCStorageManagement;
import com.huawei.agconnect.cloud.storage.core.FileMetadata;
import com.huawei.agconnect.cloud.storage.core.StorageReference;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.account.result.AuthAccount;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import kevin.com.snapit.Adapter.PictureAdapter;
import kevin.com.snapit.Adapter.SettingsAdapter;
import kevin.com.snapit.MainActivity;
import kevin.com.snapit.Model.LoadingDialog;
import kevin.com.snapit.Model.ObjectTypeInfoHelper;
import kevin.com.snapit.Model.Picture;
import kevin.com.snapit.Model.Settings;
import kevin.com.snapit.Model.Users;
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
    private RecyclerView recyclerView;

    private ArrayList<Picture> pictures = new ArrayList<Picture>();
    private ArrayList<String> pictureUrl = new ArrayList<String>();

    private AuthAccount authAccount = MainActivity.authAccount;
    private AGConnectCloudDB mCloudDB;
    private CloudDBZoneConfig mConfig;
    private CloudDBZone mCloudDBZone;

    private boolean find = false;


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
        AGConnectInstance.initialize(getContext());
        AGConnectCloudDB.initialize(getContext());
        initDB();

        LoadingDialog loadingDialog = new LoadingDialog(getContext());
        loadingDialog.startDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CloudDBZoneQuery<Picture> query = CloudDBZoneQuery.where(Picture.class).equalTo("users_email",authAccount.getEmail());
                queryPicture(query);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getImage();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                while (!find){

                                }
                                loadingDialog.dismissDialog();

                                LinearLayoutManager pictureLayout = new LinearLayoutManager(getActivity());
                                pictureLayout.setOrientation(LinearLayoutManager.HORIZONTAL);

                                PictureAdapter pictureAdapter = new PictureAdapter(getContext(),pictureUrl);
                                recyclerView.setAdapter(pictureAdapter);
                                recyclerView.setLayoutManager(pictureLayout);
                            }
                        },4000);
                    }
                },2000);
            }
        },1000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_profile,null);
            nameLbl = view.findViewById(R.id.profile_name);
            toolbar = view.findViewById(R.id.profile_toolbar);
            profilePic = view.findViewById(R.id.profile_image);
            emaillbl = view.findViewById(R.id.profile_email);
            recyclerView = view.findViewById(R.id.user_picture_recycle);

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

    private void initDB(){

        mCloudDB = AGConnectCloudDB.getInstance();

        try {
            mCloudDB.createObjectType(ObjectTypeInfoHelper.getObjectTypeInfo());
        } catch (AGConnectCloudDBException e) {
            e.printStackTrace();
        }

        mConfig = new CloudDBZoneConfig("Picture",
                CloudDBZoneConfig.CloudDBZoneSyncProperty.CLOUDDBZONE_CLOUD_CACHE,
                CloudDBZoneConfig.CloudDBZoneAccessProperty.CLOUDDBZONE_PUBLIC);
        mConfig.setPersistenceEnabled(true);


        Task<CloudDBZone> openDBZoneTask = mCloudDB.openCloudDBZone2(mConfig, true);
        openDBZoneTask.addOnSuccessListener(new OnSuccessListener<CloudDBZone>() {
            @Override
            public void onSuccess(CloudDBZone cloudDBZone) {
                Log.i(TAG, "open cloudDBZone success");
                mCloudDBZone = cloudDBZone;
                Log.d("DataBase","Here");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.w(TAG, "open cloudDBZone failed for " + e.getMessage());
            }
        });
    }

    public void queryPicture(CloudDBZoneQuery<Picture> query) {
        if (mCloudDBZone == null) {
            Log.w(TAG, "CloudDBZone is null, try re-open it");
            reloadPage();
            return;
        }
        Task<CloudDBZoneSnapshot<Picture>> queryTask = mCloudDBZone.executeQuery(query,CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
        queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<Picture>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<Picture> snapshot) {
                Log.d(TAG,"Query");
                processQueryResult(snapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(),"Search Failed",Toast.LENGTH_SHORT).show();
                Log.d(TAG,e.getMessage());
                reloadPage();
            }
        });
    }

    private void reloadPage(){
        FragmentTransaction fragmentTransaction = ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,new ProfileFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void processQueryResult(CloudDBZoneSnapshot<Picture> snapshot) {
        CloudDBZoneObjectList<Picture> queryResultPicture = snapshot.getSnapshotObjects();
        try {
            pictures.removeAll(pictures);
            while (queryResultPicture.hasNext()) {
                Picture picture = queryResultPicture.next();
                pictures.add(picture);
                Log.d("DATABASE",picture.getUsers_picturePath()+" is added");
            }
        } catch (AGConnectCloudDBException e) {
            Log.w(TAG, "processQueryResult: " + e.getMessage());
        }
        snapshot.release();
    }

    private void getImage(){
        AGCStorageManagement agcStorageManagement = AGCStorageManagement.getInstance("users-iihs6");
        AGConnectUser agConnectUser = AGConnectAuth.getInstance().getCurrentUser();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for(int i=0 ; i<pictures.size() ; i++){
                        StorageReference storageReference = agcStorageManagement.getStorageReference("Users_Picture/"+pictures.get(i).getUsers_picturePath());
                        Task<FileMetadata> fileMetadataTask = storageReference.getFileMetadata();
                        fileMetadataTask.addOnSuccessListener(new OnSuccessListener<FileMetadata>() {
                            @Override
                            public void onSuccess(FileMetadata fileMetadata) {
                                Task task = fileMetadata.getStorageReference().getDownloadUrl();
                                task.addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Log.d(TAG,o.toString());
                                        pictureUrl.add(o.toString());
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Log.d(TAG,e.getMessage());
                            }
                        });
                    }
                    find = true;
                }catch (Exception e){
                    Log.d(TAG,e.getMessage());
                }
            }
        }).start();
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            mCloudDB.closeCloudDBZone(mCloudDBZone);
        } catch (AGConnectCloudDBException e) {
            e.printStackTrace();
        }
    }
}