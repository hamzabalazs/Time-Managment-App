package com.example.tma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class RegisterActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    TextView loginbtn;
    EditText mEmail, mPassword;
    Button registerbtn;
    FirebaseAuth fAuth;
    ProgressBar progressbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = findViewById(R.id.editTextEmail);
        mPassword = findViewById(R.id.editTextPass);
        registerbtn = findViewById(R.id.button);
        loginbtn = findViewById(R.id.textView4);
        fAuth = FirebaseAuth.getInstance();
        progressbar = findViewById(R.id.progressBar);




        if(currentUser != null){
            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot docsnap = task.getResult();
                                if(docsnap.exists()){
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    finish();
                                }
                                else{
                                    startActivity(new Intent(getApplicationContext(),UserInformationActivity.class));
                                    finish();
                                }
                            }
                        }
                    });


        }


        registerbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();


                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required!");
                    return;
                }

                if(password.length() < 8){
                    mPassword.setError("Password must be at least 8 characters long!");
                    return;
                }

                progressbar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this,"User Created!",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),UserInformationActivity.class));
                        }else{
                            Toast.makeText(RegisterActivity.this,"Error! " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressbar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openLogin();
            }

        });


    }
    public void openLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}