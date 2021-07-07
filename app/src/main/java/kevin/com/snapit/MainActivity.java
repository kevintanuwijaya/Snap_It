package kevin.com.snapit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    private Fragment fragment;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton floatingActionButton;
    private Toolbar top_toolbar;
    public static AuthAccount authAccount;

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

        if (startFramgent == null){
            startFramgent = "Home";
        }

//        if (startFramgent.equals("Profile")) {
//            fragment = new ProfileFragment();
//            loadFrame(fragment);
//        } else {
            fragment = new HomeFragment();
            loadFrame(fragment);
//        }
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
                break;
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
//                MainActivity.authAccount = authAccount.getAccount(this);
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