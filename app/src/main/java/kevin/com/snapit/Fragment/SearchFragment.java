package kevin.com.snapit.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig;
import com.huawei.agconnect.cloud.database.CloudDBZoneObjectList;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;
import com.huawei.agconnect.cloud.database.CloudDBZoneSnapshot;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;

import java.util.ArrayList;

import kevin.com.snapit.Adapter.UserAdapter;
import kevin.com.snapit.Model.LoadingDialog;
import kevin.com.snapit.Model.ObjectTypeInfoHelper;
import kevin.com.snapit.Model.Users;
import kevin.com.snapit.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";




    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private final String TAG = SearchFragment.class.getSimpleName();

    private AGConnectCloudDB mCloudDB;
    private CloudDBZoneConfig mConfig;
    private CloudDBZone mCloudDBZone;

    private RecyclerView userRecycle;
    private SearchView searchView;

    private ArrayList<Users> users = new ArrayList<Users>();
    private UserAdapter userAdapter;



    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        initDB();

        LoadingDialog loadingDialog = new LoadingDialog(getContext());

        loadingDialog.startDialog();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"Prepare for Query");
                final CloudDBZoneQuery<Users> queryUser = CloudDBZoneQuery.where(Users.class);
                queryUsers(queryUser);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismissDialog();
                        Log.d(TAG,"Insert to Adapter");
                        userAdapter = new UserAdapter(getActivity(),users);
                        userRecycle.setAdapter(userAdapter);
                        userRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }
                },3000);
            }
        },2000);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search,null);
        userRecycle = view.findViewById(R.id.search_user_recycle);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.search_menu,menu);

        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //TODO MASIH BELUM BENER
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG,"Search Clicked");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG,"Filtering");
                ArrayList<Users> filterUser = new ArrayList<Users>(users);
                UserAdapter currentAdapter;
                if(newText != null || !newText.isEmpty()){
                    filterUser.removeAll(filterUser);
                    for (Users user : users){
                        if(user.getUsers_name().toLowerCase().contains(newText.toLowerCase())){
                            filterUser.add(user);
                        }
                    }
                }
                currentAdapter = new UserAdapter(getActivity(),filterUser);
                userRecycle.setAdapter(currentAdapter);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
        super.onCreateOptionsMenu(menu,inflater);
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

        Task<CloudDBZone> openDBZoneTask = mCloudDB.openCloudDBZone2(mConfig, true);
        openDBZoneTask.addOnSuccessListener(new OnSuccessListener<CloudDBZone>() {
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
    }

    public void queryUsers(CloudDBZoneQuery<Users> query) {
        if (mCloudDBZone == null) {
            Log.w(TAG, "CloudDBZone is null, try re-open it");
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
                Toast.makeText(getContext(),"Search Failed",Toast.LENGTH_SHORT).show();
                Log.d(TAG,e.getMessage());
                FragmentTransaction fragmentTransaction = ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,new SearchFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
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