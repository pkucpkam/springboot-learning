package email.auth.demo.controller;

import email.auth.demo.dto.UserDTO;
import email.auth.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        userService.registerUser(userDTO);
        return ResponseEntity.ok("Đăng ký thành công! Vui lòng kiểm tra email để lấy mã OTP xác thực.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOTP(@RequestParam("otp") String otp) {
        userService.verifyOTP(otp);
        return ResponseEntity.ok("Xác thực tài khoản thành công!");
    }
}