package com.example.bot1.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class BotUtils {

    public static final String HELP_MESSAGE = "This bot was created for studying by Taras Matviienko\n\n" +
            "Type /start to start this bot\n\n" +
            "Type /help to see this message again\n\n" +
            "Type /settings to setup this bot";

    public ReplyKeyboardMarkup getKeyboardMarkup(){

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setIsPersistent(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add(Markups.MY_PROFILE);
        row.add(Markups.DELETE_PROFILE);

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        return keyboardMarkup;
    }

    public InlineKeyboardMarkup getInlineKeyboardMarkup(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> agreeOrDisagreeBtns = new ArrayList<>();

        var yesBtn = new InlineKeyboardButton();
        yesBtn.setText("Yes, I agree!");
        yesBtn.setCallbackData("YES_BTN");

        var noBtn = new InlineKeyboardButton();
        noBtn.setText("No, I disagree!");
        noBtn.setCallbackData("NO_BTN");

        agreeOrDisagreeBtns.add(yesBtn);
        agreeOrDisagreeBtns.add(noBtn);

        inlineButtons.add(agreeOrDisagreeBtns);

        inlineKeyboardMarkup.setKeyboard(inlineButtons);
        return inlineKeyboardMarkup;
    }

    public static class Commands{
        public static final String START = "/start";
        public static final String HELP = "/help";
        public static final String SETTINGS = "/settings";
    }

    public static class Markups{
        public static final String MY_PROFILE = "MyProfile";
        public static final String DELETE_PROFILE = "DeleteProfile";
    }
}
