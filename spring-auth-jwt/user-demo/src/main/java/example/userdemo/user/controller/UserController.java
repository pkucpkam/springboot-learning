package example.userdemo.user.controller;

import example.userdemo.user.dto.UserDTO;
import example.userdemo.user.dto.UserRegistrationDTO;
import example.userdemo.user.entity.User;
import example.userdemo.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> userDTOs = userService.getAllUsers().stream()
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setId(user.getId());
                    dto.setUsername(user.getUsername());
                    dto.setName(user.getName());
                    dto.setEmail(user.getEmail());
                    dto.setAddress(user.getAddress());
                    dto.setPhone(user.getPhone());
                    dto.setNote(user.getNote());
                    dto.setStatus(user.getStatus());
                    dto.setRoles(user.getRoles());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserRegistrationDTO registrationDTO) {
        User user = userService.registerUser(registrationDTO);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setAddress(user.getAddress());
        userDTO.setPhone(user.getPhone());
        userDTO.setNote(user.getNote());
        userDTO.setStatus(user.getStatus());
        userDTO.setRoles(user.getRoles());
        return ResponseEntity.ok(userDTO);
    }
}