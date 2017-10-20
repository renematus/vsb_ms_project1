package droid.vsb.ms.rmatu.mqttclient;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import droid.vsb.ms.rmatu.mqttclient.Business.ActionListener;
import droid.vsb.ms.rmatu.mqttclient.Business.Connection;
import droid.vsb.ms.rmatu.mqttclient.Business.Connections;
import droid.vsb.ms.rmatu.mqttclient.Business.MqttCallbackHandler;
import droid.vsb.ms.rmatu.mqttclient.Business.MqttTraceCallback;
import droid.vsb.ms.rmatu.mqttclient.Business.Subscription;

public class MainActivity extends AppCompatActivity {

    private int temp_qos_value = 0;
    private Connection connection;
    private final MainActivity mainActivity = this;
    private final ChangeListener changeListener = new ChangeListener();
    private ArrayList<Subscription> subscriptions;

    private Button btnSubscribe;
    private EditText etTopic;
    private EditText etRecvMessage;
    private Switch swConnect;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSubscribe = (Button) findViewById(R.id.btnSubscribe);
        etTopic = (EditText) findViewById(R.id.etTopic);
        etRecvMessage = (EditText) findViewById(R.id.etRecvMessage);
        swConnect = (Switch) findViewById(R.id.switchConnect);

        subscriptions = new ArrayList<Subscription>();

        Connect();

        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = etTopic.getText().toString();

                Subscription subscription = new Subscription(topic, temp_qos_value, connection.handle(), false);
                subscriptions.add(subscription);
                try {
                    connection.addNewSubscription(subscription);

                } catch (MqttException ex) {
                    System.out.println("MqttException whilst subscribing: " + ex.getMessage());
                }
            }
        });


        swConnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    connect(connection);
                    changeConnectedState(true);
                } else {
                    disconnect(connection);
                    changeConnectedState(false);
                }
            }
        });
        changeConnectedState(connection.isConnected());

    }

    private void changeConnectedState(boolean state){
        swConnect.setChecked(state);
    }

    public void connect(Connection connection) {
        String[] actionArgs = new String[1];
        actionArgs[0] = connection.getId();
        final ActionListener callback = new ActionListener(this,
                ActionListener.Action.CONNECT, connection, actionArgs);
        connection.getClient().setCallback(new MqttCallbackHandler(this, connection.handle()));
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
        } catch( MqttException ex){
            Log.e("", "Exception occurred during disconnect: " + ex.getMessage());
        }
    }



    public void Connect(){
        connection = Connection.createConnection(ConnectConstants.clientHandle, ConnectConstants.clientId ,ConnectConstants.serverHostName,ConnectConstants.serverPort, this ,false);
        connection.registerChangeListener(changeListener);
        connection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);


        String[] actionArgs = new String[1];
        actionArgs[0] = ConnectConstants.clientId;
        final ActionListener callback = new ActionListener(this,
                ActionListener.Action.CONNECT, connection, actionArgs);
        connection.getClient().setCallback(new MqttCallbackHandler(this, ConnectConstants.clientHandle));



        connection.getClient().setTraceCallback(new MqttTraceCallback());

        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setConnectionTimeout(80);
        connOpts.setCleanSession(true);
        connOpts.setKeepAliveInterval(200);

        byte[] bytes = new byte[1000];
        connOpts.setWill("topic", bytes,  0, false);
        //Todo:  upravit podle MqttConnectOptions optionsFromModel(ConnectionModel model)



        connection.addConnectionOptions(connOpts);
        Connections.getInstance(this).addConnection(connection);

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
            mainActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    String oldMessages = etRecvMessage.getText().toString();
                    etRecvMessage.setText(event.getSource().toString()+oldMessages);


                    //Todo: XXX
                    //mainActivity.drawerFragment.notifyDataSetChanged();
                }

            });

        }

    }
}
