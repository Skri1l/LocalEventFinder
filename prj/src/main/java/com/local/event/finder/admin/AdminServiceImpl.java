package com.local.event.finder.admin;

import com.local.event.finder.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.local.event.finder.user.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;


    @Override
    @Transactional
    public void blockUser(Long id) {
        validateAdminAccess();
        Objects.requireNonNull(id, "id is null");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setBlocked(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void unblockUser(Long id) {
        validateAdminAccess();
        Objects.requireNonNull(id, "id is null");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setBlocked(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void makeUserAdmin(Long id) {
        validateAdminAccess();
        Objects.requireNonNull(id, "id is null");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setRole("ADMIN");
        userRepository.save(user);
    }

    private void validateAdminAccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Current user not found"));

        if (!"ADMIN".equals(currentUser.getRole())) {
            throw new AccessDeniedException("Only admin can perform this action");
        }
    }
}
