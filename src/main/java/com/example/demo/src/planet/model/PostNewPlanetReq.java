package com.example.demo.src.planet.model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor


public class PostNewPlanetReq {

    @ApiModelProperty(value = "행성이름",example = "다이어트")
    private String planet_name;
    @ApiModelProperty(value = "계획을 문자열배열로 입력")
    private List<String> detailed_plans;
    @ApiModelProperty(value="색깔", example = "#7AE3AA")
    private String color;


}
