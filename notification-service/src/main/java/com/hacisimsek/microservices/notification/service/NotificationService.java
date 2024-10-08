package com.hacisimsek.microservices.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hacisimsek.microservices.notification.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender javaMailSender;

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-placed", groupId = "notificationService")
    public void listen(String event){

        try {
            if(event != null){
                OrderPlacedEvent orderPlacedEvent = objectMapper.readValue(event, OrderPlacedEvent.class);

                log.info("Got Message from order-placed topic {}", orderPlacedEvent);
                MimeMessagePreparator messagePreparator = mimeMessage -> {
                    MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
                    messageHelper.setFrom("springshop@email.com");
                    messageHelper.setTo(orderPlacedEvent.getEmail().toString());
                    messageHelper.setSubject(String.format("Your Order with OrderNumber %s is placed successfully", orderPlacedEvent.getOrderNumber()));
                    messageHelper.setText(String.format("""
                            Hi %s %s,

                            Your order with order number %s is now placed successfully.

                            Best Regards,
                            """,
                            orderPlacedEvent.getUsername(),
                            orderPlacedEvent.getSurname(),
                            orderPlacedEvent.getOrderNumber()));
                };
                try {
                    javaMailSender.send(messagePreparator);
                    log.info("Order Notification email sent!!");
                } catch (MailException e) {
                    log.error("Exception occurred when sending mail", e);
                    throw new RuntimeException("Exception occurred when sending mail to springshop@email.com", e);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}