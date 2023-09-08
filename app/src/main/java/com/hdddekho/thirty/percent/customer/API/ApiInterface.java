package com.hdddekho.thirty.percent.customer.API;

import com.hdddekho.thirty.percent.customer.Response.CartResponse;
import com.hdddekho.thirty.percent.customer.Response.CategoryResponse;
import com.hdddekho.thirty.percent.customer.Response.CustomerResponse;
import com.hdddekho.thirty.percent.customer.Response.OrderResponse;
import com.hdddekho.thirty.percent.customer.Response.ProductResponse;
import com.hdddekho.thirty.percent.customer.Response.SuccessResponse;
import com.hdddekho.thirty.percent.customer.Response.WishlistResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {
//    String baseUrl = "http://192.168.112.11/30Percent/";
    String baseUrl = "https://30app.hdddekho.com/";

    @GET("fetch_category.php")
    Call<CategoryResponse> getAllCategory();

    @GET("fetch_product.php")
    Call<ProductResponse> getAllProducts();

    @GET("fetch_random_products.php")
    Call<ProductResponse> fetchForYouProducts();

    @GET("fetch_new_products.php")
    Call<ProductResponse> fetchNewProducts();

    @Multipart
    @POST("fetch_category_products.php")
    Call<ProductResponse> fetchCategoryProducts(@Part("cat_id") RequestBody catId);

    @Multipart
    @POST("add_customer.php")
    Call<SuccessResponse> loginCustomer(@Part("customer_phone") RequestBody phone);

    @Multipart
    @POST("signUpCustomer.php")
    Call<SuccessResponse> signUpCustomer(
            @Part("customer_id") RequestBody cusId,
            @Part("customer_name") RequestBody name,
            @Part("customer_email") RequestBody email,
            @Part("customer_address") RequestBody address,
            @Part("customer_city") RequestBody city,
            @Part("customer_pincode") RequestBody zip
    );

    @Multipart
    @POST("fetch_customer_by_phone.php")
    Call<CustomerResponse> getCustomerPhoneData(@Part("customer_phone") RequestBody phone);

    @Multipart
    @POST("fetch_customer_by_id.php")
    Call<CustomerResponse> getCustomerID(@Part("customer_id") RequestBody id);

    @Multipart
    @POST("check_wishlist.php")
    Call<SuccessResponse> checkWishlist(@Part("customer_id") RequestBody customerId, @Part("product_id") RequestBody productId);

    @Multipart
    @POST("add_wishlist.php")
    Call<SuccessResponse> addWishlist(@Part("customer_id") RequestBody customerId, @Part("product_id") RequestBody productId);

    @Multipart
    @POST("delete_wishlist.php")
    Call<SuccessResponse> deleteWishlist(@Part("customer_id") RequestBody customerId, @Part("product_id") RequestBody productId);

    @Multipart
    @POST("add_to_cart.php")
    Call<SuccessResponse> AddToCart(@Part("customer_id") RequestBody customerId, @Part("product_id") RequestBody productId, @Part("quantity") RequestBody quantity);

    @Multipart
    @POST("fetch_cart.php")
    Call<CartResponse> fetchCart(@Part("customer_id") RequestBody customerId);

    @Multipart
    @POST("fetch_wishlist.php")
    Call<WishlistResponse> fetchMyWishlist(@Part("customer_id") RequestBody customerId);

    @Multipart
    @POST("increase_cart_quantity.php")
    Call<SuccessResponse> increaseCart(@Part("cart_id") RequestBody cartId);

    @Multipart
    @POST("decrease_cart_quantity.php")
    Call<SuccessResponse> decreaseCart(@Part("cart_id") RequestBody cartId);

    @Multipart
    @POST("remove_cart.php")
    Call<SuccessResponse> deleteFromCart(@Part("cart_id") RequestBody cartId, @Part("product_id") RequestBody productId);

    @Multipart
    @POST("empty_cart.php")
    Call<SuccessResponse> emptyCart(@Part("customer_id") RequestBody cusId);

    @Multipart
    @POST("place_order.php")
    Call<SuccessResponse> placeOrder(
            @Part("customer_id") RequestBody customerId,
            @Part("product_id") RequestBody productId,
            @Part("quantity") RequestBody quantity,
            @Part("order_number") RequestBody order_number,
            @Part("transaction_id") RequestBody transaction_id,
            @Part("payment_mode") RequestBody payment_mode,
            @Part("total_amount") RequestBody total_amount,
            @Part("order_status") RequestBody order_status,
            @Part("delivery_status") RequestBody delivery_status
    );

    @GET("fetch_orders.php")
    Call<OrderResponse> fetchOrders();

    @Multipart
    @POST("cancel_order.php")
    Call<SuccessResponse> cancelOrder(
            @Part("order_id") RequestBody orderId,
            @Part("order_status") RequestBody orderStatus
    );


}
