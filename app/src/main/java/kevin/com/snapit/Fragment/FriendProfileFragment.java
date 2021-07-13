package kevin.com.snapit.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;

import kevin.com.snapit.Adapter.PictureAdapter;
import kevin.com.snapit.Model.LoadingDialog;
import kevin.com.snapit.Model.ObjectTypeInfoHelper;
import kevin.com.snapit.Model.Picture;
import kevin.com.snapit.Model.Users;
import kevin.com.snapit.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String FRIEND_EMAIL = "friendEmail";

    // TODO: Rename and change types of parameters
    
    private final String TAG = FriendProfileFragment.class.getSimpleName();

    private String friendEmail;
    private boolean find = false;

    private TextView nameLbl,emailLbl;
    private RecyclerView recyclerView;

    private AGConnectCloudDB mCloudDB;
    private CloudDBZoneConfig mConfig, mConfig1;
    private CloudDBZone mCloudDBZone, mCloudDBZone1;

    private ArrayList<Users> users = new ArrayList<Users>();
    private ArrayList<Picture> pictures = new ArrayList<Picture>();
    private ArrayList<String> pictureURL = new ArrayList<String>();


    public FriendProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FriendProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendProfileFragment newInstance(String email) {
        FriendProfileFragment fragment = new FriendProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(FRIEND_EMAIL,email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        friendEmail = (String) getArguments().getSerializable(FRIEND_EMAIL);
        View view = inflater.inflate(R.layout.fragment_friend_profile,null);
        nameLbl = view.findViewById(R.id.friend_profile_name);
        emailLbl = view.findViewById(R.id.friend_profile_email);
        recyclerView = view.findViewById(R.id.friend_picture_recycle);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        initDB();
        LoadingDialog loadingDialog = new LoadingDialog(getContext());
        loadingDialog.startDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CloudDBZoneQuery<Users> query = CloudDBZoneQuery.where(Users.class).equalTo("users_email",friendEmail);
                queryUsers(query);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nameLbl.setText(users.get(0).getUsers_name());
                        emailLbl.setText(users.get(0).getUsers_email());
                        CloudDBZoneQuery<Picture> queryPicture = CloudDBZoneQuery.where(Picture.class).equalTo("users_email",users.get(0).getUsers_email());
                        queryPicture(queryPicture);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getImage();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d(TAG,"Found");
                                        loadingDialog.dismissDialog();

                                        LinearLayoutManager pictureLayout = new LinearLayoutManager(getActivity());
                                        pictureLayout.setOrientation(LinearLayoutManager.HORIZONTAL);

                                        PictureAdapter pictureAdapter = new PictureAdapter(getContext(),pictureURL);
                                        recyclerView.setAdapter(pictureAdapter);
                                        recyclerView.setLayoutManager(pictureLayout);

                                    }
                                },4000);
                            }
                        },2000);
                    }
                },2000);
            }
        },1000);

    }

    private void initDB(){
        AGConnectCloudDB.initialize(getContext());
        mCloudDB = AGConnectCloudDB.getInstance();


        try {
            mCloudDB.createObjectType(ObjectTypeInfoHelper.getObjectTypeInfo());
        } catch (AGConnectCloudDBException e) {
            e.printStackTrace();
        }

        mConfig = new CloudDBZoneConfig("User",
                CloudDBZoneConfig.CloudDBZoneSyncProperty.CLOUDDBZONE_CLOUD_CACHE,
                CloudDBZoneConfig.CloudDBZoneAccessProperty.CLOUDDBZONE_PUBLIC);
        mConfig.setPersistenceEnabled(true);

        Task<CloudDBZone> openDBZoneTaskUser = mCloudDB.openCloudDBZone2(mConfig, true);
        openDBZoneTaskUser.addOnSuccessListener(new OnSuccessListener<CloudDBZone>() {
            @Override
            public void onSuccess(CloudDBZone cloudDBZone) {
                Log.w(TAG, "open clouddbzone success");
                mCloudDBZone = cloudDBZone;

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.w(TAG, "open clouddbzone failed for " + e.getMessage());
            }
        });

        mConfig1 = new CloudDBZoneConfig("Picture",
                CloudDBZoneConfig.CloudDBZoneSyncProperty.CLOUDDBZONE_CLOUD_CACHE,
                CloudDBZoneConfig.CloudDBZoneAccessProperty.CLOUDDBZONE_PUBLIC);
        mConfig1.setPersistenceEnabled(true);


        Task<CloudDBZone> openDBZoneTaskPicture = mCloudDB.openCloudDBZone2(mConfig1, true);
        openDBZoneTaskPicture.addOnSuccessListener(new OnSuccessListener<CloudDBZone>() {
            @Override
            public void onSuccess(CloudDBZone cloudDBZone) {
                Log.i(TAG, "open cloudDBZone success");
                mCloudDBZone1 = cloudDBZone;
                Log.d("DataBase","Here");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.w(TAG, "open cloudDBZone failed for " + e.getMessage());
            }
        });
    }

    public void queryUsers(CloudDBZoneQuery<Users> query) {
        if (mCloudDBZone == null) {
            Log.w(TAG, "CloudDBZoneUser is null, try re-open it");
            reloadPage();
            return;
        }
        Task<CloudDBZoneSnapshot<Users>> queryTask = mCloudDBZone.executeQuery(query,CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
        queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<Users>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<Users> snapshot) {
                Log.d(TAG,"Query");
                processQueryResult(snapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG,e.getMessage());
                Toast.makeText(getContext(),"Search User Failed",Toast.LENGTH_SHORT).show();
                reloadPage();
            }
        });
    }

    public void queryPicture(CloudDBZoneQuery<Picture> query){
        if(mCloudDBZone1==null){
            Log.w(TAG, "CloudDBZonePicture is null, try re-open it");
            reloadPage();
            return;
        }
        Task<CloudDBZoneSnapshot<Picture>> queryTask = mCloudDBZone1.executeQuery(query, CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
        queryTask.addOnSuccessListener(new OnSuccessListener<CloudDBZoneSnapshot<Picture>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<Picture> pictureCloudDBZoneSnapshot) {
                processQueryPicture(pictureCloudDBZoneSnapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG,e.getMessage());
                Toast.makeText(getContext(),"Search Picture Failed",Toast.LENGTH_SHORT).show();
                reloadPage();
            }
        });
    }

    private void reloadPage(){
        FragmentTransaction fragmentTransaction = ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction();
        Fragment fragment = new FriendProfileFragment().newInstance(friendEmail);
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void processQueryResult(CloudDBZoneSnapshot<Users> snapshot) {
        CloudDBZoneObjectList<Users> queryResultUser = snapshot.getSnapshotObjects();
        try {
            users.removeAll(users);
            while (queryResultUser.hasNext()) {
                Users user = queryResultUser.next();
                users.add(user);
                Log.d("DATABASE",user.getUsers_email()+" is added");
            }
        } catch (AGConnectCloudDBException e) {
            Log.w(TAG, "processQueryResult: " + e.getMessage());
        }
        snapshot.release();
    }

    private void processQueryPicture(CloudDBZoneSnapshot<Picture> snapshot){
        CloudDBZoneObjectList<Picture> queryResultPicture = snapshot.getSnapshotObjects();
        try {
            pictures.removeAll(pictures);
            while (queryResultPicture.hasNext()){
                Picture picture = queryResultPicture.next();
                pictures.add(picture);
                Log.d("DATABASE",picture.getUsers_picturePath()+" is added");
            }
        }catch (Exception e){
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
                                        pictureURL.add(o.toString());
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