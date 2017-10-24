package droid.vsb.ms.rmatu.mqttclient.Business;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import droid.vsb.ms.rmatu.mqttclient.R;


public class MessageListItemAdapter extends ArrayAdapter<ReceivedMessage> {

    private final Context context;
    private final ArrayList<ReceivedMessage> messages;

    public MessageListItemAdapter(Context context, ArrayList<ReceivedMessage> messages){
        super(context, R.layout.message_list_item, messages);
        this.context = context;
        this.messages = messages;

    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.message_list_item, parent, false);
        TextView topicTextView = (TextView) rowView.findViewById(R.id.message_topic_text);
        TextView messageTextView = (TextView) rowView.findViewById(R.id.message_text);
        TextView dateTextView = (TextView) rowView.findViewById(R.id.message_date_text);

        messageTextView.setText(new String(messages.get(position).getMessage().getPayload()));
        topicTextView.setText(String.format("Topic: %1$s", messages.get(position).getTopic()));

        DateFormat dateTimeFormatter = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        String shortDateStamp = dateTimeFormatter.format(messages.get(position).getTimestamp());
        dateTextView.setText(String.format("Time: %1$s", shortDateStamp));
        return rowView;
    }
}
