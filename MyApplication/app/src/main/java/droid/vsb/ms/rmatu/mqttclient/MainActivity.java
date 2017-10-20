package droid.vsb.ms.rmatu.mqttclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import droid.vsb.ms.rmatu.mqttclient.Business.Connection;

public class MainActivity extends AppCompatActivity {

    private Connection connection;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
