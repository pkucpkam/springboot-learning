package com.example.asynchronous_processing.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Async
    public void sendEmail(String productName) {
        try {
            Thread.sleep(5000);
            System.out.println("Email sent for " + productName);
        }
        catch (InterruptedException e) {
            System.err.println(e);
        }
    }
}
