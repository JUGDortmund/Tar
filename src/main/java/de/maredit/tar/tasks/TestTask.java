package de.maredit.tar.tasks;

import org.springframework.stereotype.Component;

@Component
public class TestTask {
    @Scheduled(fixedRate = 50000)
    public void reportCurrentTime() {
        System.out.println("I can not do that Dave!!!");
    }
}