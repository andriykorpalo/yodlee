package com.strabo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import java.util.Locale;

@SpringBootApplication
@ServletComponentScan("com.strabo.controller")
public class YodleeApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(YodleeApplication.class, args);
        Locale locale = new Locale("en");
        Locale.setDefault(locale);
    }
}
