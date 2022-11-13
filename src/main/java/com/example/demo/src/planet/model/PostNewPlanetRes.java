package com.example.demo.src.planet.model;
import com.example.demo.src.planet.model.PostNewPlanetReq.Plan_detail;
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

public class PostNewPlanetRes {
    private int planet_id;
    @ApiModelProperty(value = "행성이름",example = "다이어트")
    private String planet_name;


    private String planet_intro;
    private String color;
//    @ApiModelProperty(example = "[물먹기,운동하기,헬스장가기]")
    private List<Plan_detail> detailed_plans;


}
