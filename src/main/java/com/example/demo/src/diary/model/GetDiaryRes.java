package com.example.demo.src.diary.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetDiaryRes {
    private int diary_id;
    private String emotion;
    private int evaluation;
    private String content;
    private String created_at;
    private List<String> images;
}
