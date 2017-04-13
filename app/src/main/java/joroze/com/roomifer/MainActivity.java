package joroze.com.roomifer;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static joroze.com.roomifer.User.clientUser;

public class MainActivity extends AppCompatActivity
        implements GroupListFragment.OnListFragmentInteractionListener, GroupListFragment.UpdateGroupListInterface, GroupListFragment.MyFragInterface, NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, CreateGroupDialogFragment.CreateGroupDialogListener {

    private static final String TAG = "SignInActivity";

    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


    public static final int GROUP_COUNT_MAX_LIMIT = 3;


    private GroupListFragment.OnListFragmentInteractionListener mListener;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser mCurrentUser;

    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

                    clientUser = new User(mCurrentUser.getUid(), mCurrentUser.getDisplayName(), mCurrentUser.getEmail(), mCurrentUser.getPhotoUrl().toString());
                    fbWriteNewUser(clientUser);

                    updateUserProfile();

                } else {
                    Log.d(TAG, "User is currently signed out");
                }
            }
        };

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
            userNameView.setText(mCurrentUser.getDisplayName());

        if (mCurrentUser.getEmail() != null)
            emailView.setText(mCurrentUser.getEmail());

        if (mCurrentUser.getPhotoUrl().toString() != null)
            Glide.with(this).load(mCurrentUser.getPhotoUrl()).into(profileImageView);

        showSnackbar(1);

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
                                    public void onComplete(@NonNull Task<Void> task) {
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
            if (mCurrentUser != null)
                showSnackbar(1);
        }
    }

    public void showCreateGroupDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new CreateGroupDialogFragment();
        dialog.show(getSupportFragmentManager(), "CreateGroupDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText mEdit = (EditText) dialog.getDialog().findViewById(R.id.createGroupTextEntry);
        fbWriteNewGroup(mEdit.getText().toString(), clientUser);
        showGroupList();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }


    // This function listens to click events of items in the fragment, EXCLUDING the delete button.
    @Override
    public void onListFragmentInteraction(Group item) {

        Log.d(TAG, "TESTING FRAGMENT INTERACTION... " + item.getGroupName());

    }


    @Override
    public void updateGroupList() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //find the fragment by View or Tag
        GroupListFragment mGroupListFrag = (GroupListFragment) fragmentManager.findFragmentById(R.id.grouplistfragment);

        mGroupListFrag.updateGroupList(clientUser);
    }

    @Override
    public void showGroupList() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //find the fragment by View or Tag
        GroupListFragment myFrag = (GroupListFragment) fragmentManager.findFragmentById(R.id.grouplistfragment);
        fragmentTransaction.show(myFrag);
        fragmentTransaction.commit();
    }

    @Override
    public void hideGroupList() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //find the fragment by View or Tag
        GroupListFragment myFrag = (GroupListFragment) fragmentManager.findFragmentById(R.id.grouplistfragment);
        fragmentTransaction.hide(myFrag);
        fragmentTransaction.commit();
    }


    // TODO: If groups that belong to a user exist, but the user has not been created on the database yet, assign those groups to the user on creation
    public void fbWriteNewUser(final User user) {

        if (user.getFb_uid() == null) {
            throw new IllegalArgumentException("ERROR: User must have a g_uid (a Google ID) assigned!");
        }

        DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(user.getFb_uid());


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
                    clientUser = new User(user.getFb_uid(), user.getDisplayName(), user.getEmail(), user.getProfilePictureUrl());
                }

                if (clientUser.getGroupCount() > 0)
                {
                    showGroupList();

                }

                updateGroupList();

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


    public void fbWriteNewGroup(final String groupName, final User user) {

        if (user.getFb_uid() == null) {
            throw new IllegalArgumentException("ERROR: User must have a g_uid (a Google ID) assigned!");
        }

        DatabaseReference mGroupReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(user.getFb_uid());


        ValueEventListener groupListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                if (dataSnapshot.exists()) {

                    // if this user exists, create a user with existing information from Firebase database
                    int checkGroupCount = dataSnapshot.getValue(User.class).getGroupCount();

                    // If user attempts to create more groups than 3, don't proceed
                    if (checkGroupCount < 0 || checkGroupCount > GROUP_COUNT_MAX_LIMIT - 1) {
                        Log.d(TAG, "User has reached the max amount of groups to be in: " + GROUP_COUNT_MAX_LIMIT);
                        // On Failure
                        showSnackbar(-2);
                        return;
                    }
                } else {
                    // otherwise, create a new user with default information
                    //fbWriteNewUser(user);
                }

                String groupKey = mDatabase.child("groups").push().getKey();

                Group group = new Group(groupKey, groupName, user);

                updateGroupList();

                Map<String, Object> groupValues = group.toMap();
                Map<String, Object> userValues = user.toMap();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/groups/" + groupKey, groupValues);
                childUpdates.put("/users/" + user.getFb_uid(), userValues);


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

    public void fbDeleteGroup(final String groupName, final User user) {



    }

    public static void fbDeleteAccount(User user) {
        Map<String, Object> childUpdates = new HashMap<>();

        for (Group group : user.getGroups()) {

            if (user.getDisplayName().equals(group.getAuthor())) {
                childUpdates.put("/groups/" + group.getId(), null);

                //TODO Find a way to remove each user from the group that was deleted...
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

}
