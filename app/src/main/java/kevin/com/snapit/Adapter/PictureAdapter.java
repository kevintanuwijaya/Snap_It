package kevin.com.snapit.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.huawei.agconnect.AGConnectInstance;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.cloud.storage.core.AGCStorageManagement;
import com.huawei.agconnect.cloud.storage.core.FileMetadata;
import com.huawei.agconnect.cloud.storage.core.StorageReference;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Handler;

import kevin.com.snapit.Model.Picture;
import kevin.com.snapit.R;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> pictureUrl;

    public PictureAdapter(Context context, ArrayList<String> pictureUrl) {
        this.context = context;
        this.pictureUrl = pictureUrl;
        Log.d("UserAdapter","DAPET");
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.user_picture_grid, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.itemView).load(pictureUrl.get(position)).into(holder.imageView);
    }


    @Override
    public int getItemCount() {
        return pictureUrl.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.user_picture_item);
        }
    }
}
