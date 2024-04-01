package uqac.dim.eventmatch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.models.User;

/**
 *
 * @version 1.0 1 Apr 2024
 * @author Kyllian Hot
 */
public class UserListAdapter extends BaseAdapter {
    /* *************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

    private Context context;
    private LayoutInflater inflater;
    private List<User> userList;
    private FirebaseFirestore database;

    /* *************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public UserListAdapter(Context ctx, List<User> userList){
        context = ctx;
        this.userList = userList;
        inflater = LayoutInflater.from(ctx);
        database = FirebaseFirestore.getInstance();
    }

    /* *************************************************************************
     *                                                                         *
     * Methods                                                                 *
     *                                                                         *
     **************************************************************************/

    @Override
    public int getCount() {
        return userList.size();
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
        User current_user = userList.get(position);

        convertView = inflater.inflate(R.layout.activity_user_cutom_list_view,null);
        TextView txtViewNom = (TextView) convertView.findViewById(R.id.list_user);

        txtViewNom.setText(current_user.getEmail());

        return convertView;
    }
}