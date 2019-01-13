package raahi.fyp.mapscode;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import raahi.fyp.mapscode.Model.User;

public class login extends AppCompatActivity {

    DatabaseReference table_user;
    FirebaseDatabase database;
    private FirebaseAuth mAuth;
    EditText email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.passwords);
        mAuth = FirebaseAuth.getInstance();

        database =FirebaseDatabase.getInstance();
        table_user = database.getReference("User");
    }

    public void Login(View view) {
        mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())
                {
                    final FirebaseUser user = mAuth.getCurrentUser();
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(user.getUid()).exists())
                            {
                                User userinfo = dataSnapshot.child(user.getUid()).getValue(User.class);
                                if (userinfo.getPassword().equals(password.getText().toString()))
                                {

                                    Toast.makeText(login.this, "Sign in Successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(login.this,MapsActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(login.this, "Sign in Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(login.this,"user not exist",Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(login.this, "Failed ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void Signup(View view) {

        Intent signintent = new Intent(login.this,signup.class);
        startActivity(signintent);
    }
}
