package com.hdddekho.thirty.percent.customer.Response;

import com.hdddekho.thirty.percent.customer.Models.OrderModel;

import java.util.List;

public class OrderResponse {
    String Status,Message;
    List<OrderModel> Data;

    public OrderResponse() {
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

    public List<OrderModel> getData() {
        return Data;
    }

    public void setData(List<OrderModel> data) {
        Data = data;
    }

    public OrderResponse(String status, String message, List<OrderModel> data) {
        Status = status;
        Message = message;
        Data = data;
    }
}
