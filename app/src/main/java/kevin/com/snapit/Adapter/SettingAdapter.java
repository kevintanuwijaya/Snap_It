package kevin.com.snapit.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import kevin.com.snapit.R;


public class SettingAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] setting_item;
    private int[] icons;

    public SettingAdapter(@NonNull Context context, int resource, String[] items,int[] icons) {
        super(context, resource);
        this.context = context;
        this.setting_item = items;
        this.icons = icons;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.setting_adapterr,null);

        ImageView icon = view.findViewById(R.id.setting_adapter_icon);
        TextView name = view.findViewById(R.id.setting_adapter_name);

        icon.setImageResource(icons[position]);
        name.setText(setting_item[position]);

        return view;
    }
}
