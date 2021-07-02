package kevin.com.snapit.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kevin.com.snapit.Model.Articel;
import kevin.com.snapit.R;

public class ArticelAdapter extends RecyclerView.Adapter<ArticelAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Articel> articels;

    public ArticelAdapter(Context context, ArrayList<Articel> articels) {
        this.context = context;
        this.articels = articels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.articel_home_column,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.image.setImageResource(articels.get(position).getImage());
        holder.title.setText(articels.get(position).getTitle());
        holder.author.setText(articels.get(position).getAuthor());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
    }

    @Override
    public int getItemCount() {
        return articels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView title,author;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.home_articel_image);
            title = itemView.findViewById(R.id.articel_title);
            author = itemView.findViewById(R.id.articel_author);
        }
    }
}
