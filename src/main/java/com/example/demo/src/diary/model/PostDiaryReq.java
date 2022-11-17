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
public class PostDiaryReq {
    private int journey_id;
    private String emotion;
    private int evaluation;
    private String content;
    private MultipartFile images;


    public PostDiaryReq(int journey_id,String emotion, int evaluation, String content){
        this.journey_id=journey_id;
        this.emotion = emotion;
        this.evaluation =evaluation;
        this.content=content;
    }

}
