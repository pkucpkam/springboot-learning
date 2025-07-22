package example.userdemo.user.service;

import example.userdemo.user.dto.AuthResponse;
import example.userdemo.user.dto.LoginRequest;
import example.userdemo.user.dto.RegisterRequest;
import example.userdemo.user.entity.Role;
import example.userdemo.user.entity.User;
import example.userdemo.user.repository.RoleRepository;
import example.userdemo.user.repository.UserRepository;
import example.userdemo.user.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtProvider jwtProvider;

    public AuthResponse register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        userRepository.save(user);

        AuthResponse response = new AuthResponse();
        response.setToken(jwtProvider.createToken(user.getUsername(), "ROLE_USER"));
        return response;
    }

    public AuthResponse login(LoginRequest request) {
        // Xác thực thông tin đăng nhập
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Lấy role từ danh sách quyền
        String role = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())  // Lấy tên role
                .findFirst()
                .orElse("ROLE_USER");

        // Tạo token
        String token = jwtProvider.createToken(request.getUsername(), role);

        // Trả về AuthResponse đầy đủ
        AuthResponse response = new AuthResponse();
        response.setUsername(request.getUsername());
        response.setRole(role);
        response.setToken(token);

        return response;
    }

}
