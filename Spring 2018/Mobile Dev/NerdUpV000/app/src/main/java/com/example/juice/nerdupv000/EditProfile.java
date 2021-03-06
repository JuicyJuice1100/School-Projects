package com.example.juice.nerdupv000;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfile extends BaseActivity implements PopupMenu.OnMenuItemClickListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_GALLERY = 0;

    private ImageView profilePic;
    private TextView username, quickInfo, bio, mains, secondaries;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference, userProfileReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ValueEventListener profileListener;
    private String name, email, notes;
    private Uri photoUrl;
    private boolean isGoogleSignIn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);


        auth = FirebaseAuth.getInstance();

        username = findViewById(R.id.username);
        profilePic = findViewById(R.id.profilePic);
        quickInfo = findViewById(R.id.quickInfo);
        bio = findViewById(R.id.bio);
        mains = findViewById(R.id.mains);
        secondaries = findViewById(R.id.secondaries);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        notes = getIntent().getStringExtra("notes");


        getListeners();

        if(savedInstanceState == null){
            isGoogleSignIn = getIntent().getBooleanExtra("isGoogleSignIn", false);
            getData();
        } else {
            isGoogleSignIn = savedInstanceState.getBoolean("isGoogleSignIn");
            name = savedInstanceState.getString("name");
            email = savedInstanceState.getString("email");
            notes = savedInstanceState.getString("notes");
            if(savedInstanceState.getByteArray("profilePic") != null){
                Glide.with(getApplicationContext())
                        .load(savedInstanceState.getByteArray("profilePic"))
                        .apply(new RequestOptions()
                                .circleCrop())
                        .into(profilePic);
            } else {
                FirebaseUser user = auth.getCurrentUser();
                photoUrl = user.getPhotoUrl();
                Glide.with(getApplicationContext())
                        .load(photoUrl)
                        .apply(new RequestOptions()
                                .circleCrop())
                        .into(profilePic);
                if(profilePic.getDrawable() == null){
                    profilePic.setImageDrawable(getDrawable(R.drawable.ic_add_a_photo_white_24px));
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle){
        if(!checkProfilePic()){
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Bitmap bitmap = ((BitmapDrawable) profilePic.getDrawable()).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            bundle.putByteArray("profilePic", output.toByteArray());
        }
        bundle.putBoolean("isGoogleSignIn", isGoogleSignIn);
        bundle.putString("name", name);
        bundle.putString("email", email);
        bundle.putString("notes", notes);
        super.onSaveInstanceState(bundle);
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
            if(!checkProfilePic())
                uploadPicture();
            updateProfile();
            updateAuth();
        } else if (id == R.id.cancel){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean checkProfilePic(){
        Drawable addPic = getDrawable(R.drawable.ic_add_a_photo_white_24px);
        Drawable currentPic = profilePic.getDrawable();
        Bitmap addPicBitmap = Bitmap.createBitmap(addPic.getIntrinsicWidth(), addPic.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Bitmap currentPicBitmap = Bitmap.createBitmap(currentPic.getIntrinsicWidth(), currentPic.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        return addPicBitmap.getByteCount() == currentPicBitmap.getByteCount();
    }

    public void updateProfile(){
        userProfileReference = FirebaseDatabase.getInstance().getReference().child("userProfiles");
        UserProfile userProfile = new UserProfile(username.getText().toString() ,bio.getText().toString(), quickInfo.getText().toString(),
                mains.getText().toString(), secondaries.getText().toString(), notes);
        Map<String, Object> profileValues = userProfile.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + name + "/", profileValues);

        userProfileReference.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateAuth();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("upload", "Error updating profile");
                Toast.makeText(EditProfile.this, "Error updating profile.  Please Try again.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void uploadPicture(){
        StorageReference profilePicRef = storageReference.child("profilePictures/"+name);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) profilePic.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        profilePicRef.putBytes(output.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                updateProfile();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("upload", "Error uploading picture");
                Toast.makeText(EditProfile.this, "Error updating profile.  Please Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateAuth(){
        StorageReference profilePicRef = storageReference.child("profilePictures/"+name);
        final FirebaseUser user = auth.getCurrentUser();
        if(!checkProfilePic()){
            profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username.getText().toString())
                            .setPhotoUri(uri)
                            .build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(EditProfile.this, "Updated Profile", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                    else {
                                        Log.i("upload", "Error updating auth");
                                        Toast.makeText(EditProfile.this, "Error updating profile.  Please Try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("upload", "unable to get url");
                }
            });
        } else {
            profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username.getText().toString())
                            .build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(EditProfile.this, "Updated Profile", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                    else {
                                        Log.i("upload", "Error updating auth");
                                        Toast.makeText(EditProfile.this, "Error updating profile.  Please Try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("upload", "unable to get url");
                }
            });
        }
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
                getImageFromAlbum();
                return true;
            case R.id.takepic:
                dispatchTakePictureIntent();
                return true;
            default:
                return false;
        }
    }

    private void getImageFromAlbum(){
        try{
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, REQUEST_GALLERY);
        }catch(Exception exp){
            Log.i("Error",exp.toString());
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Glide.with(this)
                    .load(imageBitmap)
                    .apply(new RequestOptions()
                            .circleCrop())
                    .into(profilePic);
        } else if (requestCode ==  REQUEST_GALLERY && resultCode == RESULT_OK){
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                Glide.with(this)
                        .load(selectedImage)
                        .apply(new RequestOptions()
                                .circleCrop())
                        .into(profilePic);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStop(){
        super.onStop();

        if(profileListener != null){
            userProfileReference.removeEventListener(profileListener);
        }
    }

    public void getData(){
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            if(user.getDisplayName() != null)
                name = user.getDisplayName();
            if(user.getEmail() != null)
                email = user.getEmail();
            if(user.getPhotoUrl() != null)
                photoUrl = user.getPhotoUrl();
        }

        username.setText(name);

        if(photoUrl != null){
            Glide.with(getApplicationContext())
                    .load(photoUrl)
                    .apply(new RequestOptions()
                            .circleCrop())
                    .into(profilePic);
        }
        userProfileReference = FirebaseDatabase.getInstance().getReference().child("userProfiles").child(name);

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

        userProfileReference.addListenerForSingleValueEvent(dataListener);

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
