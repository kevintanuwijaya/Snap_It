package kevin.com.snapit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class LaunchAppActivity extends AppCompatActivity {

    private ImageView icon;
    private TextView name;
    private Animation iconAnim, textAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_app);

        icon = findViewById(R.id.launch_app_icon);
        name = findViewById(R.id.launch_app_name);

        iconAnim = AnimationUtils.loadAnimation(this,R.anim.launch_app_icon_animation);
        textAnim = AnimationUtils.loadAnimation(this,R.anim.launch_app_text_animation);

        icon.setAnimation(iconAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                name.setAnimation(textAnim);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        name.setVisibility(View.VISIBLE);

                        //TODO cek pake silent signin buat masuk antara loginactivity atau mainactivity

                        Intent intent = new Intent(LaunchAppActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                },2000);
            }
        },4000);

    }
}