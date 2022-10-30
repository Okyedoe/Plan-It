package com.example.demo.src.kakao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostOAuthReq {
    private String kakao_id;
    private String email;
    private String name;
}
