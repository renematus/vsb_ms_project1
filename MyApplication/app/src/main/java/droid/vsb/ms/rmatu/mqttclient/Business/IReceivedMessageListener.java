package droid.vsb.ms.rmatu.mqttclient.Business;


public interface IReceivedMessageListener {

    void onMessageReceived(ReceivedMessage message);
}