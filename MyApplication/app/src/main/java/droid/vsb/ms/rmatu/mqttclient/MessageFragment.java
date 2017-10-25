package droid.vsb.ms.rmatu.mqttclient;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    private static final String TAG = MessageFragment.class.getSimpleName();

    private ArrayList<ReceivedMessage> messages;
    private MessageListItemAdapter messageListAdapter;
    private RecyclerView messageList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.message_fragment, container, false);

        messageList = (RecyclerView) view.findViewById(R.id.message_list) ;

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(this.getContext()); // (Context context, int spanCount)
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        messageList.setLayoutManager(mLinearLayoutManagerVertical);
        messageList.setItemAnimator(new DefaultItemAnimator());

        Map<String, Connection> connections = Connections.getInstance(this.getActivity())
                .getConnections();
        final Connection connection = connections.get(ConnectConstants.clientHandle);

        messages = connection.getMessages();
        messageListAdapter = new MessageListItemAdapter(getActivity(), messages);
        messageList.setAdapter(messageListAdapter);
        connection.addReceivedMessageListner(new IReceivedMessageListener() {
            @Override
            public void onMessageReceived(ReceivedMessage message) {

               // messageListAdapter = new MessageListItemAdapter(getActivity(), connection.getMessages());
               // messageList.setAdapter(messageListAdapter);
                messageListAdapter.notifyDataSetChanged();
            }
        });


        return view;
    }


}
