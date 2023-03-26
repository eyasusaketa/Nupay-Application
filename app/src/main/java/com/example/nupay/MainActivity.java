package com.example.nupay;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS},1000);
        }
        ImageView contactcenter=findViewById(R.id.telegramlink);
        contactcenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(context,"select telegram app",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/+Fy3Ottr0BMplOGM0"));
                startActivity(intent);
            }
        });
    }
    public void paycomeet(View view){
        Intent pay=new Intent(MainActivity.this,comeetpay.class);
        startActivity(pay);

    }

    public void backpay(View view){
        Intent bank=new Intent(MainActivity.this,bancktransfer.class);
        startActivity(bank);
    }
}