package com.betterme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 */
@SpringBootApplication
public class BetterMeApplication {
    
    public static void main(String[] args) {
        // This single line starts EVERYTHING:
        // - Embedded Tomcat server (web server)
        // - Database connections
        // - All your controllers, services, etc.
        SpringApplication.run(BetterMeApplication.class, args);
        
        System.out.println("\n");
        System.out.println("\n");
    }
}
