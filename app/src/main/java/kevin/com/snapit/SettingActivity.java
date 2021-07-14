package kevin.com.snapit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.service.AccountAuthService;

import java.util.ArrayList;

import kevin.com.snapit.Adapter.SettingsAdapter;
import kevin.com.snapit.Model.Settings;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = SettingActivity.class.getSimpleName();

    private RecyclerView recycleSetting;
    private ImageView back;
    private LinearLayout logoutBtn;
    private ArrayList<Settings> settings = new ArrayList<Settings>();
    private AccountAuthParams accountAuthParams;
    private AccountAuthService accountAuthService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        recycleSetting = findViewById(R.id.setting_recycle);
        back = findViewById(R.id.setting_back);
        logoutBtn = findViewById(R.id.logout_btn);
        accountAuthParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAuthorizationCode().createParams();
        accountAuthService = AccountAuthManager.getService(SettingActivity.this,accountAuthParams);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SettingsAdapter settingAdapter = new SettingsAdapter(this,settings);
        recycleSetting.setAdapter(settingAdapter);
        recycleSetting.setLayoutManager(new LinearLayoutManager(this));

        back.setOnClickListener(this);
        logoutBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                accountAuthService.cancelAuthorization().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "onSuccess: ");
                            Intent intent1 = new Intent(SettingActivity.this,LoginActivity.class);
                            startActivity(intent1);
                        } else {
                            // Handle the exception.
                            Exception exception = task.getException();
                            if (exception instanceof ApiException){
                                int statusCode = ((ApiException) exception).getStatusCode();
                                Log.i(TAG, "onFailure: " + statusCode);
                            }
                        }
                    }
                });
                return false;
            }
        });
    }

    private void init(){
        String[] setting_items = getResources().getStringArray(R.array.setting_array);
        int[] icons = {R.drawable.privacy};

        for(int i=0 ; i<setting_items.length ; i++){
            Log.d("ADAPTER",setting_items[i]);
            Settings obj = new Settings(setting_items[i],icons[i]);
            settings.add(obj);
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_back:
                    Intent intent = new Intent(this,MainActivity.class);
                    intent.putExtra("FRAGMENT","Profile");
                    startActivity(intent);
                break;
        }
    }
}