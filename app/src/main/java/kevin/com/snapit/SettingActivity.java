package kevin.com.snapit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import kevin.com.snapit.Adapter.SettingsAdapter;
import kevin.com.snapit.Model.Settings;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recycleSetting;
    private ImageView back;
    private ArrayList<Settings> settings = new ArrayList<Settings>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        recycleSetting = findViewById(R.id.setting_recycle);
        back = findViewById(R.id.setting_back);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SettingsAdapter settingAdapter = new SettingsAdapter(this,settings);
        recycleSetting.setAdapter(settingAdapter);
        recycleSetting.setLayoutManager(new LinearLayoutManager(this));
        back.setOnClickListener(this);
    }

    private void init(){
        String[] setting_items = getResources().getStringArray(R.array.setting_array);
        int[] icons = {R.drawable.privacy,R.drawable.logout};

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