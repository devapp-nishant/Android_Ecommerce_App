package com.hdddekho.thirty.percent.customer.Models;

public class CategoryModel {
    String category_id, category_name, category_image, total_products;

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_image() {
        return category_image;
    }

    public void setCategory_image(String category_image) {
        this.category_image = category_image;
    }

    public String getTotal_products() {
        return total_products;
    }

    public void setTotal_products(String total_products) {
        this.total_products = total_products;
    }

    public CategoryModel() {
    }

    public CategoryModel(String category_id, String category_name, String category_image, String total_products) {
        this.category_id = category_id;
        this.category_name = category_name;
        this.category_image = category_image;
        this.total_products = total_products;
    }

}
