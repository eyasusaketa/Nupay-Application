package com.example.nupay;

import static android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION;

import static java.lang.Integer.parseInt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RecieveSms extends BroadcastReceiver {
SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
     sharedPreferences= context.getSharedPreferences("data",Context.MODE_PRIVATE);
     SharedPreferences.Editor input= sharedPreferences.edit();

    try {
        //Toast.makeText(context, "Sms Recieved", Toast.LENGTH_SHORT).show();
        if(SMS_RECEIVED_ACTION.equals
                ("android.provider.Telephony.SMS_RECEIVED")){
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent))
            {
                String msg = smsMessage.getMessageBody();
                String users = smsMessage.getDisplayOriginatingAddress();
                String message=msg;
                Toast.makeText(context, users, Toast.LENGTH_SHORT).show();
                if(users.equals("+251994") && message.toLowerCase().indexOf("906145754")!= -1) {
                    int tran = message.indexOf("transfer") + 9;
                    int birr = message.indexOf("Birr") - 1;
                    int numb = message.indexOf("9");
                    String number = message.substring(numb, numb + 9
                    );
                    String balance = message.substring(tran, birr);
                    Toast.makeText(context, "Your " + balance + "birr has been accepted", Toast.LENGTH_SHORT).show();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://comeet-89622-default-rtdb.firebaseio.com/");

                    String dbalance = sharedPreferences.getString("balance", "");
                    String totalbalance = String.valueOf((parseInt(dbalance) + parseInt(balance)));
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("status", true);
                    hashMap.put("balance", totalbalance);
                    String phone = sharedPreferences.getString("phone", "");

                    update(databaseReference, phone, hashMap).addOnSuccessListener(suc ->
                    {
                        input.putString("status", "true");
                        input.apply();
                    }).addOnFailureListener(er ->
                    {

                    });
                }
                if(users.equals("836")){
                  //  if("836"=="836"){
                    int balance = message.indexOf("ETB") + 4;
                    int transaction_direction = message.indexOf("Debited");
                    int dot = message.indexOf("to");
                    int name_index = message.indexOf("TO")+2;
                    String phone="",amount="";
                    FirebaseFirestore updatedata;
                    updatedata= FirebaseFirestore.getInstance();
                    String bankphone = sharedPreferences.getString("bankphone", "");


                    if ( message.toLowerCase().indexOf("transfered") != -1 ) {
                        Map<String,Object> data=new HashMap<>();
                        data.put("status","dept");
                        data.put("ammount",message.substring(balance, dot));
                        data.put("approved","yes");

                        if (message.toLowerCase().indexOf("1000087296923") != -1 ) {
                            Toast.makeText(context,message.substring(balance, dot),Toast.LENGTH_LONG).show();
                            //Toast.makeText(context,bankphone,Toast.LENGTH_SHORT).show();

                            updatedata.collection("banktransferdata").whereEqualTo("phone",bankphone).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                        String documentID = documentSnapshot.getId();
                                        updatedata.collection("banktransferdata").document(documentID)
                                                .update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(context, "Succesfull submitted", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {


                                                    }
                                                });

                                    } else {

                                        System.out.println("not found");

                                    }
                                }}
                                );

                    }



            }
        }
                    }
                }
            }


    catch (Exception e)
    {
        comeetpay com=new comeetpay();
        com.messageview.setText("some thing is wrong");

    }
    }

    public Task<Void> update(DatabaseReference databaseReference, String key, HashMap<String, Object> hashMap)
    {
        return databaseReference.child("users").child(key).updateChildren(hashMap);
    }
}



