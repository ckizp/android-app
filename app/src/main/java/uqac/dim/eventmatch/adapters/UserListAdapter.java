package uqac.dim.eventmatch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.eventmatch.R;
import uqac.dim.eventmatch.models.User;
import uqac.dim.eventmatch.ui.activities.SignUpActivity;
import uqac.dim.eventmatch.ui.fragments.main.EventDetailsFragment;
import uqac.dim.eventmatch.ui.fragments.profile.EditEventFragment;

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
    private ArrayList<User> userList;
    private FirebaseFirestore database;
    private Fragment fragment;
    private String fragtype;

    /* *************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public UserListAdapter(Context ctx, ArrayList<User> userList, Fragment frag, String fragmentype){
        context = ctx;
        this.userList = userList;
        inflater = LayoutInflater.from(ctx);
        database = FirebaseFirestore.getInstance();
        if (fragmentype.equals("EditEventFragment"))
        {
            fragment = (EditEventFragment) frag;
        }
        else
        {
            fragment = (EventDetailsFragment) frag;
        }
        fragtype = fragmentype;

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
        convertView = inflater.inflate(R.layout.activity_user_cutom_list_view,null);

        User current_user = userList.get(position);

        TextView txtViewNom = (TextView) convertView.findViewById(R.id.listuser_name);
        TextView txtViewVous = (TextView) convertView.findViewById(R.id.listuser_vous);
        Button buttonDeleteUser = (Button) convertView.findViewById(R.id.listuser_deletebutton);


        txtViewVous.setVisibility(View.INVISIBLE);
        buttonDeleteUser.setVisibility(View.INVISIBLE);
        if (fragtype.equals("EditEventFragment"))
        {
            if (position == 0)
            {
                txtViewVous.setVisibility(View.VISIBLE);
            }
            else
            {
                buttonDeleteUser.setVisibility(View.VISIBLE);
                EditEventFragment editeventfrag = (EditEventFragment)fragment;
                buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editeventfrag.deleteuser(position,current_user.getUsername());
                    }
                });
            }
        }
        else
        {
            //TODO : Check qui on est pour mettre le vous sur le nom adequat
        }


        txtViewNom.setText(current_user.getUsername());

        return convertView;
    }
}