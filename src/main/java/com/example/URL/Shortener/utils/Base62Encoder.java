package com.example.URL.Shortener.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Base62Encoder {
    private static final String BASE62 = "m8oREvOshL7X04I3j2gctA1MawKpxVrkJHuqC9WfPFGNbUzdQDYBSZ6iZ5lyeE";
    private static final long RANDOM_START = 387978022L;
    private static final int BASE62_LENGTH = BASE62.length();

    public String encode(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Id cannot be negative");
        }
        log.debug("BASE62: {} , id: {}", BASE62, id);
        long newId = RANDOM_START + id;
        log.debug("New generated id: {}", newId);
        StringBuilder shortCode = new StringBuilder();
        while(newId > 0){
            int index = Math.toIntExact(newId % BASE62_LENGTH);
            Character ch = BASE62.charAt(index);
            log.debug("Inside loop, index is {} , char is {}", index, ch);
            shortCode.append(ch);
            newId /= BASE62_LENGTH;
        }
        while(shortCode.length() < 6){
            shortCode.append(BASE62.charAt(0));
        }
        log.info("For id: {}, Short code generated: {}", id, shortCode);
        return shortCode.reverse().toString();
    }
}
