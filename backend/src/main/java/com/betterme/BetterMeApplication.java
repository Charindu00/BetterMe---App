package com.betterme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: The Main Application Class                               ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ @SpringBootApplication is a POWERFUL annotation that combines:           ║
 * ║                                                                          ║
 * ║ 1. @Configuration - This class can define beans (objects Spring manages)║
 * ║ 2. @EnableAutoConfiguration - Spring automatically configures things     ║
 * ║ 3. @ComponentScan - Spring scans this package for other components       ║
 * ║                                                                          ║
 * ║ This is the ENTRY POINT of your application - like main() in basic Java ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
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
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║  🚀 BetterMe Backend is running!                     ║");
        System.out.println("║  📍 API: http://localhost:8080                       ║");
        System.out.println("║  📊 H2 Console: http://localhost:8080/h2-console     ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println("\n");
    }
}
