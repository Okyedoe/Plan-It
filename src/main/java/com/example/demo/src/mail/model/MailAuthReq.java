package com.example.demo.src.mail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MailAuthReq {
    private String email;
    private String auth;
}
