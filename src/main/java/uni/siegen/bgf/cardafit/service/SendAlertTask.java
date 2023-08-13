package uni.siegen.bgf.cardafit.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.firebase.messaging.FirebaseMessagingException;

import uni.siegen.bgf.cardafit.firebase.FCMService;
import uni.siegen.bgf.cardafit.model.PushNotificationRequest;
import uni.siegen.bgf.cardafit.model.SentAlertInfo;
import uni.siegen.bgf.cardafit.model.User;
import uni.siegen.bgf.cardafit.util.CommonUtil;

public class SendAlertTask implements Runnable {
    private Logger logger = LoggerFactory.getLogger(PushNotificationService.class);
    private static final DateFormat TWENTY_FOUR_TF = new SimpleDateFormat("HH:mm");
    
    private User userInfo;
    private FCMService fcmService;
    
    public SendAlertTask(User userInfo, FCMService fcmService) {
        this.userInfo = userInfo;
        this.fcmService = fcmService;
    }
    
    @Override
    public void run() {
        System.out.println(new Date()+"Send alert Task for user: " + userInfo.getUserName()
          +" on thread "+Thread.currentThread().getName());
        
        String startTime = userInfo.getWorkStartTime();
        String endTime = userInfo.getWorkEndTime();
        String jobType = userInfo.getJobType();
        ArrayList<SentAlertInfo> sentAlerts = (ArrayList<SentAlertInfo>) userInfo.getSentAlerts();
        
        startTime = "03:36"; // test code
        
        LocalTime currentTime = LocalTime.parse(TWENTY_FOUR_TF.format(Calendar.getInstance().getTime())) ;
        boolean isCurrentTimeInTargetPeriod = (currentTime.isAfter(LocalTime.parse(startTime)) && 
        		currentTime.isBefore( LocalTime.parse(endTime)));
        
        // don't sent two consecutive alerts in 15 minutes duration
        if (isCurrentTimeInTargetPeriod && 
        		System.currentTimeMillis() - userInfo.getLastAlertSentTime() > 15*60*1000) {
        	System.out.println("Alert can be sent, current time is into the set time period for user: " + userInfo.getUserName());
        	
        	for (int i=0 ; i<sentAlerts.size() ; i++) {
    			SentAlertInfo sentAlert = sentAlerts.get(i);
    			
    			// don't sent two consecutive alerts of same type in 60 minutes duration
    			if (System.currentTimeMillis() - sentAlert.getLastSentAt() > 60*60*1000) {
    				userInfo.setLastAlertSentTime(System.currentTimeMillis());
    				userInfo.getSentAlerts().get(i).setLastSentAt((System.currentTimeMillis()));
    				
    				sendTaskAlertPushWithOnlyData(sentAlert.getAlertType(), userInfo.getDeviceToken());
    				break;
    			}
    		}
        }
        
        // TODO: check if the user is a part timer, then only send 4 alerts of per type
        if (CommonUtil.isNotNullOrEmpty(jobType) && jobType.equalsIgnoreCase("Teilzeit")) {
        	// send only half number of alerts
        }
    }
    
    public void sendTaskAlertPushWithOnlyData(int taskType, String token) {
    	System.out.println("+++++++++++++Send TaskAlert Push with only data...");
    	
        try {
            fcmService.sendMessageToTokenWithOnlyData(CommonUtil.getAlertPayloadData(taskType), getTaskAlertNotificationRequest(taskType, token));
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        } catch (FirebaseMessagingException e) {
            logger.error("Firebase Notification Failed:" + e.getMessage());
       }
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
		case 5:
			message = "Machen Sie eine kurze Pause und trinken Sie auch ein Glas Wasser!";
			break;
		case 6:
			message = "Gehen Sie eine Weile spazieren und strecken Sie Ihre Hände ein wenig!";
			break;

		default:
			break;
		}
    	
        PushNotificationRequest request = new PushNotificationRequest("'CardaFit Aufgabe'", message, "team");
        request.setToken(token);
        return request;
    }
}