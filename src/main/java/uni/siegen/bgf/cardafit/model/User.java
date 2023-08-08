package uni.siegen.bgf.cardafit.model;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uni.siegen.bgf.cardafit.util.CryptUtil;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class User {
	@Id 
	private String id;

	private String userName;
	private String avatarImage;
	private String deviceToken;
	private String password;
	
	private int age;
	private String gender;
	private int weight;
	private int height;
	
	private String jobPosition;
	private String jobType;
	private String workingDays; // comma separated days as 2-letter initial
	private String workStartTime;
	private String workEndTime;
	
	private String medicalConditions; // comma separated conditions string
	private String diseases; // comma separated disease string
	
	private String preferredAlerts; // comma separated string containing alert type enum values
	private boolean isMergedAlertSet; // indicate if the user wants to merge the alert types, like water + break and exercise + steps
	
	private int score = 0;

	public void setScore(int score) {
		this.score += score;
	}
	
	public void setPassword(String password) {
		this.password = CryptUtil.getInstance().getBCrypt(password);
	}

}
