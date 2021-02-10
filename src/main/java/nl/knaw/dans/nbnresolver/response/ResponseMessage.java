package nl.knaw.dans.nbnresolver.response;

public class ResponseMessage {

  private int code;
  private String message;

  public ResponseMessage(int responseCode, String responseMessage) {
    this.code = responseCode;
    this.message = responseMessage;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
