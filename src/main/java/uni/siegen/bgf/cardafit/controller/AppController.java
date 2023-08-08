package uni.siegen.bgf.cardafit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uni.siegen.bgf.cardafit.model.CommonResponse;
import uni.siegen.bgf.cardafit.model.LoginResponse;
import uni.siegen.bgf.cardafit.model.User;
import uni.siegen.bgf.cardafit.model.UserLoginRequest;
import uni.siegen.bgf.cardafit.repository.UserRepository;
import uni.siegen.bgf.cardafit.service.AppService;
import uni.siegen.bgf.cardafit.util.CryptUtil;

@RestController
public class AppController {
	@Autowired
	UserRepository repository;

    private AppService appService;

    public AppController(AppService appService) {
        this.appService = appService;
    }
    
    @GetMapping("/index")
    public ResponseEntity<CommonResponse> printProperties() {
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK.value(), "Welcome to Carda-Fit"), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse> loginUser(@RequestBody UserLoginRequest request) {
    	User user = repository.findByUserName(request.getUserName());
		if (user != null) {
			if (CryptUtil.getInstance().checkPw(request.getPassword(), user.getPassword())) {
				return new ResponseEntity<>(new LoginResponse(HttpStatus.OK.value(), "User login successful.", user), HttpStatus.OK);
			}
		}
    	
        return new ResponseEntity<>(new CommonResponse(HttpStatus.UNAUTHORIZED.value(), "User login failed, check username and password!"), HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/check-userName")
    public ResponseEntity<CommonResponse> checkUserNameAvailability(@RequestParam String userName) {
        // run search in user repo to see if there is any existing user with the same userName
    	User user = repository.findByUserName(userName);
		if (user != null) {
			return new ResponseEntity<>(new CommonResponse(HttpStatus.CONFLICT.value(), "UserName already taken"), HttpStatus.CONFLICT);
		}
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK.value(), "UserName is available"), HttpStatus.OK);
    }
}
