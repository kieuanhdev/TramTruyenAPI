package com.tramtruyen.api.security;

import com.tramtruyen.api.model.UserEntity;
import com.tramtruyen.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Tìm User trong DB
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user với email: " + email));

        // 2. Chuyển Entity của ta thành UserDetails của Spring Security
        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPasswordHash()) // Đưa cục password đã băm cho Spring tự kiểm tra
                .roles(userEntity.getRole()) // Set quyền (ADMIN, READER, AUTHOR)
                .build();
    }
}