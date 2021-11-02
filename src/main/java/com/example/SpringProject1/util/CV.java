package com.example.SpringProject1.util;

public class CV {

    private String filename;
    private String encoded;

    public CV(String filename, String encoded) {
        this.filename = filename;
        this.encoded = encoded;
    }

    public String getFilename() {
        return filename;
    }

    public String getEncoded() {
        return encoded;
    }
}
