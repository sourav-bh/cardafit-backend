package uni.siegen.bgf.cardafit.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessagingException;

import uni.siegen.bgf.cardafit.firebase.FCMService;
import uni.siegen.bgf.cardafit.model.SentAlertInfo;
import uni.siegen.bgf.cardafit.model.User;
import uni.siegen.bgf.cardafit.repository.UserRepository;
import uni.siegen.bgf.cardafit.util.CommonUtil;

@Service
public class PushNotificationService {
	
	@Autowired
	UserRepository userRepository;

    @Value("#{${app.notifications.defaults}}")
    private Map<String, String> defaults;

    private Logger logger = LoggerFactory.getLogger(PushNotificationService.class);
    private FCMService fcmService;
    
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    public PushNotificationService(FCMService fcmService) {
        this.fcmService = fcmService;
    }
    
    @Scheduled(cron = "${daily.team.exercise.scheduled.cron}")
    public void sendDailyTeamExerciseAlert() {
    	System.out.println("+++++++++++++Code for sendDailyTeamExerciseAlert is being executed...");
  	
    	try {
    		fcmService.sendMessageToAllWithOnlyData(CommonUtil.getAlertPayloadData(4), "team");
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
        } catch (FirebaseMessagingException e) {
            logger.error("Firebase Notification Failed:" + e.getMessage());
        }
    }
    
    @Scheduled(cron = "${daily.task.scheduled.cron}")
    public void scheduleTaskAlertsBasedOnUserPref() {
    	System.out.println("+++++++++++++Code for scheduleTaskAlerts based on user preference is being executed...");
    	
    	List<User> allUsers = userRepository.findAll();
    	for (int i=0; i<allUsers.size(); i++) {
    		User userInfo = allUsers.get(i);
    		String workingDays = userInfo.getWorkingDays();
            String currentDay = CommonUtil.getCurrentWeekDayName();
            
            logger.debug("scheduleTaskAlertsBasedOnUserPref >> user: " + userInfo.getId() + ">> workingDays: " + workingDays + ">> currentDay: " + currentDay);
            if (CommonUtil.isNullOrEmpty(workingDays) && CommonUtil.isNotNullOrEmpty(userInfo.getDeviceToken())) {
            	taskScheduler.schedule(new SendAlertTask(allUsers.get(i), fcmService, userRepository), new Date(System.currentTimeMillis() + 10));
            } 
            else if (CommonUtil.isNotNullOrEmpty(workingDays) && workingDays.contains(currentDay) &&
            		CommonUtil.isNotNullOrEmpty(userInfo.getDeviceToken())) {
            	taskScheduler.schedule(new SendAlertTask(allUsers.get(i), fcmService, userRepository), new Date(System.currentTimeMillis() + 10));
            }
    	}
    }
    
    @Scheduled(cron = "${daily.start.day.scheduled.cron}")
	public void resetDailyCounter() {
		// reset all users alert count and times
    	List<User> allUsers = userRepository.findAll();
    	for (int i=0; i<allUsers.size(); i++) {
    		User userInfo = allUsers.get(i);
    		userInfo.setLastAlertSentTime(0);
    		userInfo.resetSentAlerts();
    		userRepository.save(userInfo);
    	}
	}

    public void sendTestExerciseAlert(String userName, int alertType) {
//    	taskScheduler.schedule(new SendAlertTask("scheduled task"), new Date(System.currentTimeMillis() + 10));
//    	taskScheduler.scheduleAtFixedRate(new SendAlertTask("scheduled task"), 1000);
  	
    	User user = userRepository.findByUserName(userName);
    	if (user != null) {
    		ArrayList<SentAlertInfo> alertPrefs = (ArrayList<SentAlertInfo>) user.getSentAlerts();
    		System.out.printf("Alert pref size: %d\n", alertPrefs.size());
    		SentAlertInfo alertPref = alertPrefs.get(1);
    		System.out.printf("+++++++++++++Code for test alert is being executed...for user: %s and type: %d\n", userName, alertPref.getAlertType());
    		
    		try {
                fcmService.sendMessageToTokenWithOnlyData(CommonUtil.getAlertPayloadData(alertPref.getAlertType()), user.getDeviceToken(), "team");
            } catch (InterruptedException | ExecutionException e) {
                logger.error(e.getMessage());
            } catch (FirebaseMessagingException e) {
                logger.error("Firebase Notification Failed:" + e.getMessage());
           }
    	}
    }

}
