package uni.siegen.bgf.cardafit.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uni.siegen.bgf.cardafit.util.CommonUtil;
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
	private String teamName;
	
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
	
	/*
	 * AlertType - 0 : water
	 * AlertType - 1 : steps
	 * AlertType - 2 : exercise
	 * AlertType - 3 : breaks
	 * AlertType - 4 : teamExercise
	 * AlertType - 5 : waterWithBreak
	 * AlertType - 0 : walkWithExercise
	 */
	private String preferredAlerts; // comma separated string containing alert type enum values
	
	private int score = 0;
	
	// alert sending related info
	private long lastAlertSentTime = 0;
	private List<SentAlertInfo> sentAlerts;

	public void setScore(int score) {
		this.score += score;
	}
	
	public void setPassword(String password) {
		this.password = CryptUtil.getInstance().getBCrypt(password);
	}
	
	public void setWorkStartTime(String startTime) {
		if (CommonUtil.isNotNullOrEmpty(startTime)) {
			this.workStartTime = startTime;
		} else {
			this.workStartTime = "09:00";
		}
	}
	
	public String getWorkStartTime() {
		if (!CommonUtil.isNotNullOrEmpty(workStartTime)) {
			this.workStartTime = "09:00";
		}
		
		return workStartTime;
	}
	
	public void setWorkEndTime(String endTime) {
		if (CommonUtil.isNotNullOrEmpty(endTime)) {
			this.workEndTime = endTime;
		} else {
			this.workEndTime = "17:00";
		}
	}
	
	public String getWorkEndTime() {
		if (!CommonUtil.isNotNullOrEmpty(workEndTime)) {
			this.workEndTime = "17:00";
		}
		
		return workEndTime;
	}
	
	public void setPreferredAlerts(String preferredAlerts) {
		this.preferredAlerts = preferredAlerts;
		
		if (CommonUtil.isNotNullOrEmpty(preferredAlerts)) {
			this.sentAlerts = new ArrayList<>();
			
			String[] prefAlerts = preferredAlerts.split(",");
			for (int i=0 ;i<prefAlerts.length ; i++) {
				String alertSet = prefAlerts[i].trim();
				switch (alertSet) {
				case "water":
					sentAlerts.add(new SentAlertInfo(0, 0, 0));
					break;
				case "steps":
					sentAlerts.add(new SentAlertInfo(1, 0, 0));
					break;
				case "exercise":
					sentAlerts.add(new SentAlertInfo(2, 0, 0));
					break;
				case "breaks":
					sentAlerts.add(new SentAlertInfo(3, 0, 0));
					break;
				case "waterWithBreak":
					sentAlerts.add(new SentAlertInfo(5, 0, 0));
					break;
				case "walkWithExercise":
					sentAlerts.add(new SentAlertInfo(6, 0, 0));
					break;

				default:
					break;
				}
			}
		}
	}
	
	public List<SentAlertInfo> getSentAlerts() {
		if (this.sentAlerts == null) {
			this.sentAlerts = new ArrayList<>();
			
			if (CommonUtil.isNotNullOrEmpty(preferredAlerts)) {
				String[] prefAlerts = preferredAlerts.split(",");
				for (int i=0 ;i<prefAlerts.length ; i++) {
					String alertSet = prefAlerts[i].trim();
					switch (alertSet) {
					case "water":
						sentAlerts.add(new SentAlertInfo(0, 0, 0));
						break;
					case "steps":
						sentAlerts.add(new SentAlertInfo(1, 0, 0));
						break;
					case "exercise":
						sentAlerts.add(new SentAlertInfo(2, 0, 0));
						break;
					case "breaks":
						sentAlerts.add(new SentAlertInfo(3, 0, 0));
						break;
					case "waterWithBreak":
						sentAlerts.add(new SentAlertInfo(5, 0, 0));
						break;
					case "walkWithExercise":
						sentAlerts.add(new SentAlertInfo(6, 0, 0));
						break;

					default:
						break;
					}
				}
			} else {
				sentAlerts.add(new SentAlertInfo(0, 0, 0));
				sentAlerts.add(new SentAlertInfo(1, 0, 0));
				sentAlerts.add(new SentAlertInfo(2, 0, 0));
				sentAlerts.add(new SentAlertInfo(3, 0, 0));
			}
		}
		
		
		return this.sentAlerts;
	}

}
