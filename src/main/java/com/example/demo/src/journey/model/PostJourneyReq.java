package com.example.demo.src.journey.model;
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
public class PostJourneyReq {
    public static class Planetinfo
    {
        @ApiModelProperty(value = "행성이름",example = "다이어트")
        private String planet_name;
        @ApiModelProperty(value = "행성계획리스트(List<String>)",example = "행성계획리스트(List<String>)")
        private List<String> detailed_plans;

        public String getPlanet_name() {
            return planet_name;
        }

        public void setPlanet_name(String planet_name) {
            this.planet_name = planet_name;
        }

        public List<String> getDetailed_plans() {
            return detailed_plans;
        }

        public void setDetailed_plans(List<String> detailed_plans) {
            this.detailed_plans = detailed_plans;
        }
    }

    @ApiModelProperty(value = "기간",example = "25")
    private int period;
    @ApiModelProperty(value = "키워드배열(String[])",example = "[상냥함,우아한]")
    private String[] keywords;
    @ApiModelProperty(value = "List<Planetinfo>",example = "")
    private List<Planetinfo> planets;
    @ApiModelProperty(value = "유저아이디",example = "2")
    private int user_id;



}


