package com.toskie.models;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class QualificationData {
    private String degree;
    private String institution;
    private String fieldOfStudy;
    private String startYear;
    private String endYear;
    private String year;
}
