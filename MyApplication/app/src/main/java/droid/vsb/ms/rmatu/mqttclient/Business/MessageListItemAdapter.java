package droid.vsb.ms.rmatu.mqttclient.Business;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import droid.vsb.ms.rmatu.mqttclient.R;


public class MessageListItemAdapter extends RecyclerView.Adapter<MessageListItemAdapter.ReceiveMessageHolder> {

    private final ArrayList<ReceivedMessage> messages;
    private LayoutInflater inflater;

    public MessageListItemAdapter(Context contex, ArrayList<ReceivedMessage> messages) {
        inflater = LayoutInflater.from(contex);
        this.messages = messages;
    }

    @Override
    public ReceiveMessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.message_list_item, parent, false);
        ReceiveMessageHolder holder = new ReceiveMessageHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReceiveMessageHolder holder, int position) {
        ReceivedMessage message = messages.get(position);
        holder.setData(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ReceiveMessageHolder extends RecyclerView.ViewHolder
    {
        TextView topicTextView;
        TextView messageTextView;
        TextView dateTextView;

        public ReceiveMessageHolder(View itemView) {
            super(itemView);

            topicTextView = (TextView) itemView.findViewById(R.id.message_topic_text);
            messageTextView = (TextView) itemView.findViewById(R.id.message_text);
            dateTextView = (TextView) itemView.findViewById(R.id.message_date_text);
        }

        public void setData(ReceivedMessage message)
        {
            messageTextView.setText(new String(message.getMessage().getPayload()));
            topicTextView.setText(String.format("Topic: %1$s", message.getTopic()));

            DateFormat dateTimeFormatter = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            String shortDateStamp = dateTimeFormatter.format(message.getTimestamp());
            dateTextView.setText(String.format("Time: %1$s", shortDateStamp));
        }
    }
}
