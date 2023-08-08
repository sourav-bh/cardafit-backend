package uni.siegen.bgf.cardafit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uni.siegen.bgf.cardafit.repository.UserRepository;

@Service
public class AppService {
	
	@Autowired
	UserRepository userRepository;


}
