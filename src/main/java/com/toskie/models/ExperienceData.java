package com.toskie.models;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ExperienceData {
    private String title;
    private String company;
    private String startDate;
    private String endDate;
    private String description;
    private boolean currentJob;
}
