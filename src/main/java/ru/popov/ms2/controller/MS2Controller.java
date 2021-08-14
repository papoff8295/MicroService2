package ru.popov.ms2.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;
import ru.popov.ms2.model.Message;

import java.util.Date;

@RestController
public class MS2Controller {


    @MessageMapping("/chat")
    public void processMessage(@Payload Message message) {
        message.setMC2Time(new Date());
        System.out.println(message);
    }

}
