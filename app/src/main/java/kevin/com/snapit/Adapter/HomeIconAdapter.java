package kevin.com.snapit.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kevin.com.snapit.ArticelDetailActivity;
import kevin.com.snapit.CategoryOnMapActivity;
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context,"Clicked: "+ iconList.get(position).getName(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, CategoryOnMapActivity.class);
                intent.putExtra("CATEGORY", iconList.get(position).getName());
                context.startActivity(intent);
            }
        });
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
