package com.example.demo.src.report.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportRes {
    private int total_last_week;
    private int total_two_weeks_ago;
    private int total_three_weeks_ago;
    private int total_four_weeks_ago;
    private int total_five_weeks_ago;
    private int completed_last_week;
    private int completed_two_weeks_ago;
    private int completed_three_weeks_ago;
    private int completed_four_weeks_ago;
    private int completed_five_weeks_ago;






}
