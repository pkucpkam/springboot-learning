package com.likelion.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.dto.GoogleUser;
import com.likelion.entity.Role;
import com.likelion.entity.User;
import com.likelion.repository.RoleRepository;
import com.likelion.repository.UserRepository;
import com.likelion.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // role mặc định khi user mới đăng nhập bằng Google
    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Transactional
    @Override
    public User upsertGoogleUser(GoogleUser guser) {
        // 1) Tồn tại theo googleSub → update nhẹ thông tin
        User user = userRepository.findByGoogleSub(guser.sub()).orElse(null);
        if (user != null) {
            boolean dirty = false;
            if (guser.email() != null && !guser.email().equals(user.getEmail())) {
                user.setEmail(guser.email());
                dirty = true;
            }
            if (guser.name() != null && !guser.name().equals(user.getName())) {
                user.setName(guser.name());
                dirty = true;
            }
            if (guser.picture() != null && !guser.picture().equals(user.getPicture())) {
                user.setPicture(guser.picture());
                dirty = true;
            }

            // đảm bảo user có role mặc định
            ensureHasRole(user, DEFAULT_ROLE);

            // load đầy đủ roles trước khi return để tránh LazyInitialization ở controller
            return userRepository.findByIdWithRoles(user.getId()).orElse(user);
        }

        // 2) Chưa link sub nhưng đã có user theo email → link googleSub & bổ sung thông
        // tin
        user = userRepository.findByEmail(guser.email()).orElse(null);
        if (user != null) {
            user.setGoogleSub(guser.sub());
            if (user.getName() == null)
                user.setName(guser.name());
            if (user.getPicture() == null)
                user.setPicture(guser.picture());

            ensureHasRole(user, DEFAULT_ROLE);
            userRepository.save(user);
            return userRepository.findByIdWithRoles(user.getId()).orElse(user);
        }

        // 3) Tạo mới hoàn toàn
        User created = new User();
        created.setEmail(guser.email());
        created.setName(guser.name());
        created.setPicture(guser.picture());
        created.setGoogleSub(guser.sub());

        userRepository.save(created); // cần có id trước khi tạo UserRole
        ensureHasRole(created, DEFAULT_ROLE);

        // trả về kèm roles đã fetch
        return userRepository.findByIdWithRoles(created.getId()).orElse(created);
    }

    /** Gán role nếu user chưa có role code đó */
    private void ensureHasRole(User user, String roleCode) {
        // nếu đã có rồi thì thôi
        boolean hasIt = user.getUserRoles().stream()
                .anyMatch(ur -> roleCode.equals(ur.getRole().getCode()));
        if (hasIt)
            return;

        Role role = roleRepository.findByCode(roleCode)
                .orElseThrow(() -> new IllegalStateException("Missing role code: " + roleCode));
        user.addRole(role); // helper trong entity User tạo UserRole(user, role)
        // không cần repo.save ở đây nếu đang trong @Transactional và user là managed
    }
    
    @Override
    @Transactional(readOnly = true)
    public User getById(String id) {
        if (id == null || id.length() != 36)
            throw new IllegalArgumentException("User id is not a UUID: " + id);
        return userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }
}
