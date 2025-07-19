package example.userdemo.service;

import example.userdemo.dto.AuthResponse;
import example.userdemo.dto.LoginRequest;
import example.userdemo.dto.RegisterRequest;
import example.userdemo.entity.Role;
import example.userdemo.entity.User;
import example.userdemo.repository.RoleRepository;
import example.userdemo.repository.UserRepository;
import example.userdemo.security.JwtProvider;
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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        String role = authentication.getAuthorities().stream()
                .map(Object::toString)
                .findFirst()
                .orElse("ROLE_USER");
        AuthResponse response = new AuthResponse();
        response.setToken(jwtProvider.createToken(request.getUsername(), role));
        return response;
    }
}
