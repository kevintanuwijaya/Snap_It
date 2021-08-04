package kevin.com.snapit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;

public class LaunchAppActivity extends AppCompatActivity {

    private ImageView icon;
    private TextView name;
    private Animation iconAnim, textAnim;
    private AccountAuthParams accountAuthParams;
    private AccountAuthService accountAuthService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_app);

        icon = findViewById(R.id.launch_app_icon);
        name = findViewById(R.id.launch_app_name);

        iconAnim = AnimationUtils.loadAnimation(this,R.anim.launch_app_icon_animation);
        textAnim = AnimationUtils.loadAnimation(this,R.anim.launch_app_text_animation);

        accountAuthParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAuthorizationCode().createParams();
        accountAuthService = AccountAuthManager.getService(LaunchAppActivity.this,accountAuthParams);

        icon.setAnimation(iconAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                name.setAnimation(textAnim);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        name.setVisibility(View.VISIBLE);
                        Task<AuthAccount> silentSignin = accountAuthService.silentSignIn();
                        Toast.makeText(LaunchAppActivity.this, "inside Silent", Toast.LENGTH_SHORT);
                        silentSignin.addOnSuccessListener(new OnSuccessListener<AuthAccount>() {
                            @Override
                            public void onSuccess(AuthAccount authAccount) {
                                Toast.makeText(LaunchAppActivity.this, "Silent success", Toast.LENGTH_SHORT);
                                Intent successIntent = new Intent(LaunchAppActivity.this, MainActivity.class);
                                successIntent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(successIntent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(LaunchAppActivity.this, "Silent fail", Toast.LENGTH_SHORT);
                                Intent failIntent = new Intent(LaunchAppActivity.this, LoginActivity.class);
                                failIntent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(failIntent);
                            }
                        });
                    }
                },1000);
            }
        },2000);
    }
}