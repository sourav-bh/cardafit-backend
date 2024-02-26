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
import org.springframework.beans.factory.annotation.Autowired;

import com.google.firebase.messaging.FirebaseMessagingException;

import uni.siegen.bgf.cardafit.firebase.FCMService;
import uni.siegen.bgf.cardafit.model.SentAlertInfo;
import uni.siegen.bgf.cardafit.model.User;
import uni.siegen.bgf.cardafit.repository.UserRepository;
import uni.siegen.bgf.cardafit.util.CommonUtil;

public class SendAlertTask implements Runnable {
    private Logger logger = LoggerFactory.getLogger(PushNotificationService.class);
    private static final DateFormat TWENTY_FOUR_TF = new SimpleDateFormat("HH:mm");
    private final double timeDurationBetweenAnyTwoAlert = 15*60*1000; // 15 minutes
    private final double timeDurationBetweenSameTwoAlert = 60*60*1000; // 60 minutes
    
    private User userInfo;
    private FCMService fcmService;
    
	UserRepository userRepository;
    
    public SendAlertTask(User userInfo, FCMService fcmService, UserRepository userRepository) {
        this.userInfo = userInfo;
        this.fcmService = fcmService;
        this.userRepository = userRepository;
    }
    
    @Override
    public void run() {
        String startTime = userInfo.getWorkStartTime();
        String endTime = userInfo.getWorkEndTime();
        
        String jobType = userInfo.getJobType();
        ArrayList<SentAlertInfo> sentAlerts = (ArrayList<SentAlertInfo>) userInfo.getSentAlerts();
        
        System.out.println(new Date().toString() + 
        			" >> SendAlertTask for user: " + userInfo.getUserName() + 
        			" >> Id: " + userInfo.getId() +
        			" >> jobType: " + jobType +
        			" >> prefAlerts: " + userInfo.getPreferredAlerts());
        
        // check if the user is a part timer, then only send 4 alerts of per type
        if (CommonUtil.isNotNullOrEmpty(jobType) && jobType.equalsIgnoreCase("Teilzeit") && 
        		(CommonUtil.isNullOrEmpty(startTime) || CommonUtil.isNullOrEmpty(endTime))) {
        	// don't sent two consecutive alerts in 15 minutes duration
            if (System.currentTimeMillis() - userInfo.getLastAlertSentTime() > timeDurationBetweenAnyTwoAlert) {
            	System.out.println("Alert can be sent for part-timer, user: " + userInfo.getUserName());
            	
            	for (int i=0 ; i<sentAlerts.size() ; i++) {
        			SentAlertInfo sentAlert = sentAlerts.get(i);
        			
        			// don't sent two consecutive alerts of same type in 60 minutes duration
                	// send only half number of alerts as the worker is a part-timer
        			if (System.currentTimeMillis() - sentAlert.getLastSentAt() > timeDurationBetweenSameTwoAlert && 
        					CommonUtil.isNotNullOrEmpty(userInfo.getDeviceToken()) && 
        					sentAlert.getSentCount() < 4) {
        				userInfo.setLastAlertSentTime(System.currentTimeMillis());
        				userInfo.getSentAlerts().get(i).setSentCount(userInfo.getSentAlerts().get(i).getSentCount() + 1);
        				userInfo.getSentAlerts().get(i).setLastSentAt((System.currentTimeMillis()));
        				userRepository.save(userInfo);
        				
        				sendTaskAlertPushWithOnlyData(sentAlert.getAlertType(), userInfo.getDeviceToken());
        				break;
        			}
        		}
            } else {
            	System.out.println("Alert cannot be sent now, due to the constraint: timeDurationBetweenAnyTwoAlert");
            }
        } else {
        	// set default times if user hasn't set any start and/or end time
        	if (CommonUtil.isNullOrEmpty(startTime)) startTime = "09:00";
        	if (CommonUtil.isNullOrEmpty(endTime)) endTime = "18:00";
        	
        	LocalTime currentTime = LocalTime.parse(TWENTY_FOUR_TF.format(Calendar.getInstance().getTime())) ;
            boolean isCurrentTimeInTargetPeriod = (currentTime.isAfter(LocalTime.parse(startTime)) && 
            		currentTime.isBefore(LocalTime.parse(endTime)));
            
            // don't sent two consecutive alerts in 15 minutes duration
            if (isCurrentTimeInTargetPeriod && 
            		System.currentTimeMillis() - userInfo.getLastAlertSentTime() > timeDurationBetweenAnyTwoAlert) {
            	System.out.println("Alert can be sent, current time is into the set time period for user: " + userInfo.getUserName());
            	
            	for (int i=0 ; i<sentAlerts.size() ; i++) {
        			SentAlertInfo sentAlert = sentAlerts.get(i);
        			
        			// don't sent two consecutive alerts of same type in 60 minutes duration
        			if (System.currentTimeMillis() - sentAlert.getLastSentAt() > timeDurationBetweenSameTwoAlert && 
        					CommonUtil.isNotNullOrEmpty(userInfo.getDeviceToken())) {
        				userInfo.setLastAlertSentTime(System.currentTimeMillis());
        				userInfo.getSentAlerts().get(i).setSentCount(userInfo.getSentAlerts().get(i).getSentCount() + 1);
        				userInfo.getSentAlerts().get(i).setLastSentAt((System.currentTimeMillis()));
        				userRepository.save(userInfo);
        				
        				sendTaskAlertPushWithOnlyData(sentAlert.getAlertType(), userInfo.getDeviceToken());
        				break;
        			}
        		}
            } else {
            	System.out.println("Alert cannot be sent now, due to the constraint: timeDurationBetweenAnyTwoAlert");
            }
        }
        
    }
    
    public void sendTaskAlertPushWithOnlyData(int taskType, String token) {
    	System.out.println("+++++++++++++Send TaskAlert Push with only data...");
    	
        try {
            fcmService.sendMessageToTokenWithOnlyData(CommonUtil.getAlertPayloadData(taskType), token, "team");
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        } catch (FirebaseMessagingException e) {
            logger.error("Firebase Notification Failed:" + e.getMessage());
        }
    }
}