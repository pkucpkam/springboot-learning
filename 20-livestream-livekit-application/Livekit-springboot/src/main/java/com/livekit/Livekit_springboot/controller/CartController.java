package com.livekit.Livekit_springboot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "${app.frontend.url:http://localhost:3000}", methods = {RequestMethod.POST})
public class CartController {

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(
            @RequestParam String userId,
            @RequestBody CartRequest request) {

        // Validate inputs
        if (userId == null || userId.isBlank() || request.getProductId() == null || request.getProductId().isBlank()) {
            return ResponseEntity.badRequest().body("Invalid userId or productId");
        }

        // Simulate adding to cart (replace with actual database logic)
        System.out.println("Added product " + request.getProductId() + " to cart for user " + userId);
        return ResponseEntity.ok("Product added to cart");
    }

    static class CartRequest {
        private String productId;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }
    }
}