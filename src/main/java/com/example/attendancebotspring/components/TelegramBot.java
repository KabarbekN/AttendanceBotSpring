package com.example.attendancebotspring.components;
import com.example.attendancebotspring.configurations.BotConfiguration;
import com.example.attendancebotspring.models.Dictionary;
import com.example.attendancebotspring.models.firebird_models.Staff;
import com.example.attendancebotspring.models.firebird_models.TabelIntermediadate;
import com.example.attendancebotspring.models.mysql_models.Kid;
import com.example.attendancebotspring.models.mysql_models.Language;
import com.example.attendancebotspring.models.mysql_models.User;
import com.example.attendancebotspring.models.mysql_models.UserKid;
import com.example.attendancebotspring.repositories.mysql_repos.IKidRepository;
import com.example.attendancebotspring.services.firebird_service.StaffService;
import com.example.attendancebotspring.services.mysql_service.KidService;
import com.example.attendancebotspring.services.mysql_service.LanguageService;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
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
    private final LanguageService languageService;
    private enum TelegramBotStates {
        BASIC,
        INITIAL,
        ADD_KID,
        UNSUBSCRIPTION
    }
    String HELP = "Бұл болашақта жазылады";

    private TelegramBotStates telegramBotStates;
    private final Dictionary dictionary =  new Dictionary();

    String GLOBAL_LANGUAGE = "Қазақ тілі";

    public TelegramBot(UserService userService, KidService kidService, UserKidService userKidService, StaffService staffService, BotConfiguration botConfiguration,
                       IKidRepository iKidRepository, LanguageService languageService) {
        this.userService = userService;
        this.kidService = kidService;
        this.userKidService = userKidService;
        this.staffService = staffService;
        this.botConfiguration = botConfiguration;
        this.languageService = languageService;
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

        Language language = languageService.findByUserIdLanguage(update.getMessage().getChatId());
        if (language != null){
            GLOBAL_LANGUAGE = language.getLanguage();
        }

        if (update.hasMessage()) {
            addBotCommands(update.getMessage().getChatId());
        }

        if (update.hasMessage() && update.getMessage().hasContact()) {
            handleRegisterButton(update.getMessage().getChatId(), update);
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();


            switch (messageText) {
                case "/start", "Назад", "Артқа":

                    if (languageService.findByUserIdLanguage(chatId) == null){
                        chooseLanguage(chatId);
                        telegramBotStates = TelegramBotStates.BASIC;
                    }else {


                        telegramBotStates = TelegramBotStates.INITIAL;
                    }
                    break;
                case "Регистрация", "/register", "Тіркелу":
                    if (!checkRegistered(chatId)) {
                        sendMessageWithKeyboard(chatId,
                                sentenceTranslation("Телефон нөміріңізді бөлісіңіз"),
                                createContactKeyboard(chatId));
                    } else {
                        sendMessage(chatId, sentenceTranslation("Сіз әлдеқашан тіркелгенсіз"));
                    }
                    break;
                case "Добавить ребенка", "/addkid", "Баланы қосу":
                    if (checkRegistered(chatId)) {
                        sendMessageWithKeyboard(chatId, sentenceTranslation("Балаңыздың staff_id жазыңыз"), buttonBack());
                        telegramBotStates = TelegramBotStates.ADD_KID;
                    } else {
                        sendMessageWithKeyboard(chatId, sentenceTranslation("Сіз тіркелмегенсіз, алдымен тіркеліңіз"), basicCommandsKeyboard());
                    }
                    break;
                case "Подписка", "/subscription", "Жазылым":
                    outputKidList(chatId);
                    break;
                case "Отписаться", "/unsubscribe", "Жазылудан бас тарту":
                    sendMessageWithKeyboard(chatId, sentenceTranslation("Жазылымнан бас тарту үшін бала идентификаторын жазыңыз"), buttonBack());
                    telegramBotStates = TelegramBotStates.UNSUBSCRIPTION;
                    break;
                case "Помощь", "/help", "Анықтама":
                    sendMessageWithKeyboard(chatId, HELP, buttonBack());
                    break;
                case "Қазақ тілі":
                    setLanguage(chatId, "Қазақ тілі");
                    telegramBotStates = TelegramBotStates.INITIAL;
                    break;
                case "Русский язык":
                    setLanguage(chatId, "Русский язык");
                    telegramBotStates = TelegramBotStates.INITIAL;
                    break;

            }
            switch (telegramBotStates) {
                case INITIAL -> startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                case ADD_KID -> handleAddKid(chatId, update);
                case UNSUBSCRIPTION -> deleteKidFromSubscription(chatId, update);
            }
        }
    }

    public void setLanguage(Long chatId, String language) {
        Language language1 = new Language(chatId, language);
        languageService.saveLanguage(language1);
        GLOBAL_LANGUAGE = language;
    }
    public void chooseLanguage(Long chatId){

        ReplyKeyboardMarkup replyKeyboardMarkup = readyReplyKeyboardMarkUp();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Қазақ тілі");
        row.add("Русский язык");
        keyboardRows.add(row);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        sendMessageWithKeyboard(chatId, "Тілді таңдаңыз", replyKeyboardMarkup);
    }

    private void addBotCommands(Long chatId){
        List<BotCommand> botCommands = new ArrayList<>();

        botCommands.add(new BotCommand("/start", sentenceTranslation("Ботты іске қосу")));
        botCommands.add(new BotCommand("/register", sentenceTranslation("Пайдаланушыны тіркеу")));
        botCommands.add(new BotCommand("/addkid",sentenceTranslation("Баланы қосу")));
        botCommands.add(new BotCommand("/subscription", sentenceTranslation("Балалар тізімін алу")));
        botCommands.add(new BotCommand("/help", sentenceTranslation("Қосымша ақпарат алу")));
        botCommands.add(new BotCommand("/unsubscribe", sentenceTranslation("Жазылымды жою")));
        try {
            this.execute(new SetMyCommands(botCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list occurred: " + e.getMessage());
        }
    }


    public void deleteKidFromSubscription(Long chatId, Update update) {
        if (update.hasMessage()
                && !update.getMessage().getText().equals("Отписаться")
                && !update.getMessage().getText().equals("/unsubscribe")
                && !update.getMessage().getText().equals("Жазылудан бас тарту")) {
            String message = update.getMessage().getText();
            if (message.matches("\\d+")) {
                Long kidId = Long.parseLong(message);
                Kid kid = kidService.getKidById(kidId);
                if (kid != null) {
                    try {
                        Kid kid1 = kidService.getKidById(kidId);
                        kidService.deleteKidById(kidId);
                        userKidService.deleteByUserIdAndKidId(chatId, kid1.getStaff_id());
                        sendMessageWithKeyboard(chatId, sentenceTranslation("Жазылым сәтті жойылды"), basicCommandsKeyboard());
                        telegramBotStates = TelegramBotStates.BASIC;
                    } catch (Exception e) {
                        log.error("Error occurred: " + e.getMessage());
                    }
                } else {
                    sendMessageWithKeyboard(chatId, sentenceTranslation("Ондай бала жоқ, қайталап көріңіз"), buttonBack());
                    outputKidList(chatId);
                    telegramBotStates = TelegramBotStates.UNSUBSCRIPTION;
                }
            } else {
                sendMessageWithKeyboard(chatId, sentenceTranslation("Сан емес, қайталап көріңіз"), buttonBack());
                telegramBotStates = TelegramBotStates.ADD_KID;
            }
        }
    }

    private ReplyKeyboardMarkup buttonBack() {
        ReplyKeyboardMarkup replyKeyboardMarkup = readyReplyKeyboardMarkUp();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(sentenceTranslation("Артқа"));
        keyboardRows.add(row);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    private ReplyKeyboardMarkup unsubscribeButton() {
        ReplyKeyboardMarkup replyKeyboardMarkup = readyReplyKeyboardMarkUp();

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(sentenceTranslation("Жазылудан бас тарту"));
        row.add(sentenceTranslation("Артқа"));
        keyboardRows.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    private void outputKidList(Long chatId) {
        List<UserKid> userKids = userKidService.getAllUserKidByUserId(chatId);
        List<Kid> kidList = new ArrayList<>();
        StringBuilder kidsList = new StringBuilder();
        for (UserKid userKid : userKids) {
            kidList.add(kidService.getKidByStaffId(userKid.getKid_id()));
        }

        if (kidList.size() > 0) {
            for (Kid kid : kidList) {
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
            } catch (TelegramApiException e) {
                log.error("Error occurred " + e.getMessage());
            }
        } else {
            sendMessage(chatId, sentenceTranslation("Сізде жазылым жоқ"));
        }

    }

    private boolean checkRegistered(Long chatId) {
        return userService.getUserById(chatId) != null;
    }

    private boolean checkToAddKid(Long chatId, Long kidId) {
        return userKidService.checkIfUserKidExist(chatId, kidId);
    }

    private void handleAddKid(Long chatId, Update update) {
        if (update.hasMessage()
                && !update.getMessage().getText().equals("Добавить ребенка")
                && !update.getMessage().getText().equals("/addkid")
                && !update.getMessage().getText().equals("Баланы қосу")) {
            String staff_id = update.getMessage().getText();

            if (staff_id.matches("\\d+")) {
                Long kidId = Long.parseLong(staff_id);


                Staff staff = staffService.getStaffById(kidId);

                if (!checkToAddKid(chatId, kidId)) {
                    if (staff == null) {
                        sendMessageWithKeyboard(chatId, sentenceTranslation("Дерекқорда мұндай бала жоқ, қайталап көріңіз"), buttonBack());
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


                        sendMessage(chatId, sentenceTranslation("Бала сәтті қосылды"));
                        telegramBotStates = TelegramBotStates.BASIC;
                    }
                } else {
                    sendMessageWithKeyboard(chatId, sentenceTranslation("Бұл баланыз дерекқорға қосылған"), buttonBack());
                }
            } else {
                sendMessageWithKeyboard(chatId, sentenceTranslation("Сан емес, қайталап көріңіз"), buttonBack());
                telegramBotStates = TelegramBotStates.ADD_KID;
            }
        }
    }

    private String sentenceTranslation(String sentence){
        boolean russian = GLOBAL_LANGUAGE.equals("Русский язык");
        return russian?dictionary.translate(sentence):sentence;

    }

    private void handleRegisterButton(Long chatId, Update update) {
        String phoneNumber = update.getMessage().getContact().getPhoneNumber();
        User user = new User();
        user.setId(chatId);
        user.setFirstName(update.getMessage().getChat().getFirstName());
        user.setLastName(update.getMessage().getChat().getLastName());
        user.setPhoneNumber(phoneNumber);
        userService.saveUser(user);

        sendMessageWithKeyboard(chatId, sentenceTranslation("Тіркеу сәтті өтті"),basicCommandsKeyboard());
    }


    private ReplyKeyboardMarkup basicCommandsKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add(sentenceTranslation("Тіркелу"));
        row1.add(sentenceTranslation("Баланы қосу"));
        row2.add(sentenceTranslation("Жазылым"));
        row2.add(sentenceTranslation("Жазылудан бас тарту"));
        keyboardRows.add(row1);
        keyboardRows.add(row2);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    private void startCommandReceived(Long chatId, String name) {
        ReplyKeyboardMarkup replyKeyboardMarkup = basicCommandsKeyboard();

        SendMessage sendReplyKeyboard = new SendMessage();
        String greeting = sentenceTranslation("Сәлем,");
        String notGreeting = sentenceTranslation(", танысқаныма қуаныштымын!, мәзір пәрменін таңдаңыз");

        sendReplyKeyboard.setText(greeting + " " + name + notGreeting);




        sendReplyKeyboard.setReplyMarkup(replyKeyboardMarkup);
        sendReplyKeyboard.setChatId(String.valueOf(chatId));

        try {
            execute(sendReplyKeyboard);
            telegramBotStates = telegramBotStates.BASIC;
        } catch (TelegramApiException e) {
            log.error("Error occurred with sending reply keyboard markup: " + e.getMessage());
        }
    }

    public void sendNotification(List<TabelIntermediadate> records) {
        for (TabelIntermediadate record : records) {
            Long staffId = record.getSTAFF_ID();

            List<UserKid> userKidList = userKidService.findAllByKidId(staffId);
            for (UserKid userKid : userKidList) {
                Long chatId = userKid.getUser_id();
                StringBuilder message = new StringBuilder();
                String typePass = null;
                if (record.getTYPE_PASS() == 1) {
                    typePass = sentenceTranslation("Кіру");
                } else {
                    typePass = sentenceTranslation("Шығу");
                }
                message.append(record.getDATE_PASS())
                        .append(" ")
                        .append(record.getTIME_PASS())
                        .append(" ")
                        .append(typePass)
                        .append(" ")
                        .append(kidService.getKidById(staffId).getName());
                sendMessage(chatId, String.valueOf(message));
            }
        }
    }

    private ReplyKeyboardMarkup createContactKeyboard(Long chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        KeyboardButton contactButton = new KeyboardButton(sentenceTranslation("Контактіні бөлісу"));

        contactButton.setRequestContact(true);

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(contactButton);
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(keyboardRow);

        keyboardMarkup.setKeyboard(keyboard);

        return keyboardMarkup;
    }


    public ReplyKeyboardMarkup readyReplyKeyboardMarkUp() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        return replyKeyboardMarkup;
    }

    private void sendMessageWithKeyboard(Long chatId, String textToSend, ReplyKeyboardMarkup replyKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(textToSend);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error occurred with sending message " + e.getMessage());
        }
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

}
