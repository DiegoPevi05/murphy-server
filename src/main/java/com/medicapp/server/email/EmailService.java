package com.medicapp.server.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void send(String toAddress, String subject,String title, String name, String message, String link, String type) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(fromEmail);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        if(type.equals("register")){
            helper.setText(getHtmlContentRegister(title, name, link), true);
        }
        if(type.equals("message")){
            helper.setText(getHtmlContentSimpleMessage(title, name, message), true);
        }
        mailSender.send(mimeMessage);
    }

    private String getHtmlContentSimpleMessage(String title, String name, String message) {
        Context context = new Context();
        context.setVariable("title", title);
        context.setVariable("name", name);
        context.setVariable("message", message);
        return templateEngine.process("BasicMessageTemplate", context);
    }

    private String getHtmlContentRegister(String title, String name, String link) {
        Context context = new Context();
        context.setVariable("title", title);
        context.setVariable("name", name);
        context.setVariable("link", link);
        return templateEngine.process("BasicMessageLinkTemplate", context);
    }
}
