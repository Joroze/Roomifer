package joroze.com.roomifer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class GroupTasksActivity extends AppCompatActivity implements GroupTasksListFragment.CreateTaskDialogListener{

    // TODO: Passing objects/classes through activites:
    // http://stackoverflow.com/questions/2906925/how-do-i-pass-an-object-from-one-activity-to-another-on-android


    private TextView mTextMessage;

    private String group_id;

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

                    Bundle bundle = new Bundle();
                    bundle.putString("message", group_id );
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
        setContentView(R.layout.activity_group_tasks);



        group_id =  getIntent().getStringExtra("groupid");


        StringBuilder groupTitle = new StringBuilder("Group: \"");
        groupTitle.append(group_id);
        groupTitle.append("\"");




        getSupportActionBar().setTitle(groupTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setSelectedItemId(R.id.group_navigation_list);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onDialogPositiveClick(GroupTasksListFragment dialog) {

    }

    @Override
    public void onDialogNegativeClick(GroupTasksListFragment dialog) {

    }
}
