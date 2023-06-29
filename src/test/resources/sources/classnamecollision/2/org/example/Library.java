package org.example;

public class Library {

    public String getInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append(new com.example.math.Library().getInfo());
        return builder.toString();
    }
}
