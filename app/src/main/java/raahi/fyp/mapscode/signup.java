package raahi.fyp.mapscode;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

import raahi.fyp.mapscode.Model.User;

public class signup extends AppCompatActivity {

    private String mVerificationId;
    ImageView NicImage;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Uri Imageuri;
    private StorageReference mStorage;
    EditText editTextCode;
    android.support.design.widget.TextInputEditText Name,Passwords,NIC,Phone,Emails;
    private FirebaseAuth mauth;
    DatabaseReference table_user;
    FirebaseDatabase database;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        NicImage = findViewById(R.id.nicimage);
        mStorage = FirebaseStorage.getInstance().getReference();
        mauth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");
        Name = findViewById(R.id.reg_name);
        Passwords = findViewById(R.id.reg_password);
        NIC = findViewById(R.id.reg_NIC);
        Phone = findViewById(R.id.reg_phone);
        Emails = findViewById(R.id.reg_email);
        editTextCode=findViewById(R.id.smsc);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                if (code != null) {
                    editTextCode.setText(code);
                    //verifying the code
                    verifyVerificationCode(code);
                }
            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(signup.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerificationId = s;
            }
        };

    }

    public void CustomertoCustomer(View view) {
        Intent customerintent = new Intent(signup.this,signup.class);
        startActivity(customerintent);
    }

    public void customertoraahi(View view) {
        Intent raahiintent = new Intent(signup.this,raahisignup.class);
        startActivity(raahiintent);
    }

    public void cameras(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    private void startPosting(){
        StorageReference filepath = mStorage.child("Photos").child(Imageuri.getLastPathSegment());
        filepath.putFile(Imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Imageuri  = getImageUri(getApplicationContext(), imageBitmap);


            NicImage.setImageBitmap(imageBitmap);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage (inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void register(View view) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber( Phone.getText().toString(),120,TimeUnit.SECONDS,this,mCallbacks);

        mauth.createUserWithEmailAndPassword(Emails.getText().toString(),Passwords.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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
                                startPosting();

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
    }
    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mauth.signInWithCredential(credential)
                .addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                        }
                    }
                });
    }


    public void checkcode(View view) {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                if (code != null) {
                    editTextCode.setText(code);
                    //verifying the code
                    verifyVerificationCode(code);
                }
            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(signup.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerificationId = s;
            }
        };
        Intent signupintent = new Intent(signup.this,MapsActivity.class);
        startActivity(signupintent);
        finish();
    }
}
