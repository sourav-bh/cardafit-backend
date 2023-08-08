package uni.siegen.bgf.cardafit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uni.siegen.bgf.cardafit.model.CommonResponse;
import uni.siegen.bgf.cardafit.model.PushNotificationRequest;
import uni.siegen.bgf.cardafit.service.PushNotificationService;

@RestController
public class PushNotificationController {
    private PushNotificationService pushNotificationService;

    public PushNotificationController(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @PostMapping("/notification/topic")
    public ResponseEntity<CommonResponse> sendNotification(@RequestBody PushNotificationRequest request) {
        pushNotificationService.sendPushNotificationWithoutData(request);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @PostMapping("/notification/token")
    public ResponseEntity<CommonResponse> sendTokenNotification(@RequestBody PushNotificationRequest request) {
        pushNotificationService.sendPushNotificationToToken(request);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @PostMapping("/notification/data")
    public ResponseEntity<CommonResponse> sendDataNotification(@RequestBody PushNotificationRequest request) {
        pushNotificationService.sendPushNotification(request);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @GetMapping("/notification/team")
    public ResponseEntity<CommonResponse> sendTeamExerciseNotification() {
        pushNotificationService.sendDailyTeamExerciseAlert();
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }
    
    @GetMapping("/notification/test")
    public ResponseEntity<CommonResponse> sendSampleNotification(@RequestParam String userName, @RequestParam int alertType) {
        pushNotificationService.sendTestExerciseAlert(userName, alertType);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }
}
