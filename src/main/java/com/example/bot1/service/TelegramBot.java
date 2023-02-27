package com.example.bot1.service;

import com.example.bot1.config.BotConfig;
import com.example.bot1.model.User;
import com.example.bot1.utils.BotUtils;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.bot1.utils.BotUtils.Markups.*;
import static com.example.bot1.utils.BotUtils.Commands.*;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {


    private final DBCommutator commutator;
    private final BotUtils utils;
    private final BotConfig config;

    private boolean inLineButtonsActive = false;

    @Autowired
    public TelegramBot(BotConfig config, BotUtils utils, DBCommutator commutator) {
        super(config.getToken());
        this.config = config;
        this.utils = utils;
        this.commutator = commutator;


//        try {
//            execute(new DeleteMyCommands());
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }

//        setupBotCommands();
    }



    private void setupBotCommands(){
        try {
            this.execute(new SetMyCommands(createBotCommandsList(), new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(this.getClass().getName() + " Error setting bot's command list: " + e.getMessage());
        }
    }

    private List<BotCommand> createBotCommandsList(){
        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand(START, "get welcome message"));
//        commandList.add(new BotCommand(Commands.MY_DATA, "see your data"));
//        commandList.add(new BotCommand(Commands.DELETE_MY_DATA, "delete your data"));
        commandList.add(new BotCommand(HELP, "get info about bot"));
        commandList.add(new BotCommand(SETTINGS, "set up bot"));
//        commandList.add(new BotCommand("/testing", "set up bot"));

        return commandList;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage() && update.getMessage().hasText()){
            Message message = update.getMessage();
            long id = message.getChatId();

            switch (message.getText()){
                case START:
                    startReceivedMessage(id, message);
                    break;
                case HELP:
                    getHelpInfo(id);
                    break;
                case DELETE_PROFILE:
                    confirmUserDataDelete(id);
                    break;
                case SETTINGS:
                    getSettings(id, message);
                    break;
                case MY_PROFILE:
                    getUserData(id);
                    break;
                default:
                    sendMessage(id, "Hello " + message.getChat().getFirstName() + ", put right command");
            }
        } else if(update.hasCallbackQuery() && inLineButtonsActive){
            CallbackQuery query = update.getCallbackQuery();

            userDataDelete(query);
        }


    }

    private void getSettings(long id, BotApiObject message) {
        sendMessage(id, "Here you can see settings for that bot");
    }

    private void getHelpInfo(long id) {
        sendMessage(id, BotUtils.HELP_MESSAGE);
    }

    private void confirmUserDataDelete(long id) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(id);
        sendMessage.setText("Do you really want to delete your account info?");

        sendMessage.setReplyMarkup(utils.getInlineKeyboardMarkup());

        executeMessageSending(sendMessage);
        inLineButtonsActive = true;

    }

    private void userDataDelete(CallbackQuery query){
        inLineButtonsActive = false;
        String callbackData = query.getData();
        Message message = query.getMessage();

        String shownMessage = "Please, click buttons below";

        if(callbackData.equals("YES_BTN")){
            shownMessage =  EmojiParser.parseToUnicode("Your userdata was deleted. (nope :smiling_imp:)");
            commutator.deleteUserData(message.getChatId());
        } else if(callbackData.equals("NO_BTN")){
            shownMessage = EmojiParser.parseToUnicode("Your userdata was not deleted :wink:");
        }

        EditMessageText newMessage = new EditMessageText();
        newMessage.setChatId(message.getChatId());
        newMessage.setMessageId(message.getMessageId());
        newMessage.setText(shownMessage);

        executeMessageSending(newMessage);

    }

    private void getUserData(long id){
        Optional<User> user = commutator.getUserData(id);

        if(user.isEmpty()){
            sendMessage(id, "We don't have any data about you");
            log.info(this.getClass().getName() + " Data about user was not received");
        } else {
            sendMessage(id, user.get().toString());
        }
    }

    private void startReceivedMessage(long id, BotApiObject msg){

        Message message  = (Message) msg;
        commutator.registerUser(message);

        String answer = EmojiParser.parseToUnicode("Hello, " + message.getChat().getUserName() + " , nice to meet you! " + ":blush:");
        log.info("/start command received from User: [" + message.getChat().getUserName() + "]");

        sendMessage(id, answer);
    }

    private void sendMessage(long chatId, String message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        sendMessage.setReplyMarkup(utils.getKeyboardMarkup());

        executeMessageSending(sendMessage);

    }

    private void executeMessageSending(BotApiMethod msg){
        try {
            execute(msg);
        }
        catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }
    }

}
