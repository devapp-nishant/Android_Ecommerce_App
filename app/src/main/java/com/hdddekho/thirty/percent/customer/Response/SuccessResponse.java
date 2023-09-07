package com.hdddekho.thirty.percent.customer.Response;

public class SuccessResponse {

    String Status, Message;

    public SuccessResponse() {
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public SuccessResponse(String status, String message) {
        Status = status;
        Message = message;
    }
}
