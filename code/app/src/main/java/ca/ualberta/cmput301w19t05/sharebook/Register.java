package ca.ualberta.cmput301w19t05.sharebook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    ProgressDialog progressDialog;

    private EditText signupInputName, signupInputEmail, signupInputPassword, secondPassword;
    private Button submitRegister;
    private Button back;
    private FirebaseAuth mAuth;
    private FirebaseHandler firebaseHandler;
    private boolean cancel, wait;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseHandler = new FirebaseHandler(Register.this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        signupInputName = findViewById(R.id.signup_input_name);
        signupInputEmail = findViewById(R.id.signup_input_email);
        signupInputPassword = findViewById(R.id.signup_input_password);
        secondPassword = findViewById(R.id.second_input_password);
        submitRegister = findViewById(R.id.submut_register);
        back = findViewById(R.id.end_register);
        submitRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel = true;
                wait = true;
                secondPassword.setError(null);
                String first = signupInputPassword.getText().toString();
                String second = secondPassword.getText().toString();

                if (first.equals(second) && !"".equals(first)) {
                    submitForm();
                } else {

                    secondPassword.setError(getString(R.string.error_two_password_not_match));
                }

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }

    private void submitForm() {
        mAuth = FirebaseAuth.getInstance();
        final String email = signupInputEmail.getText().toString();
        final String password = signupInputPassword.getText().toString();
        final String username = signupInputName.getText().toString();
        checkIfUsernameExists(username);
        firebaseHandler.addUsernameEmailTuple(email, username);

        if (cancel){
            sameUsernameError();
            return;
        }
        progressDialog.setMessage("Adding you ...");
        showDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideDialog();
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "createUserWithEmail:success");


                            Intent intent = new Intent();
                            intent.putExtra("email", email);
                            setResult(0x07, intent);
                            finish();

                        } else {
                            // If sign in fails
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }

    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "usernameExists: check if " + username + " already exists");
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.db_username_email_tuple))
                .orderByKey().equalTo(username);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()==null){
                    cancel = false;
                }
                wait= false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
/*        reference.child(getString(R.string.db_username_email_tuple))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.hasChild(username)){
                            cancel = false;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/


    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    private void sameUsernameError(){
        signupInputName.setError(null);
        signupInputName.setError("username exists");
        signupInputName.requestFocus();
    }
}
