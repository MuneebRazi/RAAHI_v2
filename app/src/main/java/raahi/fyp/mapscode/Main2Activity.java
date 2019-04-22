package raahi.fyp.mapscode;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import raahi.fyp.mapscode.Model.Order;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class Main2Activity extends AppCompatActivity {

    EditText rname,raddress,itemname;
    ImageView itemimage;
    private StorageReference mStorage;
    Uri Imageuri;
    DatabaseReference table_user;
    FirebaseDatabase database;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //table_user = database.getReference("Order");
        //Log.d("Order","kosdsdsd");
        rname = (EditText)findViewById(R.id.rName);
        raddress = (EditText)findViewById(R.id.rAddress);
        itemname = (EditText)findViewById(R.id.iName);
        itemimage = (ImageView)findViewById(R.id.img);
    }

    public void cam(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void ship(View view) {
        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Order orderInfo = new Order(rname.getText().toString(),raddress.getText().toString(),itemname.getText().toString());
                    table_user.child("Order").setValue(orderInfo);
                    Toast.makeText(Main2Activity.this, "Sign up successful !", Toast.LENGTH_SHORT).show();
                    startPosting();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void startPosting(){
        StorageReference filepath = mStorage.child("Photos").child(Imageuri.getLastPathSegment());
        filepath.putFile(Imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Imageuri  = getImageUri(getApplicationContext(), imageBitmap);


            itemimage.setImageBitmap(imageBitmap);
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage (inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
