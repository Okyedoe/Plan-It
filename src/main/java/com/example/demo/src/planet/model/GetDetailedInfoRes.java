package com.example.demo.src.planet.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetDetailedInfoRes {
    @ApiModelProperty(value = "행성아이디",example = "2")
    private int planet_id;
    @ApiModelProperty(value = "행성이름",example = "다이어트")
    private String planet_name;
    @ApiModelProperty(value = "행성설명",example = "열심히해보자!")
    private String planet_intro; //행성설명
    @ApiModelProperty(value = "행성경험치",example = "20")
    private int planet_exp;
    @ApiModelProperty(value = "행성레벨",example = "1")
    private int planet_level;
    @ApiModelProperty(value = "행성이미지",example = "이미지 URL로 리턴될 예정")
    private String planet_image;
    @ApiModelProperty(value = "행성색깔", example ="")
    private String color;


    private List<Plans> plans;

    public GetDetailedInfoRes(int planet_id, String planet_name, String planet_intro, int planet_exp, int planet_level, String planet_image,String color) {
        this.planet_id = planet_id;
        this.planet_name = planet_name;
        this.planet_intro = planet_intro;
        this.planet_exp = planet_exp;
        this.planet_level = planet_level;
        this.planet_image = planet_image;
        this.color=color;
    }

    //세부계획 가져오는 부분.
    //계획 + 타입(1회성과같은) + 완료여부(status) 리스트
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class Plans{
        public Plans(String plan_content, String type, int status,int is_completed) {
            this.plan_name = plan_content;
            this.type = type;
            this.status = status;
            this.is_completed = is_completed;
        }


        private String plan_name;
        private String type;
        private int status;

        private int is_completed;
    }



}
