package com.example.demo.src.diary.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDiary {
    private int user_id;
    private String emotion;
    private int evaluation;
    private String content;
    private List<String> image_url;

    public PostDiary(int user_id, String emotion, int evaluation, String content) {
        this.user_id = user_id;
        this.emotion = emotion;
        this.evaluation = evaluation;
        this.content = content;
    }
}

