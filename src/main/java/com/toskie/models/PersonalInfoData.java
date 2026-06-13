package com.toskie.models;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PersonalInfoData {
    private String name;
    private String phone;
    private String dob;
    private String gender;
    private String profileImagePath;
    private String city;
    private String state;
    private String pinCode;
}
