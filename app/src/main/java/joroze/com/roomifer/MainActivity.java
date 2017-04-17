package joroze.com.roomifer;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        CreateGroupDialogFragment.CreateGroupDialogListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "SignInActivity";

    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


    public static final int GROUP_COUNT_MAX_LIMIT = 3;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser mCurrentUser;
    User clientUser;

    GoogleApiClient mGoogleApiClient;


    boolean firstSignInCheck = false;



    boolean fullGroupDelete = false;



    RecyclerView.Adapter groupRecyclerViewAdapter;
    RecyclerView recyclerView;
    private ItemTouchHelper itemTouchHelper;

    Group selectedGroup;
    int selectedGroupPosition;
    Map<String, Object> leaveGroupChildUpdates;

    StringBuilder snackbarDeleteResultMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // TODO: Fix this to work properly. What's needed: Signed-In snackbar when logged in from LoginActivity, and also when resumed, ONLY!
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            firstSignInCheck = getIntent().getBooleanExtra("firstsignin", false);
        }

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]



        itemTouchHelper = new ItemTouchHelper(simpleCallbackItemTouchHelper);

        recyclerView = (RecyclerView)findViewById(R.id.recyclegrouplist);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    // TODO: Simplify some stupid logic here!

                    Log.d(TAG, "User signed in... Collecting FireBase currentUser info");
                    // Currently signed in
                    mCurrentUser = user;

                    initializeClientUser();

                    showSnackbar(1);

                    groupRecyclerViewAdapter = new FirebaseRecyclerAdapter<Group, GroupHolder>(Group.class, R.layout.group_list_item, GroupHolder.class, mDatabase.child("users").child(mCurrentUser.getUid()).child("groups")) {
                        @Override
                        protected void populateViewHolder(GroupHolder viewHolder, final Group group, int position) {

                            //clientUser.addToGroup(group);

                            viewHolder.setCardGroupName(group.getGroupName());
                            viewHolder.setGroupMemberCount(group.getMembers().size());
                            viewHolder.setCardAuthorProfileImg(group.getAuthorProfilePictureUrl());

                            Log.d(TAG, "UPDATE FOUND: " + group.getAuthor() + group.getGroupName());


                            viewHolder.itemView.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view) {

                                    Intent nextActivity = new Intent(getApplicationContext(), GroupTasksActivity.class);
                                    nextActivity.putExtra("groupid", group.getId());
                                    startActivity(nextActivity);
                                }
                            });

                        }



                    };

                    recyclerView.setAdapter(groupRecyclerViewAdapter);
                    itemTouchHelper.attachToRecyclerView(recyclerView);

                    //updateUserProfile();


                } else {
                    Log.d(TAG, "User is currently signed out");
                }
            }
        };

        //itemTouchHelper.attachToRecyclerView(recyclerView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateGroupDialog();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // User should not need to go to the previous activity this way
            //super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // TODO: When I can generate profile URL's to share, shorten it first using the Google-Shorten-Url API
        String message = "Roomifer is awesome!";
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_menu) {
            startActivity(Intent.createChooser(share, "Share using Messenger"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_signout) {
            signOut();
        } else if (id == R.id.nav_deleteAccount) {
            revokeAccess();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection FAILED!");
    }

    public void updateUserProfile() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);

        TextView userNameView = (TextView) hView.findViewById(R.id.usernameView);
        TextView emailView = (TextView) hView.findViewById(R.id.emailView);
        ImageView profileImageView = (ImageView) hView.findViewById(R.id.profileImageView);

        if (clientUser.getDisplayName() != null)
            userNameView.setText(clientUser.getDisplayName());

        if (clientUser.getEmail() != null)
            emailView.setText(clientUser.getEmail());

        if (clientUser.getProfilePictureUrl() != "") {
            Glide.with(this).load(clientUser.getProfilePictureUrl()).into(profileImageView);
        }
    }

    public void showSnackbar(int resultCode) {
        Snackbar snackbar;
        String snackbarMsg = "";

        if (resultCode > 0) {
            switch (resultCode) {
                case 1:
                    snackbarMsg = "Signed in as " + mCurrentUser.getDisplayName();
                    break;
                case 2:
                    snackbarMsg = "Successfully created your group!";
                    break;
                default:
                    snackbarMsg = "An unknown error has occurred!";
                    break;
            }
        } else if (resultCode < 0) {
            switch (resultCode) {
                case -2:
                    snackbarMsg = "You've reached the max group limit! (" + GROUP_COUNT_MAX_LIMIT + ")";
                    break;
                default:
                    snackbarMsg = "An unknown error has occurred!";
                    break;
            }
        }

        snackbar = Snackbar.make(this.findViewById(R.id.mainSnackBarView), snackbarMsg, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void signOut() {

        // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        // Firebase sign out
                        mAuth.signOut();

                        if (status.isSuccess())
                            Log.d(TAG, "Log Out successful!");
                        else
                            Log.d(TAG, "Log Out failed!");

                        // finish this activity, and go back to the sign-in activity screen
                        finish();
                    }
                });
    }

    public void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        FirebaseUser user = mAuth.getCurrentUser();

                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull com.google.android.gms.tasks.Task task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User account deleted.");
                                        }
                                    }
                                });

                        fbDeleteAccount(clientUser);

                        if (status.isSuccess())
                            Log.d(TAG, "Google Sign-In Revoke successful!");
                        else
                            Log.d(TAG, "Google Sign-In Revoke failed!");

                        // finish this activity, and go back to the sign-in activity screen
                        finish();
                    }
                });
    }

    protected void onStart() {
        super.onStart();
        
        mAuth.addAuthStateListener(mAuthListener);

    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    protected void onResume() {
        super.onResume();

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();

        }


    }

    public void showCreateGroupDialog() {
        // Create an instance of the dialog fragment and show it
        CreateGroupDialogFragment dialog = new CreateGroupDialogFragment();
        dialog.show(getSupportFragmentManager(), "CreateGroupDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText mEdit = (EditText) dialog.getDialog().findViewById(R.id.createGroupTextEntry);
        fbWriteNewGroup(mEdit.getText().toString());
        //showGroupList();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {


    }


    // TODO: If groups that belong to a user exist, but the user has not been created on the database yet, assign those groups to the user on creation
    public void initializeClientUser() {

        DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(mCurrentUser.getUid());

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                //User checkUser = dataSnapshot.getValue(User.class);

                if (dataSnapshot.exists()) {
                    // if this user exists, create a user with existing information from Firebase database
                    Log.d(TAG, dataSnapshot.toString());
                    User savedUser = dataSnapshot.getValue(User.class);
                    clientUser = new User(savedUser.getFb_uid(), savedUser.getDisplayName(), savedUser.getEmail(), savedUser.getProfilePictureUrl(), savedUser.getGroups());
                } else {
                    // otherwise, create a new user with default information
                    Log.d(TAG, "New user detected... Creating new user");

                    String photoUrl;

                    if (mCurrentUser.getPhotoUrl() == null) {
                        photoUrl = "";
                    }
                    else
                        photoUrl = mCurrentUser.getPhotoUrl().toString();

                    clientUser = new User(mCurrentUser.getUid(), mCurrentUser.getDisplayName(), mCurrentUser.getEmail(), photoUrl);
                }

                updateUserProfile();

                if (clientUser.getGroups().size() > 0) {
                    //showGroupList();

                }


                Map<String, Object> userValues = clientUser.toMap();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/users/" + clientUser.getFb_uid(), userValues);

                // update any information to the database
                mDatabase.updateChildren(childUpdates);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        mUserReference.addListenerForSingleValueEvent(userListener);

        mUserReference.removeEventListener(userListener);

    }


    public void fbWriteNewGroup(final String groupName) {

        DatabaseReference mGroupReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(clientUser.getFb_uid());


        ValueEventListener groupListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                // This is here in case someone is deleting user group values from the Firebase Database.
                // This updates the user values on the app to update changes made when a user wants to create
                // a new group
                if (dataSnapshot.child("groups").exists()) {

                    Long checkGroupCount = dataSnapshot.child("groups").getChildrenCount();

                    // If user attempts to create more groups than 3, don't proceed
                    if (checkGroupCount < 0 || checkGroupCount > GROUP_COUNT_MAX_LIMIT - 1) {
                        Log.d(TAG, "User has reached the max amount of groups to be in: " + GROUP_COUNT_MAX_LIMIT);
                        // On Failure
                        showSnackbar(-2);
                        return;
                    }

                    // If the check was passed, we update the user values to the client android app
                    clientUser.getGroups().clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.child("groups").getChildren()) {
                        Group group = postSnapshot.getValue(Group.class);
                        clientUser.getGroups().add(group);

                    }

                }

                else if (!dataSnapshot.child("groups").exists()){
                    Log.d(TAG, "User DOES NOT EXIST, Logging the user out (back to login screen)");

                    if (!dataSnapshot.exists()) {
                        signOut();
                        return;
                    }

                    // Update user client groups
                    clientUser.getGroups().clear();
                }

                String groupKey = mDatabase.child("groups").push().getKey();

                Group group = new Group(groupKey, groupName, clientUser);

                Map<String, Object> groupValues = group.toMap();
                Map<String, Object> userValues = clientUser.toMap();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/groups/" + groupKey, groupValues);
                childUpdates.put("/users/" + clientUser.getFb_uid(), userValues);


                mDatabase.updateChildren(childUpdates);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        mGroupReference.addListenerForSingleValueEvent(groupListener);
        mGroupReference.removeEventListener(groupListener);


        // On Success
        showSnackbar(2);
    }


    public void fbUpdateUserAndGroup()
    {

        Map<String, Object> childUpdates = new HashMap<>();

        for (Group group: clientUser.getGroups()) {

            Map<String, Object> groupValues = group.toMap();
            childUpdates.put("/groups/" + group.getId(), groupValues);

        }

        Map<String, Object> userValues = clientUser.toMap();
        childUpdates.put("/users/" + clientUser.getFb_uid(), userValues);

        mDatabase.updateChildren(childUpdates);



    }

    public static void fbDeleteAccount(User user) {
        Map<String, Object> childUpdates = new HashMap<>();

        for (Group group : user.getGroups()) {

            if (user.getDisplayName().equals(group.getAuthor())) {
                childUpdates.put("/groups/" + group.getId(), null);

                //TODO Find a way to remove each user from the group that was deleted...
                // CLOUD FUNCTION

                // TODO If a user
            }
        }

        childUpdates.put("/users/" + user.getFb_uid(), null);

        /*Group group = new Group(groupId, groupName, user);

        Map<String, Object> groupValues = group.toMap();
        Map<String, Object> userValues = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/groups/" + groupId, groupValues);
        childUpdates.put("/users/" + user.g_uid, userValues);
        */

        mDatabase.updateChildren(childUpdates);
    }





    ItemTouchHelper.SimpleCallback simpleCallbackItemTouchHelper = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {


            // Using StringBuilder for String optimization
            StringBuilder dialogTitle;
            StringBuilder dialogMsg;


            selectedGroupPosition = viewHolder.getAdapterPosition();
            selectedGroup = clientUser.getGroups().get(selectedGroupPosition);
            leaveGroupChildUpdates = new HashMap<>();

            dialogTitle = new StringBuilder("Confirm to Leave");

            dialogMsg = new StringBuilder("Are you sure you want to leave the group: \"");
            dialogMsg.append(selectedGroup.getGroupName());
            dialogMsg.append("?");

            if (selectedGroup.getAuthor_id().equals(clientUser.getFb_uid()))
            {
                fullGroupDelete = true;
                dialogTitle.append(" and Delete");
                dialogMsg.append("\n\n(Since you\'re the owner, the group will also be deleted)");
            }

            snackbarDeleteResultMsg = new StringBuilder("You\'ve");

            // ========================================================================
            // Programmatically create an Alert Dialog box "pop-up"
            // DIALOG LOGIC STARTS HERE ===============================================
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

            // set title of the alert dialog
            alertDialogBuilder.setTitle(dialogTitle);

            // set dialog message
            alertDialogBuilder

                    // TODO: Use custom text with HTML design
                    // http://stackoverflow.com/questions/9935692/how-to-set-part-of-text-to-bold-when-using-alertdialog-setmessage-in-android
                    // Html.fromHtml(String source, int flags)
                    .setMessage(dialogMsg)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            // If clientUser deletes a group that it is the author of, then delete it from database
                            if (fullGroupDelete)
                            {
                                // TODO: MAKE IT SO ANDROID APP CONTINOUSLY CHECKS FOR GROUP CHANGES.... Think about it!
                                leaveGroupChildUpdates.put("/groups/" + selectedGroup.getId(), null);

                                snackbarDeleteResultMsg.append(" deleted the group: \"");
                            }

                            // If clientUser deletes a group that it is not the author of, then remove clientUser from group in the database
                            else {
                                selectedGroup.getMembers().remove(clientUser.getFb_uid());
                                Map<String, Object> groupValues = selectedGroup.toMap();
                                leaveGroupChildUpdates.put("/groups/" + selectedGroup.getId(), groupValues);

                                snackbarDeleteResultMsg.append(" left the group: \"");
                            }

                            snackbarDeleteResultMsg.append(selectedGroup.getGroupName());
                            snackbarDeleteResultMsg.append("\"");

                            // Remove group item from the Recycler List
                            //groupRecyclerViewAdapter.removeGroupItem(selectedTaskPosition);
                            groupRecyclerViewAdapter.notifyItemRemoved(selectedGroupPosition);

                            // Remove group from client user object
                            clientUser.getGroups().remove(selectedGroupPosition);

                            Map<String, Object> userValues = clientUser.toMap();
                            leaveGroupChildUpdates.put("/users/" + clientUser.getFb_uid(), userValues);

                            mDatabase.updateChildren(leaveGroupChildUpdates);

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.mainSnackBarView), snackbarDeleteResultMsg, Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close the dialog box

                            // also reset the view adapter to put back in place the slided row
                            groupRecyclerViewAdapter.notifyDataSetChanged();
                            dialog.cancel();
                        }
                    })
                    .setCancelable(false);

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            //DIALOG LOGIC ENDS HERE =============================================================
            //====================================================================================

        }
    };

}
