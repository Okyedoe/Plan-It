package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class PatchUserReq {
    private int user_id;
    private String password;
    private String phone_num;
    private MultipartFile profile_url;
}
