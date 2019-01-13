package raahi.fyp.mapscode;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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

import raahi.fyp.mapscode.Model.Raahi;
import raahi.fyp.mapscode.Model.User;

public class raahisignup extends AppCompatActivity {

    ImageView NicImage;
    private static final int CAMERA_REQUEST_CODE = 1;
    private StorageReference mStorage;
    android.support.design.widget.TextInputEditText RName,RPasswords,RNIC,RPhone,REmails,RVehicle;
    private FirebaseAuth mAuth;
    DatabaseReference table_user;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raahisignup);
        NicImage =(ImageView)findViewById(R.id.raahi_nicimage);
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("Raahi");
        RName = findViewById(R.id.raahi_reg_name);
        RPasswords = findViewById(R.id.raahi_reg_password);
        RNIC = findViewById(R.id.raahi_reg_NIC);
        RPhone = findViewById(R.id.raahi_reg_phone);
        REmails = findViewById(R.id.raahi_reg_email);
        RVehicle =findViewById(R.id.raahi_reg_vehicle);

    }

    public void Customer(View view) {
        Intent raahiintent = new Intent(raahisignup.this,signup.class);
        startActivity(raahiintent);
    }

    public void raahi(View view) {
        Intent raahiintent = new Intent(raahisignup.this,raahisignup.class);
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
                    Toast.makeText(raahisignup.this, "upload", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void register(View view) {
        mAuth.createUserWithEmailAndPassword(REmails.getText().toString(),RPasswords.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    final FirebaseUser user = mAuth.getCurrentUser();
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(user.getUid()).exists())
                            {

                                Toast.makeText(raahisignup.this, "User already exist! ", Toast.LENGTH_SHORT).show();

                            } else {
                                Raahi userInfo = new Raahi(RName.getText().toString(),
                                        RPasswords.getText().toString(),
                                        REmails.getText().toString(),
                                        RNIC.getText().toString(),
                                        RPhone.getText().toString(),
                                        RVehicle.getText().toString());

                                table_user.child(user.getUid()).setValue(userInfo);
                                Toast.makeText(raahisignup.this, "Sign up successful !", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(raahisignup.this, "Failed", Toast.LENGTH_SHORT).show();
                }

            }

        });
        Intent signupintent = new Intent(raahisignup.this,MapsActivity.class);
        startActivity(signupintent);

    }
}
