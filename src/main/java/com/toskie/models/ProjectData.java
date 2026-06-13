package com.toskie.models;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProjectData {
    private String title;
    private String description;
    private String projectUrl;
    private String url;
    private String imagePath;
}
