package droid.vsb.ms.rmatu.mqttclient;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

import droid.vsb.ms.rmatu.mqttclient.Business.Connection;
import droid.vsb.ms.rmatu.mqttclient.Business.Connections;
import droid.vsb.ms.rmatu.mqttclient.Business.IReceivedMessageListener;
import droid.vsb.ms.rmatu.mqttclient.Business.MessageListItemAdapter;
import droid.vsb.ms.rmatu.mqttclient.Business.ReceivedMessage;

/**
 * Created by renematuszek on 24/10/2017.
 */

public class MessageFragment extends Fragment{

    private ArrayList<ReceivedMessage> messages;
    private MessageListItemAdapter messageListAdapter;
    private ListView messageList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.message_fragment, container, false);

        messageList = (ListView) view.findViewById(R.id.message_list) ;

        Map<String, Connection> connections = Connections.getInstance(this.getActivity())
                .getConnections();
        Connection connection = connections.get(ConnectConstants.clientHandle);

        messages = connection.getMessages();
        messageListAdapter = new MessageListItemAdapter(getActivity(), messages);
        messageList.setAdapter(messageListAdapter);
        connection.addReceivedMessageListner(new IReceivedMessageListener() {
            @Override
            public void onMessageReceived(ReceivedMessage message) {
                messageListAdapter.notifyDataSetChanged();
            }
        });


        return view;
    }

}
