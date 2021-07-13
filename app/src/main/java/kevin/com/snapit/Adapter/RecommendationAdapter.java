package kevin.com.snapit.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kevin.com.snapit.Model.Location;
import kevin.com.snapit.R;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Location> locations;

    public RecommendationAdapter(Context context, ArrayList<Location> locations) {
        this.context = context;
        this.locations = locations;
    }

    @NonNull
    @Override
    public RecommendationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recommendation_home_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationAdapter.ViewHolder holder, int position) {
        Picasso.get().load(locations.get(position).getImage()).into(holder.image);
        holder.title.setText(locations.get(position).getName());
        holder.distance.setText(Double.toString(locations.get(position).getDistance()) + " km from your current location");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO kalo mau si recommendation bisa di klik code disini
//                Intent intent = new Intent(context, ArticelDetailActivity.class);
//                intent.putExtra("ARTICEL", articels.get(position));
//                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView title, distance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.home_recommendation_image);
            title = itemView.findViewById(R.id.recommendation_title);
            distance = itemView.findViewById(R.id.recommendation_address);
        }
    }
}
