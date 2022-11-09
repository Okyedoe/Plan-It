package com.example.demo.src.planet.model;
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
public class PatchRevisePlanetInforReq {
    private String color;
    private String planet_name;
    private String planet_intro;
}
