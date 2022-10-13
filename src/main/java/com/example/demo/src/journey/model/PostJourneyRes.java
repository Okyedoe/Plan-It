package com.example.demo.src.journey.model;
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
    private String[] keywords;
    private List<PostJourneyReq.Planetinfo> planets;
    private int user_id;
}
