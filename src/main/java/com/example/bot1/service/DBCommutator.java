package com.example.bot1.service;

import com.example.bot1.dao.UserRepository;
import com.example.bot1.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Timestamp;
import java.util.Optional;

@Component
@Slf4j
public class DBCommutator {

    private final UserRepository repository;

    @Autowired
    public DBCommutator(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> getUserData(long id){
        return repository.findById(id);
    }

    public void deleteUserData(long id){

    }

    public boolean registerNewUser(Message msg) {
        if(repository.findById(msg.getChatId()).isEmpty()){
            User user = setUpNewUser(msg);
            repository.save(user);
            return true;
        } else {
            return false;
        }
    }

    private User setUpNewUser(Message msg){
        var chatId = msg.getChatId();
        var chat = msg.getChat();

        User user = new User();

        user.setChatId(chatId);
        user.setFirstName(chat.getFirstName());
        user.setLastName(chat.getLastName());
        user.setUserName(chat.getUserName());
        user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
        user.setActive(true);

        return user;
    }
}
