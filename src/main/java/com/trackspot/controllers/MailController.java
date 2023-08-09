package com.trackspot.controllers;

import com.trackspot.entities.MailObject;
import com.trackspot.services.mail.EmailService;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mail")
@CrossOrigin(value = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class MailController {

    private static final Map<String, Map<String, String>> labels;

    static {
        labels = new HashMap<>();

        //Simple email
        Map<String, String> props = new HashMap<>();
        props.put("headerText", "Send Simple Email");
        props.put("messageLabel", "Message");
        props.put("additionalInfo", "");
        labels.put("send", props);

        //Email with template
        props = new HashMap<>();
        props.put("headerText", "Send Email Using Text Template");
        props.put("messageLabel", "Template Parameter");
        props.put("additionalInfo",
                "The parameter value will be added to the following message template:<br>" +
                        "<b>This is the test email template for your email:<br>'Template Parameter'</b>"
        );
        labels.put("sendTemplate", props);

        //Email with attachment
        props = new HashMap<>();
        props.put("headerText", "Send Email With Attachment");
        props.put("messageLabel", "Message");
        props.put("additionalInfo", "To make sure that you send an attachment with this email, change the value for the 'attachment.invoice' in the application.properties file to the path to the attachment.");
        labels.put("sendAttachment", props);

    }

    @Autowired
    public EmailService emailService;

    @Value("${attachment.invoice}")
    private String attachmentPath;

    @PostMapping(value = "/send")
    public ResponseEntity<String>  createMail(@RequestBody MailObject mailObject) {
        try {
            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("recipientName", mailObject.getRecipientName());
            templateModel.put("registrationLink", mailObject.getRegistrationLink());
            templateModel.put("senderName", mailObject.getSenderName());

            emailService.sendMessageUsingFreemarkerTemplate(
                    mailObject.getTo(),
                    mailObject.getSubject(),
                    templateModel
            );
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            System.out.println("INTERNAL_SERVER_ERROR createMail : " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error : " + e);
        }
    }
}
