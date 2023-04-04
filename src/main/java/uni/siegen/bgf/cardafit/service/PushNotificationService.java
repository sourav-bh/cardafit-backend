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
import uni.siegen.bgf.cardafit.repository.AppInMemoryRepository;
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
	UserRepository userRepository;

    @Value("#{${app.notifications.defaults}}")
    private Map<String, String> defaults;

    private Logger logger = LoggerFactory.getLogger(PushNotificationService.class);
    private FCMService fcmService;

    public PushNotificationService(FCMService fcmService) {
        this.fcmService = fcmService;
    }
    
    @Scheduled(cron = "${daily.team.exercise.scheduled.cron}")
    public void sendDailyTeamExerciseAlert() {
    	System.out.println("+++++++++++++Code for sendDailyTeamExerciseAlert is being executed...");
  	
    	try {
            fcmService.sendMessage(getAlertPayloadData(4), getTeamTaskAlertNotificationRequest());
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        }
    }
    
    @Scheduled(cron = "${daily.task.scheduled.cron}")
    public void scheduleTaskAlerts() {
    	System.out.println("+++++++++++++Code for scheduleTaskAlerts is being executed...");
    	
    	int scheduleCount = AppInMemoryRepository.getInstance().getScheduleCount();
    	if (scheduleCount % 4 == 1) {
    		sendTaskAlert(0);
    	} else if (scheduleCount % 4 == 2) {
    		sendTaskAlert(1);
    	} else if (scheduleCount % 4 == 3) {
    		sendTaskAlert(2);
    	} else if (scheduleCount % 4 == 4) {
    		sendTaskAlert(3);
    	}
    	AppInMemoryRepository.getInstance().updateScheduleCount();
    }
    
//    @Scheduled(initialDelay = 10 * 60 * 1000, fixedDelay = 60 * 60 * 1000)
    public void sendTaskAlert(int taskType) {
    	System.out.println("+++++++++++++Code for sendTaskAlert is being executed...");
    	
    	List<User> allUsers = userRepository.findAll();
    	for (int i=0; i<allUsers.size(); i++) {
    		String token = allUsers.get(i).getDeviceToken();
    		if (token != null && !token.isEmpty()) {
    			try {
    			    Thread.sleep(i * 10 * 1000);
    			} catch (InterruptedException ie) {
    			    Thread.currentThread().interrupt();
    			}
    			sendTaskAlertPushNotification(taskType, token);
    		}
    	}
        
    }
    
    public void sendTaskAlertPushNotification(int taskType, String token) {
    	System.out.println("+++++++++++++Code for sendTaskAlertPushNotification is being executed...");
    	
        try {
            fcmService.sendMessageToTokenWithData(getAlertPayloadData(taskType), getTaskAlertNotificationRequest(taskType, token));
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
    
    private PushNotificationRequest getTaskAlertNotificationRequest(int taskType, String token) {
    	String message = "";
    	switch (taskType) {
		case 0:
			message = "Trinke jetzt ein Glas Wasser!";
			break;
		case 1:
			message = "Jetzt 100 Schritte gehen!";
			break;
		case 2:
			message = "Es ist Zeit für eine schnelle Übung!";
			break;
		case 3:
			message = "Du solltest jetzt eine Pause einlegen!";
			break;
		case 4:
			message = "Es ist Zeit für eine Teamübung!";
			break;

		default:
			break;
		}
    	
        PushNotificationRequest request = new PushNotificationRequest("'CardaFit Aufgabe'", message, "team");
        request.setToken(token);
        return request;
    }
    
    private PushNotificationRequest getTeamTaskAlertNotificationRequest() {
    	PushNotificationRequest request = new PushNotificationRequest("'CardaFit Aufgabe'", "Es ist Zeit für eine Teamübung!", "team");
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
