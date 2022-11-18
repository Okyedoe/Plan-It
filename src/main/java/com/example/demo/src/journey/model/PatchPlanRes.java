package com.example.demo.src.journey.model;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchPlanRes {
    private int detailed_plan_id;
    private int planet_id;
    private int planet_exp;
    private int planet_level;
    private String plan_content;
    private String type;
    private int status;
    private int is_completed;
    private String color;





}
