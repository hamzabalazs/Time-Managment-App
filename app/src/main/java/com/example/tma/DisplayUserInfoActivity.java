package com.example.tma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DisplayUserInfoActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    DocumentReference docref = db.collection("users").document(currentUser.getUid());
    TextView nameTextView,emailTextView,zoneTextView,resetEmailTextView,resetZoneTextView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_info);

        nameTextView = findViewById(R.id.nameView);
        emailTextView = findViewById(R.id.emailView);
        zoneTextView = findViewById(R.id.zoneView);
        resetEmailTextView = findViewById(R.id.resetEmail);
        resetZoneTextView = findViewById(R.id.resetZone);
        button = findViewById(R.id.backButton);

        docref.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot docsnap = task.getResult();
                            if(docsnap.exists()){
                                String lastname = docsnap.get("Last Name").toString();
                                String firstname = docsnap.get("First Name").toString();
                                String email = docsnap.get("Email").toString();
                                String zone = docsnap.get("Zone").toString();
                                String fullname = firstname + " " + lastname;

                                nameTextView.setText(fullname);
                                emailTextView.setText(email);
                                zoneTextView.setText(zone);
                            }
                        }
                    }
                });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        resetEmailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder emailResetDialog = new AlertDialog.Builder(v.getContext());
                emailResetDialog.setTitle("Reset Email?");
                emailResetDialog.setMessage("Enter your new Email to change the old one!");
                emailResetDialog.setView(resetMail);

                emailResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetMail.getText().toString();
                        docref.update("Email",mail);

                    }
                });
                emailResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }
        });

        resetZoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetZone = new EditText(v.getContext());
                AlertDialog.Builder zoneResetDialog = new AlertDialog.Builder(v.getContext());
                zoneResetDialog.setTitle("Reset Zone?");
                zoneResetDialog.setMessage("Enter your new Zone to change the old one!");
                zoneResetDialog.setView(resetZone);

                zoneResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String zone = resetZone.getText().toString();
                        docref.update("Zone",zone);

                    }
                });
                zoneResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }
        });
    }
}