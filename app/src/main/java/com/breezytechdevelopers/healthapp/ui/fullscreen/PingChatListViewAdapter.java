package com.breezytechdevelopers.healthapp.ui.fullscreen;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.breezytechdevelopers.healthapp.R;
import com.breezytechdevelopers.healthapp.database.entities.Message;

import java.util.ArrayList;
import java.util.List;

// MessageAdapter.java
public class PingChatListViewAdapter extends BaseAdapter {

    List<Message> messages = new ArrayList<Message>();
    Context context;

    public PingChatListViewAdapter(Context context) {
        this.context = context;
    }

    // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(i);

        if (message.isForUser()) { // this message was sent by us so let's create a basic chat bubble on the right
            convertView = messageInflater.inflate(R.layout.item_chat_sent, null);
            /*holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            holder.messageBody.setText(message.getText());*/
            convertView.setTag(holder);

        } else { // this message was sent by someone else so let's create an advanced chat bubble on the left
            convertView = messageInflater.inflate(R.layout.item_chat_recieved, null);
            /*holder.avatar = (View) convertView.findViewById(R.id.avatar);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);*/
            convertView.setTag(holder);

           /* holder.name.setText(message.getMemberData().getName());
            holder.messageBody.setText(message.getText());*/
        }

        return convertView;
    }

    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged(); // to render the list we need to notify
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class MessageViewHolder {
        View avatar;
        TextView messageBody;
        MessageViewHolder() {}
    }


}
