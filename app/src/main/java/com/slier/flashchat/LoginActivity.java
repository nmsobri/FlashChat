package com.slier.flashchat;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private AutoCompleteTextView emailView;
    private EditText passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailView = findViewById(R.id.login_email);
        passwordView = findViewById(R.id.login_password);

        passwordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            attemptLogin();
            return true;
        });

        this.auth = FirebaseAuth.getInstance();
    }

    public void signInExistingUser(View v) {
        this.attemptLogin();
    }

    public void registerNewUser(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        finish();
        startActivity(intent);
    }

    private void attemptLogin() {
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        if (email.equals("") || password.equals("")) return;

        Toast.makeText(this, getString(R.string.login_progress), Toast.LENGTH_SHORT).show();

        this.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(LoginActivity.this, MainChatActivity.class);
                finish();
                startActivity(intent);
            } else {
                Log.d("FlashChat", "Problem signing in " + task.getException());
                showErrorDialog("There was a problem signing in");
            }
        });
    }

    private void showErrorDialog(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("Opps")
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}