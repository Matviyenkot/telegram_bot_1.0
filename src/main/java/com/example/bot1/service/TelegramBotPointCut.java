package com.example.bot1.service;

import org.aspectj.lang.annotation.Pointcut;

public class TelegramBotPointCut {

    @Pointcut("execution(* TelegramBot.commandsReceiveCases(..))")
    public void onCommandsReceivePointCut(){}
}
