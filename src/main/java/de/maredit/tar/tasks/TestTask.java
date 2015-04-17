package de.maredit.tar.tasks;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TestTask {
    //@Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        System.out.println("I can not do that Dave!!!");
    }
}