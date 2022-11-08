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
public class PostJourneyRes {
    private int journey_id;
    private int period;
    private int user_id;
    private String nickname;
    private String[] keywords;
    private List<PostJourneyReq.Planetinfo> planets;


}
