package joroze.com.roomifer;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class GroupContentActivity extends AppCompatActivity {

    // TODO: Passing objects/classes through activites:
    // http://stackoverflow.com/questions/2906925/how-do-i-pass-an-object-from-one-activity-to-another-on-android
    private static final String TAG = "SignInActivity";

    private final static DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();

    FirebaseUser mCurrentUser;
    User clientUser;

    StringBuilder snackbarAddMemberResultMsg;


    private String group_id;
    private String group_name;
    StringBuilder groupTitle;

    Fragment groupSectionFragment;

    MaterialSearchView searchView;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter userRecyclerViewAdapter;
    private String queryUserName;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Bundle bundle = new Bundle();
            bundle.putString("group_id", group_id );



            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();

            switch (item.getItemId()) {
                case R.id.group_navigation_settings:
                    groupSectionFragment = new GroupSettingsFragment();
                    groupSectionFragment.setArguments(bundle);
                    transaction.replace(R.id.contentframelayout, groupSectionFragment);
                    transaction.commit();
                    return true;
                case R.id.group_navigation_list:
                    groupSectionFragment = new GroupTasksListFragment();
                    groupSectionFragment.setArguments(bundle);
                    transaction.replace(R.id.contentframelayout, groupSectionFragment);
                    transaction.commit();
                    return true;
                case R.id.group_navigation_notifications:
                    groupSectionFragment = new GroupNotificationsFragment();
                    groupSectionFragment.setArguments(bundle);
                    transaction.replace(R.id.contentframelayout, groupSectionFragment);
                    transaction.commit();

                    return true;
            }



            return false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_content);


        Toolbar toolbar = (Toolbar)findViewById(R.id.searchToolBar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mCurrentUser == null) {
            Log.d(TAG, "USER SIGNED OUT AND DOES NOT EXIST HERE, RETURNING TO MAIN ACTIVITY");
            finish();
        }


        group_id = getIntent().getStringExtra("group_id");
        group_name = getIntent().getStringExtra("group_name");


        groupTitle = new StringBuilder("Add Members");

        getSupportActionBar().setTitle(groupTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setSelectedItemId(R.id.group_navigation_list);


        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("group_id", group_id );
        groupSectionFragment = new GroupTasksListFragment();
        groupSectionFragment.setArguments(bundle);
        transaction.replace(R.id.contentframelayout, groupSectionFragment);
        transaction.commit();


        setupSearchBar();

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_items,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void setupSearchBar()
    {
        recyclerView = (RecyclerView) findViewById(R.id.listviewUser);



        searchView = (MaterialSearchView)findViewById(R.id.searchView);


        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction()
                        .hide(groupSectionFragment)
                        .commit();
            }

            @Override
            public void onSearchViewClosed() {

                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction()
                        .show(groupSectionFragment)
                        .commit();
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText != null && !newText.isEmpty())
                {
                    queryUserName = newText.toLowerCase();
                }
                else
                {
                    queryUserName = "";
                }


                Query mUserQuery = mDatabaseRef.child("users").orderByChild("displayName_lowercase").equalTo(queryUserName);

                userRecyclerViewAdapter = new FirebaseRecyclerAdapter<User, SearchUserHolder>(User.class, R.layout.user_search_item, SearchUserHolder.class, mUserQuery) {
                    @Override
                    protected void populateViewHolder(SearchUserHolder viewHolder, final User user, int position) {

                        Log.d(TAG, "Found USER: \"" + user.getDisplayName() + "\" with ID: " + user.getFb_uid());

                        viewHolder.setUserName(user.getDisplayName());
                        viewHolder.setUserTaskPoints(user.getTaskPoints());
                        viewHolder.setUserProfileImg(user.getProfilePictureUrl());

                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // When a user item is clicked in the list

                                if (user.getFb_uid().equals(mCurrentUser.getUid()))
                                {
                                    return;
                                }

                                snackbarAddMemberResultMsg = new StringBuilder("Added \"");
                                snackbarAddMemberResultMsg.append(user.getDisplayName());
                                snackbarAddMemberResultMsg.append("\" to ");
                                snackbarAddMemberResultMsg.append(group_name);

                                StringBuilder dialogMsg = new StringBuilder("Add \"");
                                dialogMsg.append(user.getDisplayName());
                                dialogMsg.append("\" to this group?");

                                // ========================================================================
                                // Programmatically create an Alert Dialog box "pop-up"
                                // DIALOG LOGIC STARTS HERE ===============================================
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GroupContentActivity.this);

                                // set title of the alert dialog
                                alertDialogBuilder.setTitle("Add Member");

                                // set dialog message
                                alertDialogBuilder

                                        // TODO: Use custom text with HTML design
                                        // http://stackoverflow.com/questions/9935692/how-to-set-part-of-text-to-bold-when-using-alertdialog-setmessage-in-android
                                        // Html.fromHtml(String source, int flags)
                                        .setMessage(dialogMsg)
                                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {




                                                // Add the selected user's id to the group index
                                                mDatabaseRef.child("groups").child(group_id).child("members").child(user.getFb_uid()).setValue(true);

                                                // Go to the added user's index and add the current group's id
                                                mDatabaseRef.child("users").child(user.getFb_uid()).child("groups").child(group_id).setValue(true);



                                                Snackbar snackbar = Snackbar.make(findViewById(R.id.GroupContentSnackBarView), snackbarAddMemberResultMsg, Snackbar.LENGTH_LONG);
                                                snackbar.show();
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // if this button is clicked, close the dialog box
                                                dialog.cancel();
                                            }
                                        });

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();

                                //DIALOG LOGIC ENDS HERE =============================================================
                                //====================================================================================

                            }
                        });
                    }

                };

                recyclerView.setAdapter(userRecyclerViewAdapter);


                return true;
            }
        });
    }


    @Override
    public void onStop()
    {
        super.onStop();

    }

}
