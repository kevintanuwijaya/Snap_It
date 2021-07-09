package kevin.com.snapit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;

import kevin.com.snapit.Model.ObjectTypeInfoHelper;
import kevin.com.snapit.Model.Users;

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = RegisterActivity.class.getSimpleName();

    private EditText emailTxt, firstNameTxt, lastNameTxt;
    private Button registerBtn;
    private AGConnectCloudDB mCloudDB;
    private CloudDBZoneConfig mConfig;
    private CloudDBZone mCloudDBZone;
    private AGConnectUser anonymousUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailTxt.getText().toString();
                String name = firstNameTxt.getText().toString()+" "+lastNameTxt.getText().toString();

                Users user = new Users();

                user.setUsers_email(email);
                user.setUsers_name(name);
                Log.d("INSERT",email);
                Log.d("INSERT",name);

                signInAnonymus(user);

                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void init(){

        emailTxt = findViewById(R.id.register_email_edit);
        firstNameTxt = findViewById(R.id.register_firstname_edit);
        lastNameTxt = findViewById(R.id.register_lastname_edit);
        registerBtn = findViewById(R.id.register_save_btn);

        String email = getIntent().getStringExtra("EMAIL");
        String firstName = getIntent().getStringExtra("FIRST_NAME");
        String lastName = getIntent().getStringExtra("LAST_NAME");

        emailTxt.setText(email);
        firstNameTxt.setText(firstName);
        lastNameTxt.setText(lastName);

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

    //TODO insert masih belum jalan
    private void upsertUsersInfos(Users users) {
        if (mCloudDBZone == null) {
            Log.w(TAG, "CloudDBZone is null, try re-openit");
            return;
        }
        Task<Integer> upsertTask = mCloudDBZone.executeUpsert(users);
        upsertTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer cloudDBZoneResult) {
                Log.w(TAG, "upsert " + cloudDBZoneResult + " records");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG,"Upsert Failed");
            }
        });
    }

    private void signInAnonymus(Users user){
        AGConnectAuth.getInstance().signInAnonymously().addOnSuccessListener(new OnSuccessListener<SignInResult>() {
            @Override
            public void onSuccess(SignInResult signInResult) {
                anonymousUser = signInResult.getUser();
                upsertUsersInfos(user);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {

            }
        });
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