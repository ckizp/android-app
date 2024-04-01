package uqac.dim.eventmatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import uqac.dim.eventmatch.models.User;

public class CustomBaseAdapterUser extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List<User> userlist;
    FirebaseFirestore db;

    public CustomBaseAdapterUser(Context ctx, List<User> l){
        context = ctx;
        userlist = l;
        inflater = LayoutInflater.from(ctx);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public int getCount() {
        return userlist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User current_user = userlist.get(position);

        convertView = inflater.inflate(R.layout.activity_user_cutom_list_view,null);
        TextView txtViewNom = (TextView) convertView.findViewById(R.id.list_user);

        txtViewNom.setText(current_user.getEmail());

        return convertView;
    }
}
