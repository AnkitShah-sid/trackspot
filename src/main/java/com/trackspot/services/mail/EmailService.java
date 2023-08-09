package com.trackspot.services.mail;

import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Olga on 8/22/2016.
 */
public interface EmailService {
    void sendSimpleMessage(String to,
                           String subject,
                           String text) throws Exception;
    void sendSimpleMessageUsingTemplate(String to,
                                        String subject,
                                        String ...templateModel);
    void sendMessageWithAttachment(String to,
                                   String subject,
                                   String text,
                                   String pathToAttachment);

    void sendMessageUsingFreemarkerTemplate(String to,
                                            String subject,
                                            Map<String, Object> templateModel)
            throws IOException, TemplateException, MessagingException;
}
