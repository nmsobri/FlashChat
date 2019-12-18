package com.slier.flashchat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainChatActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private String displayName;
    private ListView chatListView;
    private EditText inputText;
    private ImageButton sendButton;
    private ChatListAdapter chatListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        setDisplayName();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        inputText = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        chatListView = findViewById(R.id.chat_list_view);

        inputText.setOnEditorActionListener((textView, i, keyEvent) -> {
            sendMessage();
            return true;
        });

        sendButton.setOnClickListener(view -> {
            sendMessage();
        });

    }

    private void setDisplayName() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.displayName = user.getDisplayName();
        if (this.displayName == null) this.displayName = "Anonymous";
    }

    private void sendMessage() {
        String message = inputText.getText().toString();
        if (!message.equals("")) {
            InstantMessage instantMessage = new InstantMessage(this.displayName, message);
            this.databaseReference.child("messages").push().setValue(instantMessage);
            inputText.setText("");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        this.chatListAdapter = new ChatListAdapter(this, this.databaseReference, this.displayName);
        this.chatListView.setAdapter(this.chatListAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        this.chatListAdapter.cleanup();

    }
}
