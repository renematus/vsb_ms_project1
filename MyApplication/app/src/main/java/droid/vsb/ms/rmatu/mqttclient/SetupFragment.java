package droid.vsb.ms.rmatu.mqttclient;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Map;

import droid.vsb.ms.rmatu.mqttclient.Business.ActionListener;
import droid.vsb.ms.rmatu.mqttclient.Business.Connection;
import droid.vsb.ms.rmatu.mqttclient.Business.Connections;
import droid.vsb.ms.rmatu.mqttclient.Business.IReceivedMessageListener;
import droid.vsb.ms.rmatu.mqttclient.Business.MessageListItemAdapter;
import droid.vsb.ms.rmatu.mqttclient.Business.MqttCallbackHandler;
import droid.vsb.ms.rmatu.mqttclient.Business.MqttTraceCallback;
import droid.vsb.ms.rmatu.mqttclient.Business.ReceivedMessage;
import droid.vsb.ms.rmatu.mqttclient.Business.Subscription;

/**
 * Created by renematuszek on 24/10/2017.
 */

public class SetupFragment extends Fragment {

    private static final String TAG = SetupFragment.class.getSimpleName();


    private int temp_qos_value = 0;
    private Connection connection;
    //private final MainActivity mainActivity = this;
    private final ChangeListener changeListener = new ChangeListener();
    private ArrayList<Subscription> subscriptions;
    private MqttConnectOptions connOpts;

    private Button btnSubscribe;
    private Button btnUnSubscribe;
    private EditText etTopic;
    private EditText etIdentity;
    private Button btnPublish;
    private EditText etPublishTopic;
    private EditText etPublishMessage;
    private TextView etRecvMessage;
    private Switch swConnect;

    private EditText etUserName;
    private EditText etPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.setup_fragment, container, false);

        etIdentity = (EditText) view.findViewById(R.id.etIdentity);
        btnSubscribe = (Button) view.findViewById(R.id.btnSubscribe);
        btnUnSubscribe = (Button) view.findViewById(R.id.btnUnSubscribe);
        etTopic = (EditText) view.findViewById(R.id.etTopic);
        btnPublish = (Button) view.findViewById(R.id.btnPublish);
        etPublishTopic = (EditText) view.findViewById(R.id.etPublishTopic);
        etPublishMessage = (EditText) view.findViewById(R.id.etPublishMessage);
        etRecvMessage = (TextView) view.findViewById(R.id.etRecvMessage);
        swConnect = (Switch) view.findViewById(R.id.switchConnect);
        etUserName = (EditText) view.findViewById(R.id.etUserName);
        etPassword = (EditText) view.findViewById(R.id.etPassword);

        InitConnection();

        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = etTopic.getText().toString();
                if (topic !=null && topic.length() >0) {
                    Subscription subscription = new Subscription(topic, temp_qos_value, connection.handle(), false);
                    subscriptions.add(subscription);
                    try {
                        connection.addNewSubscription(subscription);

                    } catch (MqttException ex) {
                        System.out.println("MqttException whilst subscribing: " + ex.getMessage());
                    }
                }
            }
        });

        btnUnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = etTopic.getText().toString();
                if (topic !=null && topic.length() >0) {
                    Subscription subscription = new Subscription(topic, temp_qos_value, connection.handle(), false);
                    subscriptions.add(subscription);
                    try {
                        connection.unsubscribe(subscription);

                    } catch (MqttException ex) {
                        System.out.println("MqttException whilst unsubscribing: " + ex.getMessage());
                    }
                }

            }
        });

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String topic = etPublishTopic.getText().toString();
                String message = etPublishMessage.getText().toString();

                if (topic!=null && topic.length()>0 && message!=null && message.length() > 0) {
                    int selectedQos = 1;
                    boolean retainValue = false;
                    publish(connection, topic, message, selectedQos, retainValue);
                }
            }
        });


        swConnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swConnect.setText("Disconnect");
                    Connect();
                    connect(connection);
                    changeConnectedState(true);
                } else {
                    swConnect.setText("Connect");

                    //Send status message to broker
                    String statusTopic = String.format("/mschat/status/%s", etIdentity.getText().toString());
                    publish(connection, statusTopic, "offline", 1, true);

                    disconnect(connection);
                    changeConnectedState(false);
                }
            }
        });

        if (connection!=null) {
            changeConnectedState(connection.isConnected());
        }





        return view;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        subscriptions = new ArrayList<Subscription>();

    }

    private void changeConnectedState(boolean state){
        swConnect.setChecked(state);
    }

    public void connect(Connection connection) {
        String[] actionArgs = new String[1];
        actionArgs[0] = connection.getId();
        final ActionListener callback = new ActionListener(getActivity(),
                ActionListener.Action.CONNECT, connection, actionArgs);
        connection.getClient().setCallback(new MqttCallbackHandler(getActivity(), connection.handle()));
        try {
            connection.getClient().connect(connection.getConnectionOptions(), null, callback);
        }
        catch (MqttException e) {
            Log.e(this.getClass().getCanonicalName(),
                    "MqttException occurred", e);
        }
    }

    public void disconnect(Connection connection){

        try {
            connection.getClient().disconnect();
            etRecvMessage.setText("Client disconnected");
        } catch( MqttException ex){
            Log.e(TAG, "Exception occurred during disconnect: " + ex.getMessage());
        }
    }

    public void InitConnection()
    {
        connection = Connection.createConnection(ConnectConstants.clientHandle, etIdentity.getText().toString() ,ConnectConstants.serverHostName,ConnectConstants.serverPort, getActivity() ,false);
        connection.registerChangeListener(changeListener);
        connection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);


        String[] actionArgs = new String[1];
        actionArgs[0] = etIdentity.getText().toString();
        final ActionListener callback = new ActionListener(getActivity(),
                ActionListener.Action.CONNECT, connection, actionArgs);
        connection.getClient().setCallback(new MqttCallbackHandler(getActivity(), ConnectConstants.clientHandle));

        connection.getClient().setTraceCallback(new MqttTraceCallback());

        connOpts = new MqttConnectOptions();
        connOpts.setConnectionTimeout(80);
        connOpts.setCleanSession(true);
        connOpts.setKeepAliveInterval(200);

        byte[] bytes = new byte[1000];
        connOpts.setWill("topic", bytes,  0, false);
        //Todo:  upravit podle MqttConnectOptions optionsFromModel(ConnectionModel model)

        connection.addConnectionOptions(connOpts);

        Connections.getInstance(getActivity()).addConnection(connection);
    }



    public void Connect(){

        Map<String, Connection> connections = Connections.getInstance(this.getActivity())
                .getConnections();
        final Connection connection = connections.get(ConnectConstants.clientHandle);


        connOpts = connection.getConnectionOptions();
        connOpts.setConnectionTimeout(80);
        connOpts.setCleanSession(true);
        connOpts.setKeepAliveInterval(200);

        byte[] bytes = new byte[1000];
        connOpts.setWill("topic", bytes,  0, false);
        //Todo:  upravit podle MqttConnectOptions optionsFromModel(ConnectionModel model)


        String userName = etUserName.getText().toString();
        String password = etPassword.getText().toString();

        if (userName!=null && userName.length()>0)
        {
            connOpts.setUserName(userName);
        }
        else
        {
            connOpts.setUserName(null);
        }

        if (password!=null && password.length()>0)
        {
            connOpts.setPassword(password.toCharArray());
        }
        else
        {
            connOpts.setPassword(null);
        }


        //Set last will
        connOpts.setWill(ConnectConstants.LastWillTopic, ConnectConstants.LastWillMessage.getBytes(), 1, true);


        connection.addConnectionOptions(connOpts);
    }

    public void publish(Connection connection, String topic, String message, int qos, boolean retain){

        try {
            String[] actionArgs = new String[2];
            actionArgs[0] = message;
            actionArgs[1] = topic;
            final ActionListener callback = new ActionListener(getActivity(),
                    ActionListener.Action.PUBLISH, connection, actionArgs);
            connection.getClient().publish(topic, message.getBytes(), qos, retain, null, callback);
        } catch( MqttException ex){
            Log.e(TAG, "Exception occurred during publish: " + ex.getMessage());
        }
    }



    private class ChangeListener implements PropertyChangeListener {

        /**
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        @Override
        public void propertyChange(final PropertyChangeEvent event) {

            if (!event.getPropertyName().equals(ConnectConstants.ConnectionStatusProperty)) {
                return;
            }

            //Only for event connected send status to broker
            if (event.getSource().toString().contains("Connected")) {
                //Send status message to broker
                String statusTopic = String.format("/mschat/status/%s", etIdentity.getText().toString());
                publish(connection, statusTopic, "online", 1, true);
            }

            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    etRecvMessage.setText(event.getSource().toString());

                }

            });

        }

    }



}
