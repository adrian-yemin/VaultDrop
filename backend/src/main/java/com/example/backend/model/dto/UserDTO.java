package com.example.backend.model.dto;

import com.example.backend.model.entity.User;
import lombok.Data;

import java.util.UUID;

@Data
public class UserDTO {
    private UUID uuid;
    private String username;

    public UserDTO(User user) {
        this.uuid = user.getExternalId();
        this.username = user.getUsername();
    }
}
