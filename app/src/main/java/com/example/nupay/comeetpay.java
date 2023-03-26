package com.example.nupay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.ls.LSParser;

import java.util.HashMap;
import java.io.*;
public class comeetpay extends AppCompatActivity {
    public  boolean status;

    TextView  messageview;



    @Override
   protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comeetpay);
         messageview=(TextView)findViewById(R.id.textView);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.CALL_PHONE},1000);
        }
    }

    public void pay(View view){

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://comeet-89622-default-rtdb.firebaseio.com/");
        SharedPreferences sharedPreferences;
        EditText username=(EditText)findViewById(R.id.editTextTextPersonName);
        EditText password=(EditText)findViewById(R.id.editTextTextPassword);


        sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
        Intent i = new Intent(Intent.ACTION_CALL);
        String encodedHash = Uri.encode("#");
        i.setData(Uri.parse("tel:"+"*806*0973651649*1"+encodedHash));

        TextView birr=(TextView)findViewById(R.id.editTextNumber);
        messageview.setText("          Please wait ......");

        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(username.getText().toString())){
                String getpassword=dataSnapshot.child(username.getText().toString()).child("password").getValue(String.class);
                String balance=dataSnapshot.child(username.getText().toString()).child("balance").getValue(String.class);
                if(getpassword.equals(password.getText().toString())){
                    messageview.setText("You have comeet account. please wait ....");
                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("tempbalance",birr.getText().toString());
                    update(databaseReference,username.getText().toString(),hashMap).addOnSuccessListener(suc ->
                    {
                        SharedPreferences.Editor ph= sharedPreferences.edit();
                        messageview.setText("Proccessing");
                        ph.putString("phone",username.getText().toString());
                        ph.putString("balance",balance);
                        ph.apply();
                        String temp=sharedPreferences.getString("status","");
                        messageview.setText(temp);

                    }).addOnFailureListener(er ->
                    {
                        messageview.setText("Failed");

                    });

                        startActivity(i);

                }else
                {
                    messageview.setText("check your username or password");
                }
                }else{

                    messageview.setText("check your username or password");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    public Task<Void> update(DatabaseReference databaseReference, String key, HashMap<String, Object> hashMap){
        return databaseReference.child("users").child(key).updateChildren(hashMap);
    }


}
