package joroze.com.roomifer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class GroupTasksActivity extends AppCompatActivity {

    // TODO: Passing objects/classes through activites:
    // http://stackoverflow.com/questions/2906925/how-do-i-pass-an-object-from-one-activity-to-another-on-android


    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.group_navigation_settings:
                    mTextMessage.setText(R.string.group_title_settings);
                    return true;
                case R.id.group_navigation_list:
                    mTextMessage.setText(R.string.title_activity_group_tasks);
                    return true;
                case R.id.group_navigation_notifications:
                    mTextMessage.setText(R.string.group_title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_tasks);

        StringBuilder groupTitle = new StringBuilder("Group: \"");
        groupTitle.append(getIntent().getStringExtra("groupname"));
        groupTitle.append("\"");

        getSupportActionBar().setTitle(groupTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

}
