package com.example.demo.src.journey.model;

import com.example.demo.utils.image.model.GetImageList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetJourneyRes {
    private int period;
    private List<String> planet;
    private GetImageList diary_img_url;
    private String start_date;
    private String end_date;
}
