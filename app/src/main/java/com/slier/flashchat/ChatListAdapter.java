package com.slier.flashchat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;


import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {
    private Activity activity;
    private DatabaseReference databaseReference;
    private String displayName;
    private ArrayList<DataSnapshot> dataSnapshots;

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            ChatListAdapter.this.dataSnapshots.add(dataSnapshot);
            notifyDataSetChanged(); //tell the adapter to refresh itself
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    static class ViewHolder {
        TextView author;
        TextView message;
        LinearLayout.LayoutParams layoutParams;
    }


    public ChatListAdapter(Activity activity, DatabaseReference databaseReference, String displayName) {
        this.activity = activity;
        this.databaseReference = databaseReference.child("messages");
        this.databaseReference.addChildEventListener(this.childEventListener);
        this.displayName = displayName;
        this.dataSnapshots = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return this.dataSnapshots.size();
    }

    @Override
    public InstantMessage getItem(int i) {
        DataSnapshot dataSnapshot = this.dataSnapshots.get(i);
        return dataSnapshot.getValue(InstantMessage.class); //unwrap value to InstantMessage class
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.chat_msg_row, viewGroup, false);

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.author = view.findViewById(R.id.author);
            viewHolder.message = view.findViewById(R.id.message);
            viewHolder.layoutParams = (LinearLayout.LayoutParams) viewHolder.author.getLayoutParams();

            view.setTag(viewHolder);
        }

        final InstantMessage instantMessage = getItem(i);
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        boolean isMe = instantMessage.getAuthor().equals(this.displayName);
        this.setChatRowAppearance(isMe, viewHolder);

        viewHolder.author.setText(instantMessage.getAuthor());
        viewHolder.message.setText(instantMessage.getMessage());


        return view;
    }

    private void setChatRowAppearance(boolean isMe, ViewHolder v) {
        if (isMe) {
            v.layoutParams.gravity = Gravity.END;
            v.author.setTextColor(Color.GREEN);
            v.message.setBackgroundResource(R.drawable.bubble2);
        } else {
            v.layoutParams.gravity = Gravity.START;
            v.author.setTextColor(Color.BLUE);
            v.message.setBackgroundResource(R.drawable.bubble1);
        }

        v.author.setLayoutParams(v.layoutParams);
        v.message.setLayoutParams(v.layoutParams);
    }

    public void cleanup() {
        this.databaseReference.removeEventListener(this.childEventListener);
    }
}
