package com.trackspot.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
public class UserEmulatorAssignment {

    private User user;
    private EmulatorDetails emulatorDetails;
}
