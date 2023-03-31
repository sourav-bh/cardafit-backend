package uni.siegen.bgf.cardafit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import uni.siegen.bgf.cardafit.firebase.FCMService;
import uni.siegen.bgf.cardafit.model.PushNotificationRequest;
import uni.siegen.bgf.cardafit.model.User;
import uni.siegen.bgf.cardafit.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class PushNotificationService {
	
	@Autowired
	UserRepository repository;

    @Value("#{${app.notifications.defaults}}")
    private Map<String, String> defaults;

    private Logger logger = LoggerFactory.getLogger(PushNotificationService.class);
    private FCMService fcmService;

    public PushNotificationService(FCMService fcmService) {
        this.fcmService = fcmService;
    }

    @Scheduled(initialDelay = 10000, fixedDelay = 1 * 60 * 1000)
//    @Scheduled(cron = "${com.scheduled.cron}")
    public void sendDailyTeamExercisePush() {
    	System.out.println("+++++++++++++Code for sendDailyTeamExercisePush is being executed...");
    	
        try {
            fcmService.sendMessage(getAlertPayloadData(2), getExercisePushNotificationRequest());
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
    }
    
    @Scheduled(initialDelay = 2 * 60 * 1000, fixedDelay = 2 * 60 * 1000)
    public void sendTakeWaterPush() {
    	System.out.println("+++++++++++++Code for sendTakeWaterPush is being executed...");
    	
        try {
            fcmService.sendMessage(getAlertPayloadData(0), getWaterPushNotificationRequest());
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
    }

    public void sendPushNotification(PushNotificationRequest request) {
        try {
            fcmService.sendMessage(getSamplePayloadData(), request);
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
    }

    public void sendPushNotificationWithoutData(PushNotificationRequest request) {
        try {
            fcmService.sendMessageWithoutData(request);
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
    }


    public void sendPushNotificationToToken(PushNotificationRequest request) {
        try {
            fcmService.sendMessageToToken(request);
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
    }
    
    private Map<String, String> getAlertPayloadData(int alertType) {
        Map<String, String> pushData = new HashMap<>();
        pushData.put("messageId", "" + LocalDateTime.now());
        pushData.put("text", "" + alertType);
        return pushData;
    }
    
    private PushNotificationRequest getExercisePushNotificationRequest() {
        PushNotificationRequest request = new PushNotificationRequest("'CardaFit Aufgabe'",
                "Es ist Zeit für eine schnelle Übung",
                "team");
        return request;
    }
    
    private PushNotificationRequest getWaterPushNotificationRequest() {
        PushNotificationRequest request = new PushNotificationRequest("'CardaFit Aufgabe'",
                "Trinke jetzt ein Glas Wasser!",
                "team");
        return request;
    }


    private Map<String, String> getSamplePayloadData() {
        Map<String, String> pushData = new HashMap<>();
        pushData.put("messageId", " " + LocalDateTime.now());
        pushData.put("text", defaults.get("payloadData"));
        return pushData;
    }


    private PushNotificationRequest getSamplePushNotificationRequest() {
        PushNotificationRequest request = new PushNotificationRequest(defaults.get("title"),
                defaults.get("message"),
                defaults.get("topic"));
        return request;
    }


}
