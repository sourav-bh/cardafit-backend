package uni.siegen.bgf.cardafit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import uni.siegen.bgf.cardafit.model.PushNotificationRequest;
import uni.siegen.bgf.cardafit.model.PushNotificationResponse;
import uni.siegen.bgf.cardafit.repository.UserRepository;
import uni.siegen.bgf.cardafit.service.PushNotificationService;

@RestController
public class PushNotificationController {
	@Autowired
	private Environment env;
	
	@Autowired
	UserRepository repository;

    private PushNotificationService pushNotificationService;

    public PushNotificationController(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @PostMapping("/notification/topic")
    public ResponseEntity<PushNotificationResponse> sendNotification(@RequestBody PushNotificationRequest request) {
        pushNotificationService.sendPushNotificationWithoutData(request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @PostMapping("/notification/token")
    public ResponseEntity<PushNotificationResponse> sendTokenNotification(@RequestBody PushNotificationRequest request) {
        pushNotificationService.sendPushNotificationToToken(request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @PostMapping("/notification/data")
    public ResponseEntity<PushNotificationResponse> sendDataNotification(@RequestBody PushNotificationRequest request) {
        pushNotificationService.sendPushNotification(request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @GetMapping("/team/notification")
    public ResponseEntity<PushNotificationResponse> sendSampleNotification() {
        pushNotificationService.sendDailyTeamExerciseAlert();
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }
    
    @GetMapping("/properties")
    public ResponseEntity<PushNotificationResponse> printProperties() {
        String cronTime = env.getProperty("com.scheduled.cron");
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Cron job is scheduled at: " + cronTime), HttpStatus.OK);
    }
    
    @GetMapping("/findAll")
    public ResponseEntity<PushNotificationResponse> findAllUsers() {
        int size = repository.findAll().size();
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "User count: " + size), HttpStatus.OK);
    }
    
    @GetMapping("/deleteAll")
    public ResponseEntity<PushNotificationResponse> clearAllUsers() {
        repository.deleteAll();
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "All users deleted successfully"), HttpStatus.OK);
    }
}
