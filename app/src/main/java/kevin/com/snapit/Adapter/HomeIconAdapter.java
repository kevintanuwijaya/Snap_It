package kevin.com.snapit.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kevin.com.snapit.Model.Icon;
import kevin.com.snapit.R;

public class HomeIconAdapter extends RecyclerView.Adapter<HomeIconAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Icon> iconList;

    public HomeIconAdapter(Context context, ArrayList<Icon> iconList) {
        this.context = context;
        this.iconList = iconList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.home_icon_column,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.icon.setImageResource(iconList.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return iconList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon_image);
        }
    }
}
