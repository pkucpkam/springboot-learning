package email.auth.demo.service;

import email.auth.demo.dto.UserDTO;
import email.auth.demo.entity.AppUser;
import email.auth.demo.entity.VerificationToken;
import email.auth.demo.repository.UserRepository;
import email.auth.demo.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new RuntimeException("Email đã tồn tại!");
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(userDTO.getUsername());
        appUser.setEmail(userDTO.getEmail());
        appUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        appUser.setVerified(false);
        userRepository.save(appUser);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(appUser);
        verificationToken.setExpiryDate(calculateExpiryDate(24 * 60));
        tokenRepository.save(verificationToken);

        sendVerificationEmail(appUser, token);
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return cal.getTime();
    }

    private void sendVerificationEmail(AppUser appUser, String token) {
        String subject = "Xác thực tài khoản";
        String confirmationUrl = "http://localhost:8080/api/auth/verify?token=" + token;
        String message = "Vui lòng nhấp vào liên kết sau để xác thực tài khoản: \n" + confirmationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(appUser.getEmail());
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }

    public void verifyUser(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null || verificationToken.getExpiryDate().before(new Date())) {
            throw new RuntimeException("Token không hợp lệ hoặc đã hết hạn!");
        }

        AppUser appUser = verificationToken.getUser();
        appUser.setVerified(true);
        userRepository.save(appUser);
        tokenRepository.delete(verificationToken);
    }
}