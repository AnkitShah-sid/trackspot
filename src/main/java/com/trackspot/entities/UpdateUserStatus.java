package com.trackspot.entities;

import com.trackspot.entities.enums.UserStatus;
import lombok.Data;

@Data
public class UpdateUserStatus {
private long id;
private UserStatus status;
}

