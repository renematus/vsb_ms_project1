package droid.vsb.ms.rmatu.mqttclient;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import droid.vsb.ms.rmatu.mqttclient.Business.ActionListener;
import droid.vsb.ms.rmatu.mqttclient.Business.Connection;
import droid.vsb.ms.rmatu.mqttclient.Business.Connections;
import droid.vsb.ms.rmatu.mqttclient.Business.IReceivedMessageListener;
import droid.vsb.ms.rmatu.mqttclient.Business.MessageListItemAdapter;
import droid.vsb.ms.rmatu.mqttclient.Business.MqttCallbackHandler;
import droid.vsb.ms.rmatu.mqttclient.Business.MqttTraceCallback;
import droid.vsb.ms.rmatu.mqttclient.Business.ReceivedMessage;
import droid.vsb.ms.rmatu.mqttclient.Business.Subscription;
import droid.vsb.ms.rmatu.mqttclient.Business.TextTabsAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();

    private ViewPager viewPager;
    private TextTabsAdapter adapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        addData(new SetupFragment(), "SETUP");
        addData(new MessageFragment(), "RECEIVED MESSAGES");

        adapter = new TextTabsAdapter(getSupportFragmentManager(), fragmentList, titleList);

        // Bind Adapter to ViewPager.
        viewPager.setAdapter(adapter);

        // Link ViewPager and TabLayout
        tabLayout.setupWithViewPager(viewPager);

    }

    private void addData(Fragment fragment, String title) {
        fragmentList.add(fragment);
        titleList.add(title);
    }
}
