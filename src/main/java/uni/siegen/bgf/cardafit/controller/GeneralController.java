package uni.siegen.bgf.cardafit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import uni.siegen.bgf.cardafit.model.CommonResponse;
import uni.siegen.bgf.cardafit.repository.UserRepository;
import uni.siegen.bgf.cardafit.service.PushNotificationService;

@RestController
public class GeneralController {
	
	@Autowired
	private Environment env;
	
	@Autowired
	UserRepository repository;
	
	public GeneralController() {
        
    }
	
	@GetMapping("/properties")
    public ResponseEntity<CommonResponse> printProperties() {
        String cronTime = env.getProperty("com.scheduled.cron");
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK.value(), "Cron job is scheduled at: " + cronTime), HttpStatus.OK);
    }
    
    @GetMapping("/findAll")
    public ResponseEntity<CommonResponse> findAllUsers() {
        int size = repository.findAll().size();
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK.value(), "User count: " + size), HttpStatus.OK);
    }
    
    @GetMapping("/deleteAll")
    public ResponseEntity<CommonResponse> clearAllUsers() {
        repository.deleteAll();
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK.value(), "All users deleted successfully"), HttpStatus.OK);
    }
    
    @GetMapping("/test")
    public ResponseEntity<CommonResponse> testMethod() {
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK.value(), "Test is successful"), HttpStatus.OK);
    }

}
