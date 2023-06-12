package com.example.attendancebotspring.components;

import com.example.attendancebotspring.configurations.BotConfiguration;
import com.example.attendancebotspring.models.firebird_models.Staff;
import com.example.attendancebotspring.models.firebird_models.TabelIntermediadate;
import com.example.attendancebotspring.models.mysql_models.Kid;
import com.example.attendancebotspring.models.mysql_models.User;
import com.example.attendancebotspring.models.mysql_models.UserKid;
import com.example.attendancebotspring.repositories.mysql_repos.IKidRepository;
import com.example.attendancebotspring.services.firebird_service.StaffService;
import com.example.attendancebotspring.services.firebird_service.TabelIntermediateService;
import com.example.attendancebotspring.services.mysql_service.KidService;
import com.example.attendancebotspring.services.mysql_service.UserKidService;
import com.example.attendancebotspring.services.mysql_service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final IKidRepository iKidRepository;
    private final UserService userService;
    private final KidService kidService;
    private final UserKidService userKidService;
    private final StaffService staffService;
    private final BotConfiguration botConfiguration;


    private enum TelegramBotStates{
        BASIC,
        INITIAL,
        REGISTER,
        ADD_KID,
        UNSUBSCRIPTION
    }
    private TelegramBotStates telegramBotStates;

    public TelegramBot(UserService userService, KidService kidService, UserKidService userKidService, StaffService staffService, BotConfiguration botConfiguration,
                       IKidRepository iKidRepository){
        this.userService = userService;
        this.kidService = kidService;
        this.userKidService = userKidService;
        this.staffService = staffService;
        this.botConfiguration = botConfiguration;
        List<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new BotCommand("/start", "start bot"));
        botCommands.add(new BotCommand("/register", "Register User"));
        botCommands.add(new BotCommand("/addkid", "Add kid"));
        botCommands.add(new BotCommand("/subscription", "Get kids list"));
        botCommands.add(new BotCommand("/help", "Get more info"));
        try {
            this.execute(new SetMyCommands(botCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list occurred: " + e.getMessage());
        }
        this.iKidRepository = iKidRepository;
    }
    @Override
    public String getBotUsername() {
        return botConfiguration.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfiguration.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText){
                case "/start":
                    telegramBotStates = TelegramBotStates.INITIAL;
                    break;
                case "Register":
                    if (!checkRegistered(chatId)){
                    sendMessage(chatId, "Please input your phone number in format +77761230102");
                    telegramBotStates = TelegramBotStates.REGISTER;}
                    else {
                        sendMessage(chatId, "You are already registered");
                    }
                    break;
                case "Add kid":
                    if (checkRegistered(chatId)){
                        sendMessage(chatId, "Please write staff_id of your kid");
                        telegramBotStates = TelegramBotStates.ADD_KID;
                    }
                    else{
                        sendMessage(chatId, "You are not registered, register first");
                    }
                    break;
                case "Subscription":
                      outputKidList(chatId);
                      break;
                case "Unsubscribe":
                      sendMessage(chatId, "Write a kid id to delete subscription");
                      telegramBotStates = TelegramBotStates.UNSUBSCRIPTION;
                      break;
            }
            switch (telegramBotStates){
                case INITIAL -> startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                case REGISTER -> handleRegisterButton(chatId, update);
                case ADD_KID -> handleAddKid(chatId, update);
                case UNSUBSCRIPTION -> deleteKidFromSubscription(chatId, update);
            }
        }
    }
    public void deleteKidFromSubscription(Long chatId, Update update){
        if (update.hasMessage() && !update.getMessage().getText().equals("Unsubscribe")) {
            String message = update.getMessage().getText();
            if (message.matches("\\d+")) {
                Long kidId = Long.parseLong(message);
                Kid kid = kidService.getKidById(kidId);
                if (kid != null){
                    try {
                        Kid kid1 = kidService.getKidById(kidId);
                        kidService.deleteKidById(kidId);

                        userKidService.deleteByUserIdAndKidId(chatId, kid1.getStaff_id());

                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setReplyMarkup(basicCommandsKeyboard());
                        sendMessage.setText("Subscription was deleted successfully");
                        sendMessage.setChatId(String.valueOf(chatId));
                        try{
                            execute(sendMessage);
                        }catch (TelegramApiException e){
                            log.error("Error occurred in sending message "  + e.getMessage());
                        }

                        telegramBotStates = TelegramBotStates.BASIC;
                    }catch (Exception e){
                        log.error("Error occurred: " + e.getMessage());
                    }
                }else {
                    sendMessage(chatId, "No such kid, please try again");
                    outputKidList(chatId);
                    telegramBotStates = TelegramBotStates.UNSUBSCRIPTION;
                }
            } else {
                sendMessage(chatId, "Input is not number, please try again");
                telegramBotStates = TelegramBotStates.ADD_KID;
            }
        }
    }
    private ReplyKeyboardMarkup unsubscribeButton(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Unsubscribe");
        keyboardRows.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;

    }

    private void outputKidList(Long chatId){
        List<UserKid> userKids = userKidService.getAllUserKidByUserId(chatId);
        List<Kid> kidList = new ArrayList<>();
        StringBuilder kidsList = new StringBuilder();
        for(UserKid userKid: userKids){
            kidList.add(kidService.getKidByStaffId(userKid.getKid_id()));
        }

        if (kidList.size() > 0){
            for (Kid kid: kidList){
                kidsList.append("Kid id ")
                        .append(kid.getId())
                        .append(" ")
                        .append(kid.getName())
                        .append(" Staff_id ")
                        .append(kid.getStaff_id())
                        .append(" table_id ")
                        .append(kid.getTable_id())
                        .append("\n");
            }

            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(String.valueOf(kidsList));
            sendMessage.setChatId(String.valueOf(chatId));
            sendMessage.setReplyMarkup(unsubscribeButton());
            try {
                execute(sendMessage);
            }catch (TelegramApiException e){
                log.error("Error occurred " + e.getMessage());
            }
        }
        else {
            sendMessage(chatId, "You do not have any subscription");
        }

    }

    private boolean checkRegistered(Long chatId){
        return userService.getUserById(chatId) != null;
    }

    private boolean checkToAddKid(Long chatId, Long kidId){
        return userKidService.checkIfUserKidExist(chatId, kidId);
    }

    private void handleAddKid(Long chatId, Update update) {
        if (update.hasMessage() && !update.getMessage().getText().equals("Add kid")) {
            String staff_id = update.getMessage().getText();

            if (staff_id.matches("\\d+")) {
                Long kidId = Long.parseLong(staff_id);


                Staff staff = staffService.getStaffById(kidId);

                if (!checkToAddKid(chatId, kidId)) {
                    if (staff == null) {
                        sendMessage(chatId, "No such kid in database, please try again");
                    } else {
                        Kid kid = new Kid();


                        kid.setTable_id(Long.parseLong(staff.getTABEL_ID().strip()));
                        Long STAFF_ID = staff.getID_STAFF();
                        kid.setStaff_id(STAFF_ID);
                        String full_fio = staffService.getStaffById(STAFF_ID).getFULL_FIO();
                        kid.setName(full_fio);

                        iKidRepository.save(kid);

                        UserKid userKid = new UserKid();


                        userKid.setKid_id(staff.getID_STAFF());
                        userKid.setUser_id(chatId);
                        userKidService.saveUserKid(userKid);


                        sendMessage(chatId, "Kid was successfully added");
                        telegramBotStates = TelegramBotStates.BASIC;
                    }
                }
                else{
                    sendMessage(chatId, "Such kid already added in to the database");
                }
            } else {
                sendMessage(chatId, "Input is not number, please try again");
                telegramBotStates = TelegramBotStates.ADD_KID;
            }
        }
    }

    private void handleRegisterButton(Long chatId, Update update){
        if (update.hasMessage() && !update.getMessage().getText().equals("Register")){

            String phoneNumber = update.getMessage().getText();

            String regexPattern = "\\+7\\d{10}";
            if(phoneNumber.matches(regexPattern)){
                User user = new User();
                user.setId(chatId);
                user.setFirstName(update.getMessage().getChat().getFirstName());
                user.setLastName(update.getMessage().getChat().getLastName());
                user.setPhoneNumber(phoneNumber);
                userService.saveUser(user);
                sendMessage(chatId, "Registration was successful");
                telegramBotStates = TelegramBotStates.BASIC;
            }
            else {
                sendMessage(chatId, "Number format incorrect, please try again");
                telegramBotStates = TelegramBotStates.REGISTER;
            }
        }

    }

    private ReplyKeyboardMarkup basicCommandsKeyboard(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("Register");
        row1.add("Add kid");
        row1.add("Subscription");
        keyboardRows.add(row1);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }
    private void startCommandReceived(Long chatId, String name){
        ReplyKeyboardMarkup replyKeyboardMarkup = basicCommandsKeyboard();

        SendMessage sendReplyKeyboard = new SendMessage();

        sendReplyKeyboard.setText("Hi, " + name + ", nice to meet you!, choose from menu command");
        sendReplyKeyboard.setReplyMarkup(replyKeyboardMarkup);
        sendReplyKeyboard.setChatId(String.valueOf(chatId));

        try {
            execute(sendReplyKeyboard);
            telegramBotStates = telegramBotStates.BASIC;
        } catch (TelegramApiException e) {
            log.error("Error occurred with sending reply keyboard markup: "  + e.getMessage());
        }
    }
    private void sendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        }
        catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }
    }

    public void sendNotification(List<TabelIntermediadate> records){
        for (TabelIntermediadate record: records){
            Long staffId = record.getSTAFF_ID();

            List<UserKid> userKidList = userKidService.findAllByKidId(staffId);
            for (UserKid userKid: userKidList){
                Long chatId = userKid.getUser_id();
                StringBuilder message = new StringBuilder();
                String typePass = null;
                if (record.getTYPE_PASS() == 1){
                    typePass = "Enter";
                }else{
                    typePass = "Exit";
                }
                message.append(record.getDATE_PASS())
                        .append(" ")
                        .append(record.getTIME_PASS())
                        .append(" ")
                        .append(typePass)
                        .append(" ")
                        .append(kidService.getKidById(staffId).getName());
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(String.valueOf(chatId));
                sendMessage.setText(String.valueOf(message));

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    log.error("Error occurred with sendMessage function: " + e.getMessage());
                }
            }
        }


    }

}
