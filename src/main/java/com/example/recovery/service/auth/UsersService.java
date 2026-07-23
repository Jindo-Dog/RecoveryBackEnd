package com.example.recovery.service.auth;

import com.example.recovery.common.exception.EmailAlreadyExistException;
import com.example.recovery.common.exception.UsersNotFoundException;
import com.example.recovery.domain.user.UserCredential;
import com.example.recovery.domain.user.Users;
import com.example.recovery.repository.users.UserCredentialRepository;
import com.example.recovery.repository.users.UsersRepository;
import com.example.recovery.request.UserPasswordUpdateRequest;
import com.example.recovery.request.UserUpdateRequest;
import com.example.recovery.request.auth.SignupRequest;
import com.example.recovery.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final AuthTokenService authTokenService;

    @Value("${app.upload.profile-root:uploads/profile}")
    private String profileRootDir;

    public void signup(SignupRequest request) {
        if (userCredentialRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistException("이미 존재하는 이메일입니다.");
        }

        UserCredential credential = new UserCredential();
        Users users = new Users();
        users.setNickname(request.getNickname());

        credential.setEmail(request.getEmail());
        credential.setPassword(passwordEncoder.encode(request.getPassword()));
        credential.setUsers(users);

        usersRepository.save(users);
        userCredentialRepository.save(credential);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser() {
        Long userId = authTokenService.getCurrentUserId();

        UserCredential credential = userCredentialRepository.findByUsersId(userId)
                .orElseThrow(() -> new UsersNotFoundException("해당 사용자가 없습니다."));

        Users users = credential.getUsers();

        return UserResponse.builder()
                .email(credential.getEmail())
                .nickname(users.getNickname())
                .profileUrl(users.getProfileUrl())
                .build();
    }

    @Transactional
    public void updateUser(UserUpdateRequest request) {
        Long userId = authTokenService.getCurrentUserId();

        UserCredential credential = userCredentialRepository.findByUsersId(userId)
                .orElseThrow(() -> new UsersNotFoundException("해당 사용자가 없습니다."));

        if (!passwordEncoder.matches(request.getPassword(), credential.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        credential.getUsers().setNickname(request.getNickname());
        credential.setUpdatedAt(OffsetDateTime.now());
    }

    @Transactional
    public void updatePassword(UserPasswordUpdateRequest request) {
        Long userId = authTokenService.getCurrentUserId();

        UserCredential credential = userCredentialRepository.findByUsersId(userId)
                .orElseThrow(() -> new UsersNotFoundException("해당 사용자가 없습니다."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), credential.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        credential.setPassword(passwordEncoder.encode(request.getNewPassword()));
        credential.setUpdatedAt(OffsetDateTime.now());
    }

    @Transactional
    public void updateProfileImg(MultipartFile multipartFile) {
        Long userId = authTokenService.getCurrentUserId();

        UserCredential credential = userCredentialRepository.findByUsersId(userId)
                .orElseThrow(() -> new UsersNotFoundException("해당 사용자가 없습니다."));

        String originalFilename = multipartFile.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }

        String storedFilename = UUID.randomUUID() + extension;
        Path userDir = Path.of(profileRootDir, String.valueOf(userId));
        Path targetPath = userDir.resolve(storedFilename);

        try {
            Files.createDirectories(userDir);
            Files.copy(multipartFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("프로필 이미지 저장에 실패했습니다.", e);
        }

        credential.getUsers().setProfileUrl(targetPath.toString().replace('\\', '/'));
        credential.setUpdatedAt(OffsetDateTime.now());
    }

    @Transactional
    public void withdrawUser() {
        Long userId = authTokenService.getCurrentUserId();

        UserCredential credential = userCredentialRepository.findByUsersId(userId)
                .orElseThrow(() -> new UsersNotFoundException("해당 사용자가 없습니다."));

        credential.getUsers().setActivation(false);
        credential.setUpdatedAt(OffsetDateTime.now());
    }
}
