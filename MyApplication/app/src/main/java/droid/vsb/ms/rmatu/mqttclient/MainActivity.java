package droid.vsb.ms.rmatu.mqttclient;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

    private Button btnSubscribe;
    private EditText etTopic;
    private EditText etRecvMessage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSubscribe = (Button) findViewById(R.id.btnSubscribe);
        etTopic = (EditText) findViewById(R.id.etTopic);
        etRecvMessage = (EditText) findViewById(R.id.etRecvMessage);

        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = etTopic.getText().toString();

                Subscription subscription = new Subscription(topic, temp_qos_value, connection.handle(), false);
                //subscriptions.add(subscription);
                try {
                    connection.addNewSubscription(subscription);

                } catch (MqttException ex) {
                    System.out.println("MqttException whilst subscribing: " + ex.getMessage());
                }
            }
        });

        Connect();

    }



    public void Connect(){
        Connection connection = Connection.createConnection(ConnectConstants.clientHandle, ConnectConstants.clientId ,ConnectConstants.serverHostName,ConnectConstants.serverPort, this ,false);
        connection.registerChangeListener(changeListener);
        connection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);


        String[] actionArgs = new String[1];
        actionArgs[0] = ConnectConstants.clientId;
        final ActionListener callback = new ActionListener(this,
                ActionListener.Action.CONNECT, connection, actionArgs);
        connection.getClient().setCallback(new MqttCallbackHandler(this, ConnectConstants.clientHandle));



        connection.getClient().setTraceCallback(new MqttTraceCallback());

        MqttConnectOptions connOpts = new MqttConnectOptions();
        //Todo:  upravit podle MqttConnectOptions optionsFromModel(ConnectionModel model)



        connection.addConnectionOptions(connOpts);
        Connections.getInstance(this).addConnection(connection);
        //connectionMap.add(model.getClientHandle());
        //drawerFragment.addConnection(connection);

        try {
            connection.getClient().connect(connOpts, null, callback);


//            Fragment fragment  = new ConnectionFragment();
//            Bundle bundle = new Bundle();
//            bundle.putString(ActivityConstants.CONNECTION_KEY, connection.handle());
//            bundle.putBoolean(ActivityConstants.CONNECTED, true);
//            fragment.setArguments(bundle);
//            String title = connection.getId();
//            displayFragment(fragment, title);

        }
        catch (MqttException e) {
            Log.e(this.getClass().getCanonicalName(),
                    "MqttException occurred", e);
        }

    }



    private class ChangeListener implements PropertyChangeListener {

        /**
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        @Override
        public void propertyChange(PropertyChangeEvent event) {

            if (!event.getPropertyName().equals(ConnectConstants.ConnectionStatusProperty)) {
                return;
            }
            mainActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    //Todo: XXX
                    //mainActivity.drawerFragment.notifyDataSetChanged();
                }

            });

        }

    }
}
