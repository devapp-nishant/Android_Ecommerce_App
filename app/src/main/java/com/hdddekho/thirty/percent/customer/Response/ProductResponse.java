package com.hdddekho.thirty.percent.customer.Response;

import com.hdddekho.thirty.percent.customer.Models.ProductModel;

import java.util.List;

public class ProductResponse {
    String Status,Message;
    List<ProductModel> Data;

    public ProductResponse() {
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

    public List<ProductModel> getData() {
        return Data;
    }

    public void setData(List<ProductModel> data) {
        Data = data;
    }

    public ProductResponse(String status, String message, List<ProductModel> data) {
        Status = status;
        Message = message;
        Data = data;
    }
}
