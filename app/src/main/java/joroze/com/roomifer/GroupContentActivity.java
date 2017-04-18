package joroze.com.roomifer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class GroupContentActivity extends AppCompatActivity {

    // TODO: Passing objects/classes through activites:
    // http://stackoverflow.com/questions/2906925/how-do-i-pass-an-object-from-one-activity-to-another-on-android
    private static final String TAG = "SignInActivity";


    private TextView mTextMessage;


    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    FirebaseUser mCurrentUser;
    User clientUser;

    private String group_id;
    private String group_name;
    private int group_index;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment groupTasksListFragment;

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();

            switch (item.getItemId()) {
                case R.id.group_navigation_settings:
                    mTextMessage.setText(R.string.group_title_settings);
                    transaction.replace(R.id.contentframelayout, new Fragment());
                    transaction.commit();

                    // fragment = new GroupTasksListFragment();
                   // transaction.replace(R.id.contentFragment, fragment);
                    //transaction.commit();
                    return true;
                case R.id.group_navigation_list:
                    //mTextMessage.setText(R.string.group_title_tasks);

                    mTextMessage.setText("");
                    Bundle bundle = new Bundle();
                    bundle.putString("group_id", group_id );
                    bundle.putInt("group_index", group_index);
                    groupTasksListFragment = new GroupTasksListFragment();
                    groupTasksListFragment.setArguments(bundle);
                    transaction.replace(R.id.contentframelayout, groupTasksListFragment);
                    transaction.commit();
                    return true;
                case R.id.group_navigation_notifications:
                    mTextMessage.setText(R.string.group_title_notifications);
                    transaction.replace(R.id.contentframelayout, new Fragment());
                    transaction.commit();
                    //fragment = new GroupTasksListFragment();
                    //transaction.replace(R.id.contentFragment, fragment);
                    //transaction.commit();
                    return true;
            }





            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_content);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mCurrentUser == null) {
            Log.d(TAG, "USER SIGNED OUT AND DOES NOT EXIST HERE, RETURNING TO MAIN ACTIVITY");
            finish();
        }


        group_id = getIntent().getStringExtra("group_id");
        group_name = getIntent().getStringExtra("group_name");
        group_index = getIntent().getIntExtra("group_index", -1);





        StringBuilder groupTitle = new StringBuilder("Group: \"");
        groupTitle.append(group_name);
        groupTitle.append("\"");




        getSupportActionBar().setTitle(groupTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setSelectedItemId(R.id.group_navigation_list);

        Fragment groupTasksListFragment;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("group_id", group_id );
        bundle.putInt("group_index", group_index);
        groupTasksListFragment = new GroupTasksListFragment();
        groupTasksListFragment.setArguments(bundle);
        transaction.replace(R.id.contentframelayout, groupTasksListFragment);
        transaction.commit();

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }








}
