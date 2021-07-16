package kevin.com.snapit;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
    // Define the request code for signInIntent.
    private static final int REQUEST_CODE_SIGN_IN = 2222;

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

        ActivityResultLauncher<Intent> intentLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result ->  {
                    if (result.getResultCode() == REQUEST_CODE_SIGN_IN) {

                    }
                }
        );

        switch (v.getId()) {
            case R.id.login_btn:
                AccountAuthParams authParams1 = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAuthorizationCode().setEmail().setProfile().setMobileNumber().createParams();
                AccountAuthService service1 = AccountAuthManager.getService(LoginActivity.this, authParams1);
                // Use silent sign-in to sign in with a HUAWEI ID.
                Task<AuthAccount> task = service1.silentSignIn();
                task.addOnSuccessListener(new OnSuccessListener<AuthAccount>() {
                    @Override
                    public void onSuccess(AuthAccount authAccount) {
                        // The silent sign-in is successful. Process the returned account object AuthAccount to obtain the HUAWEI ID information.
                        dealWithResultOfSignIn(authAccount);
                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // The silent sign-in fails. Use the getSignInIntent() method to show the authorization or sign-in screen.
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            Intent signInIntent = service1.getSignInIntent();
                            startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
                        }
                    }
                });
                break;
        }
    }

    private void dealWithResultOfSignIn(AuthAccount authAccount) {
        // Obtain the HUAWEI DI information.
        Log.i(TAG, "display name:" + authAccount.getDisplayName());
        Log.i(TAG, "photo uri string:" + authAccount.getAvatarUriString());
        Log.i(TAG, "photo uri:" + authAccount.getAvatarUri());
        Log.i(TAG, "email:" + authAccount.getEmail());
        Log.i(TAG, "openid:" + authAccount.getOpenId());
        Log.i(TAG, "unionid:" + authAccount.getUnionId());
        // TODO: Implement service logic after the HUAWEI ID information is obtained.

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Log.i(TAG, "onActivitResult of sigInInIntent, request code: " + REQUEST_CODE_SIGN_IN);
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            Toast toast = Toast.makeText(this, "ERROR", Toast.LENGTH_LONG);
            toast.show();
            if (authAccountTask.isSuccessful()) {
                // The sign-in is successful, and the authAccount object that contains the HUAWEI ID information is obtained.
                AuthAccount authAccount = authAccountTask.getResult();
                dealWithResultOfSignIn(authAccount);
                Log.i(TAG, "onActivitResult of sigInInIntent, request code: " + REQUEST_CODE_SIGN_IN);

                if(authAccount.getEmail() == null){
                    Toast.makeText(this,"Please enable all the autorization",Toast.LENGTH_SHORT).show();
                    return;
                }

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
                // The sign-in fails. Find the failure cause from the status code. For more information, please refer to the "Error Codes" section in the API Reference.
                Log.e(TAG, "sign in failed : " +((ApiException)authAccountTask.getException()).getStatusCode());
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