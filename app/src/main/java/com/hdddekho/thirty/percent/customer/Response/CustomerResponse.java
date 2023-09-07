package com.hdddekho.thirty.percent.customer.Response;

import com.hdddekho.thirty.percent.customer.Models.CustomerModel;

import java.util.List;

public class CustomerResponse {
    String Status,Message;
    List<CustomerModel> Data;

    public CustomerResponse() {
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

    public List<CustomerModel> getData() {
        return Data;
    }

    public void setData(List<CustomerModel> data) {
        Data = data;
    }

    public CustomerResponse(String status, String message, List<CustomerModel> data) {
        Status = status;
        Message = message;
        Data = data;
    }
}
