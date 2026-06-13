package com.toskie.models;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TalentCardData {
    private String name;
    private String skill;
    private String location;
    private String rating;
    private String imageUrl;
}
