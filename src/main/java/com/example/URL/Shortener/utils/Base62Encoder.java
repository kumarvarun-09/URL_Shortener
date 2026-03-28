package com.example.URL.Shortener.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Base62Encoder {
    private static final String BASE62 = "OPvkMrhDVSRpgJ6X15wGIFTEybs48NuU2Qmi9YHtz0coCAaLKedljqZW37xnf";
    private static final Long randomStart = 387978022L;
    public String encode(Long id) {
        log.info("BASE62: {} , id: {}", BASE62, id);
        long newId = randomStart + id;
        log.info("Nre generated id: {}", newId);
        StringBuilder shortCode = new StringBuilder();
        while(newId > 0){
            int index = Math.toIntExact(newId % BASE62.length());
            Character ch = BASE62.charAt(index);
            log.info("Inside loop, index is {} , char is {}", index, ch);
            shortCode.append(ch);
            newId /= BASE62.length();
        }
        while(shortCode.length() < 6){
            shortCode.append(BASE62.charAt(0));
        }
        log.info("Short code generated: {}", shortCode);
        return shortCode.reverse().toString();
    }
}
