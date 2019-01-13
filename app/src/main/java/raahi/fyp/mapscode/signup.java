package raahi.fyp.mapscode;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import raahi.fyp.mapscode.Model.User;

public class signup extends AppCompatActivity {

    ImageView NicImage;
    private static final int CAMERA_REQUEST_CODE = 1;
    private StorageReference mStorage;
    android.support.design.widget.TextInputEditText Name,Passwords,NIC,Phone,Emails;
    private FirebaseAuth mauth;
    DatabaseReference table_user;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        NicImage =(ImageView)findViewById(R.id.nicimage);
        mStorage = FirebaseStorage.getInstance().getReference();
        mauth=FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");
        Name = findViewById(R.id.reg_name);
        Passwords = findViewById(R.id.reg_password);
        NIC = findViewById(R.id.reg_NIC);
        Phone = findViewById(R.id.reg_phone);
        Emails = findViewById(R.id.reg_email);
    }

    public void Customer(View view) {
        Intent customerintent = new Intent(signup.this,signup.class);
        startActivity(customerintent);
    }

    public void raahi(View view) {
        Intent raahiintent = new Intent(signup.this,raahisignup.class);
        startActivity(raahiintent);
    }

    public void camera(View view) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent ,CAMERA_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK)
        {
            Uri uri =data.getData();
            StorageReference filepath = mStorage.child("Photos").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(signup.this, "upload", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void register(View view) {
        mauth.createUserWithEmailAndPassword(Emails.getText().toString(),Passwords.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    final FirebaseUser user = mauth.getCurrentUser();
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(user.getUid()).exists())
                            {

                                Toast.makeText(signup.this, "User already exist! ", Toast.LENGTH_SHORT).show();

                            } else {
                                User userInfo = new User(Name.getText().toString(),
                                        Passwords.getText().toString(),
                                        Emails.getText().toString(),
                                        NIC.getText().toString(),
                                        Phone.getText().toString());

                                table_user.child(user.getUid()).setValue(userInfo);
                                Toast.makeText(signup.this, "Sign up successful !", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(signup.this, "Failed", Toast.LENGTH_SHORT).show();
                }

            }

        });
        Intent signupintent = new Intent(signup.this,MapsActivity.class);
        startActivity(signupintent);

    }


}
