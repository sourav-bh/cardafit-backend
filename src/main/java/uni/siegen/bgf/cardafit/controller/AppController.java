package uni.siegen.bgf.cardafit.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uni.siegen.bgf.cardafit.model.CommonResponse;
import uni.siegen.bgf.cardafit.model.Feedback;
import uni.siegen.bgf.cardafit.model.LoginResponse;
import uni.siegen.bgf.cardafit.model.SentAlertInfo;
import uni.siegen.bgf.cardafit.model.Team;
import uni.siegen.bgf.cardafit.model.User;
import uni.siegen.bgf.cardafit.model.UserListResponse;
import uni.siegen.bgf.cardafit.model.UserLoginRequest;
import uni.siegen.bgf.cardafit.repository.FeedbackRepository;
import uni.siegen.bgf.cardafit.repository.TeamRepository;
import uni.siegen.bgf.cardafit.repository.UserRepository;
import uni.siegen.bgf.cardafit.service.AppService;
import uni.siegen.bgf.cardafit.util.CommonUtil;
import uni.siegen.bgf.cardafit.util.CryptUtil;

@RestController
public class AppController {
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	FeedbackRepository feedbackRepository;
	
	@Autowired
	TeamRepository teamRepository;

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
    	User user = userRepository.findByUserName(request.getUserName());
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
    	User user = userRepository.findByUserName(userName);
		if (user != null) {
			return new ResponseEntity<>(new CommonResponse(HttpStatus.CONFLICT.value(), "UserName already taken"), HttpStatus.CONFLICT);
		}
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK.value(), "UserName is available"), HttpStatus.OK);
    }
    
    @GetMapping("/users")
    public ResponseEntity<CommonResponse> getUsersByTeam(@RequestParam String teamName) {
    	List<User> users = userRepository.findByTeamName(teamName);
		if (users != null && !users.isEmpty()) {
			return new ResponseEntity<>(new UserListResponse(HttpStatus.OK.value(), "Success", users), HttpStatus.OK);
		}
        return new ResponseEntity<>(new UserListResponse(HttpStatus.NOT_FOUND.value(), "No user found in this team", new ArrayList<>()), HttpStatus.NOT_FOUND);
    }
    
    @GetMapping("/team/check")
    public ResponseEntity<CommonResponse> checkIfTeamIsActive(@RequestParam String teamName) {
    	Team team = teamRepository.findByTeamName(teamName);
		if (team != null && team.isActive()) {
			return new ResponseEntity<>(new CommonResponse(HttpStatus.OK.value(), "Team is active"), HttpStatus.OK);
		}
        return new ResponseEntity<>(new CommonResponse(HttpStatus.NOT_ACCEPTABLE.value(), "Team is inactive for use right now!"), HttpStatus.NOT_ACCEPTABLE);
    }
    
    @GetMapping("/sent-alerts")
    public ResponseEntity<List<SentAlertInfo>> getSentAlertsForUser(@RequestParam(required = false) String userName, @RequestParam(required = false) String userId) {
    	Optional<User> user = null;
    	// run search in user repo to see if there is any existing user with the same userName or userId
    	if (CommonUtil.isNotNullOrEmpty(userName)) {
    		user = Optional.of(userRepository.findByUserName(userName));
    	} else if (CommonUtil.isNotNullOrEmpty(userId)) {
    		user = userRepository.findById(userId);
    	} 
    	
		if (user != null && user.isPresent()) {
			return new ResponseEntity<>(user.get().getSentAlerts(), HttpStatus.OK);
		}
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }
    
    @GetMapping("/feedbackByUser")
    public ResponseEntity<List<Feedback>> getUserFeedback(@RequestParam String userId) {
    	// run search in user repo to see if there is any existing user with the same userName or userId
    	if (CommonUtil.isNotNullOrEmpty(userId)) {
    		return new ResponseEntity<>(feedbackRepository.findByUserId(userId), HttpStatus.OK);
    	}
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }
}
