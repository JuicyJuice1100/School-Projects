package com.example.juice.nerdupv000;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfile extends BaseActivity implements PopupMenu.OnMenuItemClickListener {
    private ImageView profilePic;
    private TextView username, quickInfo, bio, mains, secondaries;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private ValueEventListener profileListener;
    private String name, email;
    private Uri photoUrl;
    private boolean isGoogleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);

        isGoogleSignIn = getIntent().getBooleanExtra("isGoogleSignIn", false);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart(){
        super.onStart();
        username = findViewById(R.id.username);
        profilePic = findViewById(R.id.profilePic);
        quickInfo = findViewById(R.id.quickInfo);
        bio = findViewById(R.id.bio);
        mains = findViewById(R.id.mains);
        secondaries = findViewById(R.id.secondaries);
        getListeners();
        getData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editprofile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.confirm){
            Log.i("actionBar", "confirm");
            finish();
        } else if (id == R.id.cancel){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.profilepicture);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.uploadpic:
                Log.i("menu", "upload picture");
                return true;
            case R.id.takepic:
                Log.i("menu", "take picture");
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onStop(){
        super.onStop();

        if(profileListener != null){
            database.removeEventListener(profileListener);
        }
    }

    public void getData(){
        if (isGoogleSignIn) {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                if(acct.getDisplayName() != null)
                    name = acct.getDisplayName();
                if(acct.getEmail() != null)
                    email = acct.getEmail();
                if(acct.getPhotoUrl() != null)
                    photoUrl = acct.getPhotoUrl();
            }
        }else {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                if(user.getDisplayName() != null)
                    name = user.getDisplayName();
                if(user.getEmail() != null)
                    email = user.getEmail();
                if(user.getPhotoUrl() != null)
                    photoUrl = user.getPhotoUrl();
            }
        }
        username.setText(name);

        if(photoUrl != null){
            Glide.with(this)
                    .load(photoUrl)
                    .apply(new RequestOptions()
                            .circleCrop())
                    .into(profilePic);
        }
        database = FirebaseDatabase.getInstance().getReference().child("userProfiles").child(encodeUserEmail(email));

        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);

                if(userProfile != null){
                    if(userProfile.bio != null)
                        bio.setText(userProfile.bio);
                    if(userProfile.quickInfo != null)
                        quickInfo.setText(userProfile.quickInfo);
                    if(userProfile.mains != null)
                        mains.setText(userProfile.mains);
                    if(userProfile.secondaries != null)
                        secondaries.setText(userProfile.secondaries);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditProfile.this, "Unable to retrieve profile data.", Toast.LENGTH_SHORT).show();
            }
        };

        database.addListenerForSingleValueEvent(dataListener);

        profileListener = dataListener;
    }

    public void getListeners(){
        profilePic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showMenu(profilePic);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }
}
