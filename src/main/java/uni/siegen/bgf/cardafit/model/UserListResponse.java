package uni.siegen.bgf.cardafit.model;

import java.util.List;

public class UserListResponse extends CommonResponse {

    private List<User> records;

    public UserListResponse() {
    }

    public UserListResponse(int status, String message, List<User> users) {
        super.setStatus(status);
        super.setMessage(message);
        
        this.setRecords(users);
    }

	public List<User> getRecords() {
		return records;
	}

	public void setRecords(List<User> records) {
		this.records = records;
	}
}
