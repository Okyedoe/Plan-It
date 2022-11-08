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
public class GetAllJourneyRes {
    private int sum_journey ;
    private int sum_planet ;
    private int sum_period;
    private List<GetJourneyRes> getJourneyRes;
}
