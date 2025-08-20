package email.auth.demo.service;

import email.auth.demo.dto.UserDTO;
import email.auth.demo.entity.AppUser;
import email.auth.demo.entity.VerificationToken;
import email.auth.demo.repository.UserRepository;
import email.auth.demo.repository.VerificationTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SpringTemplateEngine templateEngine;

    private static final int OTP_EXPIRY_MINUTES = 5;

    public void registerUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new RuntimeException("Email đã tồn tại!");
        }

        AppUser user = new AppUser();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setVerified(false);
        userRepository.save(user);

        String otp = generateOTP();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(otp);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(calculateExpiryDate(OTP_EXPIRY_MINUTES));
        tokenRepository.save(verificationToken);

        sendVerificationEmail(user, otp);
    }

    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // OTP 6 chữ số
        return String.valueOf(otp);
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return cal.getTime();
    }

    private void sendVerificationEmail(AppUser user, String otp) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject("Xác thực tài khoản - Mã OTP");

            // Render template HTML với Thymeleaf
            Context context = new Context();
            context.setVariable("email", user.getEmail());
            context.setVariable("otp", otp);
            context.setVariable("expiryMinutes", OTP_EXPIRY_MINUTES);
            String htmlContent = templateEngine.process("verificationEmail", context);

            helper.setText(htmlContent, true); // true: HTML content

            mailSender.send(mimeMessage);
            logger.info("Email with OTP sent to {}", user.getEmail());
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void verifyOTP(String otp) {
        VerificationToken verificationToken = tokenRepository.findByToken(otp);
        if (verificationToken == null || verificationToken.getExpiryDate().before(new Date())) {
            throw new RuntimeException("OTP không hợp lệ hoặc đã hết hạn!");
        }

        AppUser user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken);
    }
}