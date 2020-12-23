package org.openjfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JavaFX App
 */
public class App extends Application {
    private Bank bank;                        //Основной класс приложения
    private final String FILENAME = "bank.dat";  //Файл для хранения текущего состояния

    private HBox holders = new HBox(20);        //Список клиентов
    private VBox holdersColumn1 = new VBox();      //Колонка "ИМЯ"
    private VBox holdersColumn2 = new VBox();      //Колонка "ФАМИЛИЯ"
    private VBox holdersColumn3 = new VBox();      //Колонка "ДАТА РОЖДЕНИЯ"
    private VBox holdersColumn4 = new VBox();      //Колонка "СПИСОК СЧЕТОВ"


    private HBox accounts = new HBox(20);      //Список счетов
    private VBox accountsColumn1 = new VBox();    //Колонка "НОМЕР СЧЁТА"
    private VBox accountsColumn2 = new VBox();    //Колонка "ВЛАДЕЛЕЦ СЧЁТА"
    private VBox accountsColumn3 = new VBox();    //Колонка "БАЛАНС"
    private VBox accountsColumn4 = new VBox();    //Колонка "ОТКРЫТ(TRUE)/ЗАКРЫТ(FALSE)"


    @Override
    public void start(Stage primaryStage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "Восстановить предыдущее состояние?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Требуется подтверждение");
        alert.setHeaderText("Запрос к пользователю");
        String response = alert.showAndWait().get().getText();
        if (response.equals("Yes")) {
            try {
                bank = new Bank(FILENAME);
                listHolders();
                listAccounts();
                showInfo("Информация загружена");
            } catch (Exception e) {
                showError("Ошибка при открытии файла");
                System.exit(1);     //Выход из программы с ошибкой
            }
        } else {
            try {
                bank = new Bank();
            } catch (BankException ae) {
                showError(ae.getMessage());
                System.exit(1);     //Выход из программы с ошибкой
            } catch (Exception e) {
                showError(e.getMessage());
                System.exit(1);     //Выход из программы с ошибкой
            }
        }

        VBox root = initGUI();
        primaryStage.setTitle("БАНК");
        primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private VBox initGUI() {
        VBox root = new VBox();
        TabPane tabPane = new TabPane();
        Tab tab1 = new Tab("Управление банковскими счетами");
        Tab tab2 = new Tab("Клиенты");
        Tab tab3 = new Tab("Банковские счета");
        tabPane.getTabs().addAll(tab1, tab2, tab3);

        MenuBar bar = new MenuBar();
        bar.setMinHeight(25);
        Menu item = new Menu("Файл");
        Menu saveAndContinueOption = new Menu("Сохранить и продолжить");
        Menu saveAndExitOption = new Menu("Сохранить и выйти");
        Menu exitWithoutSavingOption = new Menu("Выйти без сохранения");
        item.getItems().addAll(saveAndContinueOption, saveAndExitOption, exitWithoutSavingOption);
        bar.getMenus().add(item);
        try {
            saveAndContinueOption.setOnAction(e -> save(FILENAME));
            saveAndExitOption.setOnAction(e -> {
                save(FILENAME);
                Platform.exit();
            });
            exitWithoutSavingOption.setOnAction(e -> exitWithoutSaving());
        } catch (Exception e) {
            showError("Некорректная операция");
        }

        tab1.setContent(initManageBox());
        holders.setPadding(new Insets(10));
        holders.getChildren().addAll(holdersColumn1, holdersColumn2, holdersColumn3, holdersColumn4);
        tab2.setContent(holders);
        accounts.setPadding(new Insets(10));
        accounts.getChildren().addAll(accountsColumn1, accountsColumn2, accountsColumn3, accountsColumn4);
        tab3.setContent(accounts);

        root.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                new CornerRadii(0), new BorderWidths(2))));
        root.getChildren().addAll(bar, tabPane);
        return root;
    }

    /**
     * Создание вкладки с управлением
     * @return Вкладка с управлением системой
     */
    private VBox initManageBox() {
        VBox box = new VBox();
        box.setMinHeight(400);
        box.setAlignment(Pos.BOTTOM_LEFT);
        Label label = new Label("Выберите действие:");
        HBox controls = new HBox(10);
        Button button1 = new Button("Регистрация клиента");
        button1.setTooltip(new Tooltip("Регистрация клиента в банке"));
        Button button2 = new Button("Открытие счёта");
        button2.setTooltip(new Tooltip("Открытие банковского счёта"));
        Button button3 = new Button("Закрытие счёта");
        button3.setTooltip(new Tooltip("Закрытие банковского счёта"));
        Button button4 = new Button("История операций");
        button4.setTooltip(new Tooltip("История операций по счёту"));
        Button button5 = new Button("Пополнение счёта");
        button5.setTooltip(new Tooltip("Пополнение банковского счёта"));
        Button button6 = new Button("Снятие средств");
        button6.setTooltip(new Tooltip("Снятие средств с банковского счета"));
        Button button7 = new Button("Перевод между счетами");
        button7.setTooltip(new Tooltip("Перевед между банковскими счетами"));
        Button button8 = new Button("Баланс");
        button8.setTooltip(new Tooltip("Пулучение информации о балансе банковского счёта"));
        controls.getChildren().addAll(button1, button2, button3, button4, button5, button6, button7, button8);
        box.getChildren().addAll(label, controls);

        try {
            button1.setOnAction(e -> registerHolder());
            button2.setOnAction(e -> openAcc());
            button3.setOnAction(e -> closeAcc());
            button4.setOnAction(e -> getHistoryOperation());
            button5.setOnAction(e -> deposit());
            button6.setOnAction(e -> withdrawal());
            button7.setOnAction(e -> transition());
            button8.setOnAction(e -> getBalance());
        } catch (Exception e) {
            showError("Некорректная операция");
        }
        return box;
    }

    /**
     * Получение баланса счёта
     *
     */
    private void getBalance(){
        String numberIn;
        try {
            numberIn = getAccountNo("Получение баланса счёта");
            try {
                int accNum = Integer.parseInt(numberIn.trim());
                showInfo("Баланс стёта №"+accNum + ": " + bank.getAccBalance(accNum));
            }catch (NumberFormatException nfe){
                showError(nfe.getMessage());
            }
        }catch (BankException be) {
            showError(be.getMessage());
        }
        listAccounts();
    }

    /**
     * Перевод средств между счетами
     */
    private void transition(){
        String numberIn, numberTo, money;
        try {
            numberIn = getAccountNo("Счёт отправитель");
            numberTo = getAccountNo("Счёт получатель");
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Введите сумму перевода");
            dialog.setTitle("Форма ввода суммы перевода");
            money = dialog.showAndWait().get();
            checkIfEmpty(money, "Не введена сумма перевода");
            try {
                int accNumFrom = Integer.parseInt(numberIn.trim());
                int accNumTo = Integer.parseInt(numberTo.trim());
                int summa = Integer.parseInt(money.trim());
                bank.transferBetweenAccounts(accNumFrom, accNumTo, summa);
                showInfo("Средства между счетами успешно переведены");
            }catch (NumberFormatException nfe){
                showError(nfe.getMessage());
            }
        }catch (BankException be) {
            showError(be.getMessage());
        }
        listAccounts();
    }

    /**
     * Снятие средств со счёта
     */
    private void withdrawal(){
        String numberIn, money;
        try {
            numberIn = getAccountNo("Форма снятия средств со счёта");
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Введите сумму снятия");
            dialog.setTitle("Форма ввода суммы снятия");
            money = dialog.showAndWait().get();
            checkIfEmpty(money, "Не введена сумма снятия");
            try {
                int accNum = Integer.parseInt(numberIn.trim());
                int summa = Integer.parseInt(money.trim());
                bank.withdrawalFromAnAccount(accNum, summa);
                showInfo("Средства со счёта успешно сняты");
            }catch (NumberFormatException nfe){
                showError(nfe.getMessage());
            }
        }catch (BankException be) {
            showError(be.getMessage());
        }
        listAccounts();
    }

    /**
     * Пополнение счёта
     */
    private void deposit(){
        String numberIn, money;
        try {
            numberIn = getAccountNo("Форма пополнения счёта");
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Введите сумму пополнения");
            dialog.setTitle("Форма ввода суммы пополнения");
            money = dialog.showAndWait().get();
            checkIfEmpty(money, "Не введена сумма пополнения");
            try {
                int accNum = Integer.parseInt(numberIn.trim());
                int summa = Integer.parseInt(money.trim());
                bank.depositIntoAccount(accNum, summa);
                showInfo("Счёт успешно пополнен");
            }catch (NumberFormatException nfe){
                showError(nfe.getMessage());
            }
        }catch (BankException be) {
            showError(be.getMessage());
        }
        listAccounts();
    }

    /**
     * Регистрация нового клиента в банке
     */
    private void registerHolder() {
        String name, surname, dateOfBirth;
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Введите свое имя");
            dialog.setTitle("Форма открытия счёта");
            name = dialog.showAndWait().get();
            checkIfEmpty(name, "Не указано имя");
            dialog.getEditor().clear();
            dialog.setHeaderText("Введите свою фамилию");
            surname = dialog.showAndWait().get();
            checkIfEmpty(surname, "Не указана фамилия");
            dialog.getEditor().clear();
            dialog.setHeaderText("Введите дату рождения в формате дд.мм.гггг");
            dateOfBirth = dialog.showAndWait().get();
            checkIfEmpty(dateOfBirth, "Не указана дата рождения");
            try{
                SimpleDateFormat curFormater = new SimpleDateFormat("dd.MM.yyyy");
                Date dateObj = curFormater.parse(dateOfBirth);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateObj);
                bank.registrationHolder(name, surname, calendar);
                showInfo("Регистрация прошла успешно");
            }catch (ParseException pe){
                showError(pe.getMessage());
            }
        }catch (BankException be) {
            showError(be.getMessage());
        }
        listHolders();
    }

    /**
     * Открытие счёта в банке
     */
    private void openAcc(){
        String name, surname, dateOfBirth, number;
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Введите свое имя");
            dialog.setTitle("Форма открытия счёта");
            name = dialog.showAndWait().get();
            checkIfEmpty(name, "Не указано имя");
            dialog.getEditor().clear();
            dialog.setHeaderText("Введите свою фамилию");
            surname = dialog.showAndWait().get();
            checkIfEmpty(surname, "Не указана фамилия");
            dialog.getEditor().clear();
            dialog.setHeaderText("Введите дату рождения в формате дд.мм.гггг");
            dateOfBirth = dialog.showAndWait().get();
            checkIfEmpty(dateOfBirth, "Не указана дата рождения");
            number = getAccountNo("Форма открытия счёта");
            try{
                SimpleDateFormat curFormater = new SimpleDateFormat("dd.MM.yyyy");
                Date dateObj = curFormater.parse(dateOfBirth);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateObj);
                int num = Integer.parseInt(number.trim());
                if (bank.alreadyRegistered(name, surname, calendar)){
                    AccountHolder holder = bank.getHolders().stream().filter(i-> i.getName().equals(name)).filter(i-> i.getSurname().equals(surname))
                            .filter(i-> i.getDateOfBirth().equals(calendar)).findFirst().get();
                    bank.openingAccount(holder, num);
                }else {
                    bank.registrationHolder(name, surname, calendar);
                    AccountHolder holder = bank.getHolders().stream().filter(i-> i.getName().equals(name)).filter(i-> i.getSurname().equals(surname))
                            .filter(i-> i.getDateOfBirth().equals(calendar)).findFirst().get();
                    bank.openingAccount(holder, num);
                    showInfo("Счёт успешно открыт");
                }
            }catch (ParseException pe){
                showError(pe.getMessage());
            }
        } catch (BankException be) {
            showError(be.getMessage());
        }
        listHolders();
        listAccounts();
    }

    /**
     * Закрытие счёта в банке
     */
    private void closeAcc(){
        String number;
        try {
            number = getAccountNo("Форма закрытия счёта");
            try {
                int accNum = Integer.parseInt(number.trim());
                bank.closingAccount(accNum);
                showInfo("Счёт успешно закрыт");
            }catch (NumberFormatException nfe){
                showError(nfe.getMessage());
            }
        }catch (BankException be) {
            showError(be.getMessage());
        }
        listAccounts();
        listHolders();
    }

    /**
     * Получение истории операций по счёту
     */
    private void getHistoryOperation(){
        String number;
        HashMap<Calendar, String> historyMap;
        VBox root = new VBox(10);
        HBox historyOperations = new HBox(20);
        HBox name = new HBox();
        VBox dates = new VBox();
        VBox messages = new VBox();
        Stage historyStage = new Stage();
        try {
            number = getAccountNo("Форма получения истории операций по счёту");
            try {
                int accNum = Integer.parseInt(number.trim());
                historyMap =  bank.history(accNum);
                name.getChildren().add(new Text("СЧЁТ №" + number));
                Map<Calendar, String> treeMap = new TreeMap<>(
                (Comparator<Calendar>) (o1, o2) -> o2.compareTo(o1)*(-1)
                );
                treeMap.putAll(historyMap);
                SimpleDateFormat formattedDate = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
                for (Calendar key : treeMap.keySet()) {
                    dates.getChildren().add(new Text(formattedDate.format(key.getTime())));
                    messages.getChildren().add(new Text(historyMap.get(key)));
                }
                historyOperations.getChildren().addAll( dates, messages);
                root.getChildren().addAll(name, historyOperations);
                historyStage.setTitle("ИСТОРИЯ ОПЕРАЦИЙ");
                historyStage.setScene(new Scene(root, 600, 600));
                historyStage.initStyle(StageStyle.UTILITY);
                historyStage.show();
            }catch (NumberFormatException nfe){
                showError(nfe.getMessage());
            }
        }catch (BankException be) {
            showError(be.getMessage());
        }
    }

    /**
     * Получение номера счета от пользователя
     * @param title Текст заголовка
     * @return Номер счета
     * @throws BankException Ошибка при получении номера рейса
     */
    private String getAccountNo(String title) throws BankException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Введите номер счета");
        dialog.setTitle(title);
        String accountNo = dialog.showAndWait().get();
        checkIfEmpty(accountNo, "Не указан номер счета");
        return accountNo;
    }

    /**
     * Выход без сохранения
     */
    private void exitWithoutSaving() {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Вы уверены? Все данные будут потеряны",
                ButtonType.YES, ButtonType.CANCEL);
        alert.setTitle("Требуется подтверждение");
        alert.setHeaderText("Предупреждение");
        String response = alert.showAndWait().get().getText();
        if (response.equals("Yes"))
            Platform.exit();
    }

    /**
     * Список клиентов
     */
    private void listHolders() {
        List<AccountHolder> holdersList = bank.getHolders();
        holdersColumn1.getChildren().clear();
        holdersColumn2.getChildren().clear();
        holdersColumn3.getChildren().clear();
        holdersColumn4.getChildren().clear();


        holdersColumn1.getChildren().add(new Text("ИМЯ"));
        holdersColumn2.getChildren().add(new Text("ФАМИЛИЯ"));
        holdersColumn3.getChildren().add(new Text("ДАТА РОЖДЕНИЯ"));
        holdersColumn4.getChildren().add(new Text("СПИСОК СЧЕТОВ"));


        for (AccountHolder holder : holdersList) {
            holdersColumn1.getChildren().add(new Text(holder.getName()));
            holdersColumn2.getChildren().add(new Text(holder.getSurname()));
            try {
                SimpleDateFormat formattedDate = new SimpleDateFormat("dd.MM.yyyy");
                holdersColumn3.getChildren().add(new Text(formattedDate.format(holder.getDateOfBirth().getTime())));
            } catch (Exception e) {
                holdersColumn3.getChildren().add(new Text(""));
            }
            holdersColumn4.getChildren().add(new Text(holder.getAccountList().stream().map(BankAccount::getNumber).collect(Collectors.toList()).toString()));

        }
    }

    /**
     * Список банковских счетов
     */
    private void listAccounts() {
        List<BankAccount> accountsList = bank.getBankAccounts();
        accountsColumn1.getChildren().clear();
        accountsColumn2.getChildren().clear();
        accountsColumn3.getChildren().clear();
        accountsColumn4.getChildren().clear();


        accountsColumn1.getChildren().add(new Text("НОМЕР СЧЁТА"));
        accountsColumn2.getChildren().add(new Text("ВЛАДЕЛЕЦ СЧЁТА"));
        accountsColumn3.getChildren().add(new Text("БАЛАНС"));
        accountsColumn4.getChildren().add(new Text("ОТКРЫТ(TRUE)/ЗАКРЫТ(FALSE)"));

        for (BankAccount account : accountsList) {
            accountsColumn1.getChildren().add(new Text(""+account.getNumber()));
            accountsColumn2.getChildren().add(new Text(account.getHolder().getName() + " " + account.getHolder().getSurname()));
            accountsColumn3.getChildren().add(new Text(""+account.getBalance()));
            accountsColumn4.getChildren().add(new Text(""+account.isOpen()));

        }
    }

    /**
     * Сохранение данных
     * @param fileName Имя файла
     */
    private void save(String fileName) {
        try {
            bank.save(fileName);
            showInfo("Данные сохранены");
        } catch (Exception e) {
            showError("Ошибка при сохранении файла");
        }
    }

    /**
     * Отображение информации
     * @param msg Сообщение
     */
    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Информация о системе");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Отображение ошибки
     * @param msg Сообщение
     */
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Ошибка в системе");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void checkIfEmpty(String s, String errorMsg) {
        if (s.equals(""))
            throw new BankException(errorMsg);
    }





}