package kevin.com.snapit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;

import kevin.com.snapit.Fragment.HomeFragment;
import kevin.com.snapit.Fragment.MapFragment;
import kevin.com.snapit.Fragment.ProfileFragment;
import kevin.com.snapit.Fragment.SearchFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private final int CAMERA_REQUEST_TOKEN = 1000;
    private final int PERMISSION_CODE = 1001;

    private Fragment fragment;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton floatingActionButton;
    private Toolbar top_toolbar;
    public static AuthAccount authAccount;

    private Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        floatingActionButton = findViewById(R.id.camera_main_btn);
        top_toolbar = findViewById(R.id.home_toolbar);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        floatingActionButton.setOnClickListener(this);
//        setSupportActionBar(top_toolbar);


        String startFramgent = getIntent().getStringExtra("FRAGMENT");

        if(startFramgent==null){
            startFramgent = "Home";
        }

        if(startFramgent.equals("Profile")){
            fragment = new ProfileFragment();
            loadFrame(fragment);
        }else{
            fragment = new HomeFragment();
            loadFrame(fragment);
        }
        silentSignIn();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
//        return true;
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.home_bottom_nav:
                fragment = new HomeFragment();
                loadFrame(fragment);
                return true;
            case R.id.search_bottom_nav:
                fragment = new SearchFragment();
                loadFrame(fragment);
                return true;
            case R.id.map_bottom_nav:
                fragment = new MapFragment();
                loadFrame(fragment);
                return true;
            case R.id.profile_bottom_nav:
                fragment = new ProfileFragment();
                loadFrame(fragment);
                return true;

        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.camera_main_btn:
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        String[] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission,PERMISSION_CODE);
                    }else{
                        openCamera();

                    }
                }else{
                    openCamera();
                }

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }else {
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void openCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Current Picture");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,CAMERA_REQUEST_TOKEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_TOKEN && resultCode == RESULT_OK){
            Toast.makeText(this,"Image Capture",Toast.LENGTH_SHORT).show();
            Intent captureIntent = new Intent(MainActivity.this,ImageCaptureActivity.class);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri.toString());
            startActivity(captureIntent);
        }

    }

    private void loadFrame(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void silentSignIn(){
        AccountAuthParams accountAuthParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAuthorizationCode().createParams();
        AccountAuthService accountAuthService = AccountAuthManager.getService(MainActivity.this,accountAuthParams);

        Task<AuthAccount> silentSignin = accountAuthService.silentSignIn();
        silentSignin.addOnSuccessListener(new OnSuccessListener<AuthAccount>() {
            @Override
            public void onSuccess(AuthAccount authAccount) {
                MainActivity.authAccount = authAccount;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Intent failIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(failIntent);
            }
        });
    }
}