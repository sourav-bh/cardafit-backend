package uni.siegen.bgf.cardafit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uni.siegen.bgf.cardafit.model.CommonResponse;
import uni.siegen.bgf.cardafit.service.PushNotificationService;

@RestController
public class PushNotificationController {
    private PushNotificationService pushNotificationService;

    public PushNotificationController(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
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