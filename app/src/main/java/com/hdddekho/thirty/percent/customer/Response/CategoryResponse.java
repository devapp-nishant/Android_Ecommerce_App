package com.hdddekho.thirty.percent.customer.Response;

import com.hdddekho.thirty.percent.customer.Models.CategoryModel;

import java.util.List;

public class CategoryResponse {
    String Status,Message;
    List<CategoryModel> Data;

    public CategoryResponse() {
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

    public List<CategoryModel> getData() {
        return Data;
    }

    public void setData(List<CategoryModel> data) {
        Data = data;
    }

    public CategoryResponse(String status, String message, List<CategoryModel> data) {
        Status = status;
        Message = message;
        Data = data;
    }

}
