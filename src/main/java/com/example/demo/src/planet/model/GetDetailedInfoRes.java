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
        public Plans(int detailed_plan_id,String plan_content, String type, int status,int is_completed) {
            this.detailed_plan_id = detailed_plan_id;
            this.plan_name = plan_content;
            this.type = type;
            this.status = status;
            this.is_completed = is_completed;
        }


        private int detailed_plan_id;
        private String plan_name;
        private String type;
        private int status;

        private int is_completed;

        public int getDetailed_plan_id() {
            return detailed_plan_id;
        }

        public void setDetailed_plan_id(int detailed_plan_id) {
            this.detailed_plan_id = detailed_plan_id;
        }

        public String getPlan_name() {
            return plan_name;
        }

        public void setPlan_name(String plan_name) {
            this.plan_name = plan_name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getIs_completed() {
            return is_completed;
        }

        public void setIs_completed(int is_completed) {
            this.is_completed = is_completed;
        }
    }



}
