package com.example.bot1.AOPProxy;

import com.example.bot1.model.User;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Aspect
@Slf4j
public class DBCommutatorLoggerAspect {

    @AfterReturning(pointcut = "execution(* com.example.bot1.service.DBCommutator.registerNewUser(..))",
    returning = "isRegistered")
    public void registerUserAspect(JoinPoint joinPoint, boolean isRegistered){
        String className = joinPoint.getSignature().getDeclaringType().getName();
        String methodName = joinPoint.getSignature().getName();

        if(isRegistered){
            log.info(className + "." + methodName + " " + "User was registered");
        }else {
            log.info(className + "." + methodName + " " + " User already had been registered");
        }
    }

    @AfterReturning(pointcut = "execution(* com.example.bot1.service.DBCommutator.getUserData(..))",
            returning = "user")
    public void registerUserAspect(JoinPoint joinPoint, Optional<User> user){
        String className = joinPoint.getSignature().getDeclaringType().getName();
        String methodName = joinPoint.getSignature().getName();
        if(user.isPresent()){
            log.info(className + "." + methodName + " " + user.get().getUserName() + " data was successfully received");
        }else {
            log.info(className + "." + methodName + " Data about user was not received");
        }
    }

}
