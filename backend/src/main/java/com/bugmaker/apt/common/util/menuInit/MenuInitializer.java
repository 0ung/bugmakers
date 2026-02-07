package com.bugmaker.apt.common.util.menuInit;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuInitializer implements ApplicationRunner {

    private final MenuBatchService menuBatchService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        menuBatchService.init();
    }
}
