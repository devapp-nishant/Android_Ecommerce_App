package com.hdddekho.thirty.percent.customer.Models;

public class CartModel {
    String cart_id, product_id, quantity, product_name, image1, mrp, discount;

    public CartModel() {
    }

    public CartModel(String cart_id, String product_id, String quantity, String product_name, String image1, String mrp, String discount) {
        this.cart_id = cart_id;
        this.product_id = product_id;
        this.quantity = quantity;
        this.product_name = product_name;
        this.image1 = image1;
        this.mrp = mrp;
        this.discount = discount;
    }

    public String getCart_id() {
        return cart_id;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
