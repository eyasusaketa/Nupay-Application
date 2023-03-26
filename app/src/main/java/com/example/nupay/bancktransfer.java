package com.example.nupay;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.HashMap;
import java.util.Map;

public class bancktransfer extends AppCompatActivity {
    TextView accountnumber;
    TextView phone;
    private String string;
    TextView balance;
    TextView fullname;
    Context context;
    Button submit;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bancktransfer);

        TextView textView=findViewById(R.id.acno);
        TextView txt=findViewById(R.id.textView);
        accountnumber = (TextView) findViewById(R.id.acno);
        ImageView view = (ImageView) findViewById(R.id.imageView6);
        phone=findViewById(R.id.editTextTextPersonName2);
        balance=findViewById(R.id.editTextNumber);
        fullname=findViewById(R.id.editTextTextPersonName);
        spinner=findViewById(R.id.spinner2);
        submit=findViewById(R.id.button3);

        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,banks);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                textView.setText(accnumber[spinner.getSelectedItemPosition()]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

       submit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(TextUtils.isEmpty(fullname.getText())){
                   fullname.setError("Full name is required");
               }
               else if(TextUtils.isEmpty(phone.getText())){
                   phone.setError("Phone number is must");
               }else if(!(phone.getText().toString().matches("^[+][0-9]{10,13}$"))){
                   phone.requestFocus();
                   phone.setError("Correct format: +251xxxxxxxxx");
               }else if(!(phone.getText().toString().length()==13)){
                   phone.requestFocus();
                   phone.setError("Enter full phone number");
               }
               else if(TextUtils.isEmpty(balance.getText())){
                    balance.setError("please enter amount");
               }

               else {
                   SharedPreferences sharedPreferences;
                   sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
                   SharedPreferences.Editor ph= sharedPreferences.edit();
                   ph.putString("bankphone",phone.getText().toString());
                   ph.apply();

                   try {

                       add(fullname.getText().toString(), phone.getText().toString(), txt, balance.getText().toString(), spinner.getSelectedItem().toString());
                       submit.setVisibility(View.INVISIBLE);
                   }catch(Exception e){
                       txt.setText( "Please check you network- ");
                   }
               }
           }
       });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Copy", accountnumber.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(bancktransfer.this, "Copied", Toast.LENGTH_SHORT).show();
            }
        });
    }


  public void add(String fullname,String phonenumber,TextView text, String balance,String banktype){

      FirebaseFirestore db;

      Map<String, Object> user = new HashMap<>();
      user.put("fullname",fullname);

      user.put("phone", phonenumber);
      user.put("ammount", balance);
      user.put("bank",banktype);
      user.put("approved","No");
      user.put("status", "request");
      user.put("timestamp", FieldValue.serverTimestamp());
      db = FirebaseFirestore.getInstance();

      db.collection("banktransferdata")
              .add(user)
              .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                  @Override
                  public void onSuccess(DocumentReference documentReference) {
                      Intent i = new Intent(Intent.ACTION_CALL);
                      String encodedHash = Uri.encode("#");
                      i.setData(Uri.parse("tel:" + ussdnumber[spinner.getSelectedItemPosition()] + encodedHash));
                      startActivity(i);
                      text.setText( "On process... please transfer birr to given account number!");
                      Toast.makeText(bancktransfer.this, "On process... please transfer birr to given account number!", Toast.LENGTH_SHORT).show();

                  }
              })
              .addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {

                      text.setText( "Please check you network- ");
                      Toast.makeText(bancktransfer.this, "Please check you network- " + e.getMessage(), Toast.LENGTH_SHORT).show();
                  }
              });

  }


    String[] banks ={"Cooperative Bank of Oromia","commertial Bank", "Oromia Bank", "Awash Bank", "Abyssinia Bank"};
    String[] accnumber={"1000087296923","Not active use coop ","Not active use coop ","NOt active use coop ","Not active use coop "};
    String[] ussdnumber={"*841"," ","  "," "," "};
}