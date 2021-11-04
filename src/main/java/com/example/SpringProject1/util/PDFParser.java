package com.example.SpringProject1.util;

import org.apache.http.HttpEntity;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class PDFParser {

    private static String content ;




    public static String parse(byte [] bytes) throws IOException {
        //Loading an existing document
        PDDocument document = Loader.loadPDF(bytes);
        //Instantiate PDFTextStripper class
        PDFTextStripper pdfStripper = new PDFTextStripper();
        //Retrieving text from PDF document
        content = pdfStripper.getText(document);
        //Closing the document
        document.close();
        return content;
    }

    public static String encodePdfBase64(InputStream fileInputStream) {
        try {

            byte[]  fileByteStream = IOUtils.toByteArray(fileInputStream );
            String base64String = new String(Base64.getEncoder().encodeToString(fileByteStream).getBytes(),"UTF-8");
            String strEncoded = Base64.getEncoder().encodeToString( base64String.getBytes( "utf-8" ));
            fileInputStream.close();
            return strEncoded;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void decode(String strEncoded,String saveRep) throws IOException {

        byte[] decodedStr = Base64.getDecoder().decode( strEncoded );

        FileOutputStream fos = new FileOutputStream(saveRep);
        fos.write(Base64.getDecoder().decode(new String( decodedStr, "utf-8" )));
        fos.close();
    }
}
