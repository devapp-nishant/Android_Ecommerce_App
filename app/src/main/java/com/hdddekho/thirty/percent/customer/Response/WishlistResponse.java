package com.hdddekho.thirty.percent.customer.Response;

import com.hdddekho.thirty.percent.customer.Models.WishlistModel;

import java.util.List;

public class WishlistResponse {

    String Status,Message;
    List<WishlistModel> Data;

    public WishlistResponse() {
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

    public List<WishlistModel> getData() {
        return Data;
    }

    public void setData(List<WishlistModel> data) {
        Data = data;
    }

    public WishlistResponse(String status, String message, List<WishlistModel> data) {
        Status = status;
        Message = message;
        Data = data;
    }
}
