package droid.vsb.ms.rmatu.mqttclient.Business;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by renematuszek on 25/10/2017.
 */

public class MqttAgent {

    public class PublishMessage
    {
        private String Topic;

        public PublishMessage(String topic, String message) {
            Topic = topic;
            Message = message;
        }

        public String getTopic() {
            return Topic;
        }

        public void setTopic(String topic) {
            Topic = topic;
        }

        public String getMessage() {
            return Message;
        }

        public void setMessage(String message) {
            Message = message;
        }

        private String Message;


    }


    private static BlockingQueue<MqttAgent.PublishMessage> queue = new ArrayBlockingQueue<MqttAgent.PublishMessage>(1024);;

    public static int PendingMessageCount()
    {
        return queue.size();
    }

    public static void AddPublishMessage(PublishMessage message)
    {
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static PublishMessage GetPublishMessage()
    {
        try {
            if (queue.size()>0) {
                return queue.take();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }



}
