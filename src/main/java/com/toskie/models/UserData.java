package com.toskie.models;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserData {
    private String name;
    private String email;
    private String phone;
    private String password;
    private String confirmPassword;
}
