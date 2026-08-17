package com.glasscore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GlassCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlassCoreApplication.class, args);
        System.out.println("GlassCore: http://localhost:8080");
    }
}
