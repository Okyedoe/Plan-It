package com.example.demo.src.plan.model;

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
public class GetTodayPlanRes {
    private int planet_id;
    private String planet_image;
    private String plan_content;
    private String type;
    private int is_completed;
    private int detailed_plan_id;



}
