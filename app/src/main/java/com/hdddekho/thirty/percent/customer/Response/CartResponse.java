package com.hdddekho.thirty.percent.customer.Response;

import com.hdddekho.thirty.percent.customer.Models.CartModel;

import java.util.List;

public class CartResponse {

    String Status,Message;
    List<CartModel> Data;

    public CartResponse() {
    }

    public CartResponse(String status, String message, List<CartModel> data) {
        Status = status;
        Message = message;
        Data = data;
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

    public List<CartModel> getData() {
        return Data;
    }

    public void setData(List<CartModel> data) {
        Data = data;
    }
}
