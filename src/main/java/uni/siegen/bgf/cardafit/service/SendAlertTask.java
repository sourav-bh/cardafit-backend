package uni.siegen.bgf.cardafit.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

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
    
	UserRepository userRepository;
    
    public SendAlertTask(User userInfo, UserRepository userRepository) {
        this.userInfo = userInfo;
        this.userRepository = userRepository;
    }
    
    @Override
    public void run() {
        String startTime = userInfo.getWorkStartTime();
        String endTime = userInfo.getWorkEndTime();
        String jobType = userInfo.getJobType();
        ArrayList<SentAlertInfo> sentAlerts = userInfo.getSentAlerts();
        
        // set default times if user hasn't set any start and/or end time.
        // also check if the user is a part timer, then set the start or end time accordingly to make total of 4 working hours.
    	if (CommonUtil.isNullOrEmpty(startTime)) {
    		startTime = "09:00";
    		if (jobType.equalsIgnoreCase("Teilzeit") && CommonUtil.isNullOrEmpty(endTime)) {
    			endTime = "13:00";
    		}
    	}
    	if (CommonUtil.isNullOrEmpty(endTime)) {
    		if (jobType.equalsIgnoreCase("Teilzeit")) {
    			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
    			LocalDateTime localStartTime = LocalDateTime.parse(startTime, formatter);
    			LocalDateTime localEndTime = localStartTime.plusHours(4);
    			String localEndTimeStr = localEndTime.format(formatter);    					
    			endTime = localEndTimeStr;
    		} else {
    			endTime = "18:00";
    		}
    	}
    	
    	LocalTime currentTime = LocalTime.parse(TWENTY_FOUR_TF.format(Calendar.getInstance().getTime())) ;
        boolean isCurrentTimeInTargetPeriod = (currentTime.isAfter(LocalTime.parse(startTime)) && 
        		currentTime.isBefore(LocalTime.parse(endTime)));
        
        // don't sent two consecutive alerts in 15 minutes duration
        if (isCurrentTimeInTargetPeriod && 
        		System.currentTimeMillis() - userInfo.getLastAlertSentTime() > timeDurationBetweenAnyTwoAlert) {
        	
        	for (int i=0 ; i<sentAlerts.size() ; i++) {
    			SentAlertInfo sentAlert = sentAlerts.get(i);
    			
    			// don't sent two consecutive alerts of same type in 60 minutes duration
    			if (System.currentTimeMillis() - sentAlert.getLastSentAt() > timeDurationBetweenSameTwoAlert) {
    				userInfo.setLastAlertSentTime(System.currentTimeMillis());
    				userInfo.getSentAlerts().get(i).setSentCount(userInfo.getSentAlerts().get(i).getSentCount() + 1);
    				userInfo.getSentAlerts().get(i).setLastSentAt((System.currentTimeMillis()));
    				userRepository.save(userInfo);
    				
    				sendTaskAlertPushWithOnlyData(sentAlert.getAlertType(), userInfo.getDeviceToken());
    				break;
    			}
    		}
        } else {
        	logger.info("SendAlertTask for user: " + userInfo.getUserName() + " cancelled due to constraint of "
        			 + (isCurrentTimeInTargetPeriod == false ? "start/end time" : "timeDurationBetweenAnyTwoAlert"));
        }
        
    }
    
    public void sendTaskAlertPushWithOnlyData(int taskType, String token) {
        try {
            sendMessageToTokenWithOnlyData(CommonUtil.getAlertPayloadData(taskType), token, "team");
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        } catch (FirebaseMessagingException e) {
            logger.error("Firebase Notification Failed:" + e.getMessage());
        }
    }
    
    private void sendMessageToTokenWithOnlyData(Map<String, String> data, String token, String topic)
            throws InterruptedException, ExecutionException, FirebaseMessagingException {
        Message message = getPreconfiguredMessageToTokenWithOnlyData(data, token, topic);
    	String response = sendAndGetResponse(message);
    	logger.info("Sent message with only data. For User:" + userInfo.getUserName() + ";Token: " + token + ", " + response);
    	if (response.length() > 0 && response.equalsIgnoreCase("-1")) {
    		userInfo.setDeviceToken("");
    		userRepository.save(userInfo);
    	}
    }

    private String sendAndGetResponse(Message message) throws InterruptedException, ExecutionException {
        try {
			return FirebaseMessaging.getInstance().send(message);
		} catch (FirebaseMessagingException e) {
			e.printStackTrace();
			if (e.getErrorCode().equals("messaging/registration-token-not-registered") 
					|| e.getErrorCode().equals("INVALID_ARGUMENT") || e.getErrorCode().equals("UNREGISTERED")) {
				return "-1";
			}
		}
        return "";
    }
    
    private AndroidConfig getAndroidConfigOnlyData(String topic) {
        return AndroidConfig.builder()
                .setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(topic)
                .setPriority(AndroidConfig.Priority.HIGH).build();
    }

    private ApnsConfig getApnsConfig(String topic) {
        return ApnsConfig.builder()
                .setAps(Aps.builder().setCategory(topic).setThreadId(topic)
                		.setContentAvailable(true).setMutableContent(true)
                		.putCustomData("interruption-level", "time-sensitive").build())
                .putHeader("apns-push-type", "background")
                .putHeader("apns-priority", "10")
                .putHeader("apns-topic", "de.uni-siegen.bgf.cardafit")
                .build();
    }
    
    private Message getPreconfiguredMessageToTokenWithOnlyData(Map<String, String> data, String token, String topic) {
        return getPlatformConfigForOnlyDataMessage(topic)
        		.putAllData(data)
        		.setToken(token)
                .build();
    }
    
    private Message.Builder getPlatformConfigForOnlyDataMessage(String topic) {
        AndroidConfig androidConfig = getAndroidConfigOnlyData(topic);
        ApnsConfig apnsConfig = getApnsConfig(topic);
        return Message.builder().setApnsConfig(apnsConfig).setAndroidConfig(androidConfig);
    }
}