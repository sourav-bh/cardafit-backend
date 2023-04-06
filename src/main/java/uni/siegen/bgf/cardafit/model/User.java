package uni.siegen.bgf.cardafit.model;

import org.springframework.data.annotation.Id;

public class User {
	@Id private String id;

	private String userName;
	private String avatarName;
	private String avatarImage;
	private String deviceToken;
	private int score = 0;

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAvatarName() {
		return avatarName;
	}

	public void setAvatarName(String avatar) {
		this.avatarName = avatar;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score += score;
	}

	public String getAvatarImage() {
		return avatarImage;
	}

	public void setAvatarImage(String avatarImage) {
		this.avatarImage = avatarImage;
	}

}
