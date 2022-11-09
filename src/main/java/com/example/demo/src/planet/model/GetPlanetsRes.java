package com.example.demo.src.planet.model;

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
public class GetPlanetsRes {
    //행성이름,행성설명,행성이미지,행성레벨,행성경험치
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
    private String color_rgb;


    public GetPlanetsRes(int planet_id, String planet_name, String planet_intro, int planet_exp,
        int planet_level,String color) {
        this.planet_id = planet_id;
        this.planet_name = planet_name;
        this.planet_intro = planet_intro;
        this.planet_exp = planet_exp;
        this.planet_level = planet_level;
        this.planet_image = "";
        this.color_rgb = color;

    }
}
