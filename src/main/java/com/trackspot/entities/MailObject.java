package com.trackspot.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Olga on 7/20/2016.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailObject {
    @Email
    @NotNull
    @Size(min = 1, message = "Please, set an email address to send the message to it")
    private String to;
    private String recipientName;
    private String subject;
    private String registrationLink;
    private String senderName;
    private String templateEngine;
}
