package com.hdddekho.thirty.percent.customer.Models;

public class WishlistModel {
    String product_id, cat_id, product_name, discount, image1, mrp,customer_id;

    public WishlistModel() {
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
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

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public WishlistModel(String product_id, String cat_id, String product_name, String discount, String image1, String mrp, String customer_id) {
        this.product_id = product_id;
        this.cat_id = cat_id;
        this.product_name = product_name;
        this.discount = discount;
        this.image1 = image1;
        this.mrp = mrp;
        this.customer_id = customer_id;
    }
}
