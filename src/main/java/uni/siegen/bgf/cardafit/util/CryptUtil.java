package uni.siegen.bgf.cardafit.util;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CryptUtil {
	
	private static CryptUtil instance = new CryptUtil();

	private CryptUtil() {}

	public static CryptUtil getInstance() {
		return instance;
	}
	
	public String getBCrypt(String input) {
		BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
		return bcryptPasswordEncoder.encode(input);
	}
	
	public boolean checkPw(String plaintext, String hashed) {
		return BCrypt.checkpw(plaintext, hashed);
	}

}

