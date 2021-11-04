package com.example.SpringProject1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
public class PDFIndexer {

	public static void main(String[] args) {
		SpringApplication.run(PDFIndexer.class, args);
	}

}
