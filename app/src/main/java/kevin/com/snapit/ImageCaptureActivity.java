package kevin.com.snapit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.huawei.agconnect.AGCRoutePolicy;
import com.huawei.agconnect.AGConnectInstance;
import com.huawei.agconnect.AGConnectOptions;
import com.huawei.agconnect.AGConnectOptionsBuilder;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;
import com.huawei.agconnect.cloud.storage.core.AGCStorageManagement;
import com.huawei.agconnect.cloud.storage.core.ListResult;
import com.huawei.agconnect.cloud.storage.core.OnPausedListener;
import com.huawei.agconnect.cloud.storage.core.OnProgressListener;
import com.huawei.agconnect.cloud.storage.core.StorageReference;
import com.huawei.agconnect.cloud.storage.core.UploadTask;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hmf.tasks.Tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.LongFunction;

import kevin.com.snapit.Model.ObjectTypeInfoHelper;
import kevin.com.snapit.Model.Picture;
import kevin.com.snapit.Model.Users;

public class ImageCaptureActivity extends AppCompatActivity {

    private final String TAG = ImageCaptureActivity.class.getSimpleName();

    private ImageView imageView;
    private Button button;

    private AGCStorageManagement agcStorageManagement;

    private Uri image_uri;
    private String imagePath,imageName;
    private AGConnectCloudDB mCloudDB;
    private CloudDBZoneConfig mConfig;
    private CloudDBZone mCloudDBZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_capture);
        AGConnectInstance.initialize(this);
        AGConnectCloudDB.initialize(this);

        imageView = findViewById(R.id.image_capture_image);
        button = findViewById(R.id.image_capture_savebtn);
        agcStorageManagement = AGCStorageManagement.getInstance("users-iihs6");
        initDB();

        String image_s = getIntent().getStringExtra(MediaStore.EXTRA_OUTPUT);
        imagePath = getIntent().getStringExtra("FILE_PATH");
        imageName = getIntent().getStringExtra("FILE_NAME");
        image_uri = Uri.parse(image_s);

        imageView.setImageURI(image_uri);
        requestPermission();
        Log.d(TAG,image_uri.toString());

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO belum save imagenya;
                Log.d(TAG,"Image Saved");
                login();
                uploadFile();
            }
        });
    }

    private void requestPermission(){

        boolean writeExternalPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean readExternalPermission = ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if(!writeExternalPermission && !readExternalPermission){
            String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this,permission,1);
        }
    }

    private void uploadFile(){
        if(agcStorageManagement == null){
            agcStorageManagement = AGCStorageManagement.getInstance("users-iihs6");
        }


        String files = "Users_Picture/"; //default;
        if(imagePath.toLowerCase().endsWith(".jpg")||imagePath.toLowerCase().endsWith(".png")){
            files = "Users_Picture/";
        }


        File file = new File(imagePath+"/"+imageName);
        Log.d(TAG,imagePath);

        if(!file.isFile()){
            Log.d(TAG,"File not Found");
            return;
        }

        StorageReference storageReference = agcStorageManagement.getStorageReference(files + imageName);

        try {
            Log.d(TAG,"MASUK");
            UploadTask uploadTask = storageReference.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG, "onFailure: " + e);
                    Log.d(TAG,"Upload Failed");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.UploadResult>() {
                @Override
                public void onSuccess(UploadTask.UploadResult uploadResult) {
                    Log.d(TAG,"Upload Success");
                    Picture picture = new Picture();

                    picture.setUsers_email(MainActivity.authAccount.getEmail());
                    picture.setUsers_picturePath(imageName);

                    upsertPictureInfos(picture);

                    Intent intent = new Intent(ImageCaptureActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.UploadResult>() {
                @Override
                public void onProgress(UploadTask.UploadResult uploadResult) {

                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.UploadResult>() {
                @Override
                public void onPaused(UploadTask.UploadResult uploadResult) {

                }
            });
        }catch (Exception e){
            Log.d(TAG,e.getMessage());
        }
    }

    private void login() {
        if (AGConnectAuth.getInstance().getCurrentUser() != null) {
            Log.d(TAG,"Already Sign-in");
            return;
        }
        AGConnectAuth.getInstance().signInAnonymously().addOnSuccessListener(new OnSuccessListener<SignInResult>() {
            @Override
            public void onSuccess(SignInResult signInResult) {
                Log.d(TAG,"Sign In Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG,"Sign In Failed");
            }
        });
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

    private void upsertPictureInfos(Picture picture) {
        if (mCloudDBZone == null) {
            Log.w(TAG, "CloudDBZone is null, try re-openit");
            return;
        }
        Task<Integer> upsertTask = mCloudDBZone.executeUpsert(picture);
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