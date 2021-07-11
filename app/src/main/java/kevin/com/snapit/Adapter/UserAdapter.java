package kevin.com.snapit.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;

import java.util.ArrayList;

import kevin.com.snapit.Fragment.FriendProfileFragment;
import kevin.com.snapit.Fragment.ProfileFragment;
import kevin.com.snapit.Model.ObjectTypeInfoHelper;
import kevin.com.snapit.Model.Users;
import kevin.com.snapit.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {


    private Context context;
    private ArrayList<Users> users;
    private ArrayList<Users> usersFull;

    public UserAdapter(Context context, ArrayList<Users> users) {
        this.context = context;
        this.users = users;
        this.usersFull = new ArrayList<>(users);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.search_user_adapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.emailTxt.setText(users.get(position).getUsers_email());
        holder.nameTxt.setText(users.get(position).getUsers_name());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               FragmentTransaction fragmentTransaction = ((FragmentActivity)v.getContext()).getSupportFragmentManager().beginTransaction();
               Fragment fragment = new FriendProfileFragment().newInstance(users.get(position).getUsers_email());
               fragmentTransaction.replace(R.id.main_frame,fragment);
               fragmentTransaction.addToBackStack(null);
               fragmentTransaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTxt, emailTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.useradapter_name);
            emailTxt = itemView.findViewById(R.id.useradapter_email);
        }
    }
}
