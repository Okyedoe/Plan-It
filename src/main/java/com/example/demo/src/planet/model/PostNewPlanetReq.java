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

    private String planet_intro;

    private String color;

    private List<Plan_detail> plan_list;

    public static class Plan_detail {
        private String plan_content;
        private String type;

        public String getPlan_content() {
            return plan_content;
        }

        public void setPlan_content(String plan_content) {
            this.plan_content = plan_content;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }




}

