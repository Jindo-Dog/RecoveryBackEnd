package com.example.recovery.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter
@Setter
public class UserResponse {
    private String email;
    private String nickname;
    private MultipartFile profileImg;
}
