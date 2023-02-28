package com.example.bot1.AOPProxy;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class TelegramBotLoggerAspect {

    @Before("execution(* com.example.bot1.service.TelegramBot.commandsReceiveCases(..))")
    private void commandsReceiveAspect(JoinPoint jp){
        log.info(jp.getSignature().getName());
        System.out.println("Before!!!!!!!!");
    }
}
