package uni.siegen.bgf.cardafit.model;

public class LoginResponse extends CommonResponse {

    private User user;

    public LoginResponse() {
    }

    public LoginResponse(int status, String message, User user) {
        super.setStatus(status);
        super.setMessage(message);
        
        this.setUser(user);
    }

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
