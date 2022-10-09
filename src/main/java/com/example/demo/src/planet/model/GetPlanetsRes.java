package com.example.demo.src.planet.model;

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
    private String planet_name;
    private String planet_intro; //행성설명
    private int planet_exp;
    private int planet_level;
    private String planet_image;





}
