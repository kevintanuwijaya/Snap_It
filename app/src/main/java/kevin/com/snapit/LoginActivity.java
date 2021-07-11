package kevin.com.snapit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig;
import com.huawei.agconnect.cloud.database.CloudDBZoneObjectList;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;
import com.huawei.agconnect.cloud.database.CloudDBZoneSnapshot;
import com.huawei.agconnect.cloud.database.ListenerHandler;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;

import java.util.ArrayList;
import java.util.List;

import kevin.com.snapit.Model.LoadingDialog;
import kevin.com.snapit.Model.ObjectTypeInfoHelper;
import kevin.com.snapit.Model.Users;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private AGConnectCloudDB mCloudDB;
    private CloudDBZone mCloudDBZone;
    private CloudDBZoneConfig mConfig;
    private ListenerHandler mRegister;
    private List<Users> users = new ArrayList<>();
    private LoadingDialog loadingDialog = new LoadingDialog(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.login_btn).setOnClickListener(this);

        initDB();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                AccountAuthParams authParams1 = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAuthorizationCode().setEmail().setProfile().setMobileNumber().createParams();
                AccountAuthService service1 = AccountAuthManager.getService(LoginActivity.this, authParams1);
                startActivityForResult(service1.getSignInIntent(), 2222);
                break;
        }
    }

    private void initDB(){
        AGConnectCloudDB.initialize(this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Process the authorization result to obtain the authorization code from AuthAccount.
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2222) {
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask.isSuccessful()) {
                // The sign-in is successful, and the user's ID information and authorization code are obtained.
                AuthAccount authAccount = authAccountTask.getResult();
                Log.i(TAG, "serverAuthCode:" + authAccount.getAuthorizationCode());

                Log.d("Database",""+authAccount.getEmail());

                CloudDBZoneQuery<Users> query = CloudDBZoneQuery.where(Users.class).equalTo("users_email",authAccount.getEmail());

                queryUsers(query);

                loadingDialog.startDialog();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(users.isEmpty()){
                            loadingDialog.dismissDialog();
                            Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                            registerIntent.putExtra("EMAIL",authAccount.getEmail());
                            registerIntent.putExtra("FIRST_NAME",authAccount.getGivenName());
                            registerIntent.putExtra("LAST_NAME",authAccount.getFamilyName());
                            startActivity(registerIntent);
                        }else{
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                    }
                },2000);
            } else {
                // The sign-in failed.
                Log.e(TAG, "sign in failed:" + ((ApiException) authAccountTask.getException()).getStatusCode());
                startActivity(new Intent(this, MainActivity.class));
            }
        }
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
                processQueryResult(snapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void processQueryResult(CloudDBZoneSnapshot<Users> snapshot) {
        CloudDBZoneObjectList<Users> queryResultUser = snapshot.getSnapshotObjects();
        try {
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
    protected void onDestroy() {
        super.onDestroy();
        try {
            mCloudDB.closeCloudDBZone(mCloudDBZone);
        } catch (AGConnectCloudDBException e) {
            e.printStackTrace();
        }
    }
}