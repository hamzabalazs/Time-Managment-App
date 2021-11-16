package com.example.tma;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserInformationActivity extends AppCompatActivity {
    Map<String, Object> user = new HashMap<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText firstname,lastname;
    Spinner zone;
    Button button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        firstname = findViewById(R.id.userFirstName);
        lastname = findViewById(R.id.userLastName);
        zone = findViewById(R.id.userZone);
        button = findViewById(R.id.userRegister);

        zone.setAdapter(new ArrayAdapter<Zone>(this,R.layout.support_simple_spinner_dropdown_item, Zone.values()));

        FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user!=null){
                    String email = CurrentUser.getEmail();
                    String mFirstName = firstname.getText().toString().trim();
                    String mLastName = lastname.getText().toString().trim();
                    String mZone = zone.getSelectedItem().toString();

                    if(TextUtils.isEmpty(mFirstName)){
                        firstname.setError("First name is Required!");
                        return;
                    }
                    if(TextUtils.isEmpty(mLastName)){
                        lastname.setError("Last name is Required!");
                        return;
                    }


                    user.put("Email",email);
                    user.put("First Name",mFirstName);
                    user.put("Last Name",mLastName);
                    user.put("Zone",mZone);

                    db.collection("users")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });

                }
            }
        });



    }

}