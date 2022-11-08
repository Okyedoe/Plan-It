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
public class PatchPlanReviseRes {
    private int detailed_plan_id;
    private String plan_content;
    private String type;

    public PatchPlanReviseRes(String plan_content, String type) {
    }
}
