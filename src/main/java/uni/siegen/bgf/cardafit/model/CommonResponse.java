package uni.siegen.bgf.cardafit.model;

public class CommonResponse {

    private int status;
    private String message;

    public CommonResponse() {
    }

    public CommonResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
