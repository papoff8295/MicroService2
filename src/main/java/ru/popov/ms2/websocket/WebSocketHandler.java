package ru.popov.ms2.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.popov.ms2.cash.SessionCash;
import ru.popov.ms2.cash.TimeCash;
import ru.popov.ms2.model.Message;

import java.util.Date;

public class WebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private TimeCash timeCash;
    @Autowired
    private SessionCash sessionCash;
    @Autowired
    private KafkaTemplate<Long, String> kafkaTemplate;

    public WebSocketHandler() {
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {

        //Gson g = new Gson();
        //Message m = g.fromJson(message.getPayload(), Message.class);
        Message m = new ObjectMapper().readValue(message.getPayload(), Message.class);

        long sessionId = m.getSessionId();

        //save latest session id to cash
        if (sessionCash.getSessionCash().get("sessionId") < sessionId) {
            sessionCash.saveEventCash("sessionId", sessionId);
        }

        m.setMC2Time(new Date());

        //Gson is incorrect pars Date from object. use jackson
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String s = ow.writeValueAsString(m);
        kafkaTemplate.send("msg", m.getId(), s);
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        System.out.println("Connected!");

        //save start time after action
        timeCash.saveEventCash("start", new Date());
        //set start session id
        sessionCash.saveEventCash("sessionId", 0L);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        //take start time from cash
        Date startDate = timeCash.getTimeCash().get("start");
        //set time end of action
        Date endDate = new Date();
        //we calculate the interaction time
        Date timeWork = new Date(endDate.getTime() - startDate.getTime());
        long time = timeWork.getTime()/1000;
        System.out.println("Disconnected" + "Время взаимодействия: " + time +
                "сек. " + "Кол-во сообщений: " + sessionCash.getSessionCash().get("sessionId"));

    }
}
