package com.hacisimsek.microservices.notification.event;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderPlacedEvent {
  private String orderNumber;
  private String email;
  private String username;
  private String surname;
}