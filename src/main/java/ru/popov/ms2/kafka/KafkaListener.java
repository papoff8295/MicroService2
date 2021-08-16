package ru.popov.ms2.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.stereotype.Component;
import ru.popov.ms2.model.Message;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@EnableKafka
@Component
public class KafkaListener {

    @org.springframework.kafka.annotation.KafkaListener(topics = "msg")
    public void msgListener(String msg) throws IOException {

        Gson g = new Gson();
        Message m = g.fromJson(msg, Message.class);

        m.setMC3Time(new Date());
        //Gson is incorrect pars Date from object. use jackson
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String s = ow.writeValueAsString(m);

        //create post request
        URL url = new URL("http://localhost:8081");
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestMethod("POST"); // PUT is another valid option
        http.setDoOutput(true);

        byte[] out = s.getBytes(StandardCharsets.UTF_8);
        int length = out.length;
        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        http.connect();
        try (OutputStream os = http.getOutputStream()) {
            os.write(out);
        }

    }

}
