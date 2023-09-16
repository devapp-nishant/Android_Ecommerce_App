package com.hdddekho.thirty.percent.customer.Utils;

import java.util.Random;

public class OrderNumber {
    private final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private final String NUMBERS = "0123456789";
    private final char[] ALPHA_NUMBER = (LETTERS+LETTERS.toUpperCase()+NUMBERS).toCharArray();

    public String GenerateOrderNumber(int length){
        StringBuilder result = new StringBuilder();
        for (int i=0;i<length;i++){
            result.append(ALPHA_NUMBER[new Random().nextInt(ALPHA_NUMBER.length)]);
        }
        return result.toString();
    }
}
