package uni.siegen.bgf.cardafit.firebase;

import com.google.firebase.messaging.*;

import uni.siegen.bgf.cardafit.model.PushNotificationRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FCMService {

    private Logger logger = LoggerFactory.getLogger(FCMService.class);

    public void sendMessage(Map<String, String> data, PushNotificationRequest request)
            throws InterruptedException, ExecutionException {
        Message message = getPreconfiguredMessageWithData(data, request);
        String response = sendAndGetResponse(message);
        logger.info("Sent message with data. Topic: " + request.getTopic() + ", " + response);
    }

    public void sendMessageWithoutData(PushNotificationRequest request)
            throws InterruptedException, ExecutionException {
        Message message = getPreconfiguredMessageWithoutData(request);
        String response = sendAndGetResponse(message);
        logger.info("Sent message without data. Topic: " + request.getTopic() + ", " + response);
    }

    public void sendMessageToToken(PushNotificationRequest request)
            throws InterruptedException, ExecutionException {
        Message message = getPreconfiguredMessageToToken(request);
        String response = sendAndGetResponse(message);
        logger.info("Sent message to token. Device token: " + request.getToken() + ", " + response);
    }
    
    public void sendMessageToTokenWithData(Map<String, String> data, PushNotificationRequest request)
            throws InterruptedException, ExecutionException, FirebaseMessagingException {
        Message message = getPreconfiguredMessageToTokenWithData(data, request);
    	String response = sendAndGetResponse(message);
    	logger.info("Sent message with data. Topic: " + request.getTopic() + ", " + response);
    }
    
    public void sendMessageToAllWithOnlyData(Map<String, String> data, String topic)
            throws InterruptedException, ExecutionException, FirebaseMessagingException {
        Message message = getPreconfiguredMessageWithOnlyData(data, topic);
    	String response = sendAndGetResponse(message);
    	logger.info("Sent message with only data and without token" + ", " + response);
    }
    
    public void sendMessageToTokenWithOnlyData(Map<String, String> data, String token, String topic)
            throws InterruptedException, ExecutionException, FirebaseMessagingException {
        Message message = getPreconfiguredMessageToTokenWithOnlyData(data, token, topic);
    	String response = sendAndGetResponse(message);
    	logger.info("Sent message with only data. Token: " + token + ", " + response);
    }

    private String sendAndGetResponse(Message message) throws InterruptedException, ExecutionException {
        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }

    private AndroidConfig getAndroidConfig(String topic) {
        return AndroidConfig.builder()
                .setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(topic)
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder().setSound(NotificationParameter.SOUND.getValue())
                        .setColor(NotificationParameter.COLOR.getValue()).setTag(" " + LocalDateTime.now()).build()).build();
    }
    
    private AndroidConfig getAndroidConfigOnlyData(String topic) {
        return AndroidConfig.builder()
                .setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(topic)
                .setPriority(AndroidConfig.Priority.HIGH).build();
    }

    private ApnsConfig getApnsConfig(String topic) {
        return ApnsConfig.builder()
                .setAps(Aps.builder().setCategory(topic).setThreadId(topic).build()).build();
    }

    private Message getPreconfiguredMessageToToken(PushNotificationRequest request) {
        return getPlatformConfigForNotificationMessage(request).setToken(request.getToken())
                .build();
    }

    private Message getPreconfiguredMessageWithoutData(PushNotificationRequest request) {
        return getPlatformConfigForNotificationMessage(request).setTopic(request.getTopic())
                .build();
    }

    private Message getPreconfiguredMessageWithData(Map<String, String> data, PushNotificationRequest request) {
        return getPlatformConfigForNotificationMessage(request).putAllData(data).setTopic(request.getTopic())
                .build();
    }
    
    private Message getPreconfiguredMessageToTokenWithData(Map<String, String> data, PushNotificationRequest request) {
        return getPlatformConfigForNotificationMessage(request).putAllData(data).setToken(request.getToken())
                .build();
    }
    
    private Message getPreconfiguredMessageWithOnlyData(Map<String, String> data, String topic) {
        return getPlatformConfigForOnlyDataMessage(topic)
        		.putAllData(data)
                .build();
    }
    
    private Message getPreconfiguredMessageToTokenWithOnlyData(Map<String, String> data, String token, String topic) {
        return getPlatformConfigForOnlyDataMessage(topic)
        		.putAllData(data)
        		.setToken(token)
                .build();
    }

    private Message.Builder getPlatformConfigForNotificationMessage(PushNotificationRequest request) {
        AndroidConfig androidConfig = getAndroidConfig(request.getTopic());
        ApnsConfig apnsConfig = getApnsConfig(request.getTopic());
        return Message.builder()
                .setApnsConfig(apnsConfig).setAndroidConfig(androidConfig).setNotification(
                        new Notification(request.getTitle(), request.getMessage()));
    }

    private Message.Builder getPlatformConfigForOnlyDataMessage(String topic) {
        AndroidConfig androidConfig = getAndroidConfigOnlyData(topic);
        ApnsConfig apnsConfig = getApnsConfig(topic);
        return Message.builder().setApnsConfig(apnsConfig).setAndroidConfig(androidConfig);
    }

}
