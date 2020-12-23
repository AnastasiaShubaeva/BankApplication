package org.openjfx;

import java.io.*;
import java.util.*;


public class Bank {
    private List<BankAccount> bankAccounts; //существующие в банке аккаунты
    private List<AccountHolder> holders;    //зарегистрированные в банке клиенты

    /**
     * Конструктор класса Банк через загрузку файла
     * @param filenameIn Имя файла
     * @throws IOException Ошибка при чтении файла
     * @throws ClassNotFoundException Ошибка при разборе файла
     */
    public Bank (String filenameIn) throws IOException, ClassNotFoundException {
        load(filenameIn);
    }

    /**
     * Конструктор пустого класса Банк
     * @throws BankException Ошибка при создании базового класса логики
     */
    public Bank() throws BankException {
        try {
            bankAccounts = new ArrayList<BankAccount>();
            holders = new ArrayList<AccountHolder>();
        }
        catch (Exception e) {
            throw new BankException("Ошибка при создании банка, приведшая к закрытию приложения");
        }
    }

    /**
     * Загружает текущее состояние из файла
     * @param fileName Имя файла
     * @throws IOException Ошибка при чтении файла
     * @throws ClassNotFoundException Ошибка при разборе файла
     */
    public void load(String fileName) throws IOException, ClassNotFoundException {
        try  (FileInputStream fileInput = new FileInputStream(fileName);
              ObjectInputStream objInput = new ObjectInputStream(fileInput)){
            bankAccounts = (List<BankAccount>) objInput.readObject();
            holders = (List<AccountHolder>) objInput.readObject();
        }
    }

    /**
     * Выгружает текущее состояние в файл
     * @param fileIn Имя файла
     * @throws IOException Ошибка при записи файла
     */
    public void save(String fileIn) throws IOException {
        try  (FileOutputStream fileOut = new FileOutputStream(fileIn);
              ObjectOutputStream objOut = new ObjectOutputStream(fileOut)){
            objOut.writeObject(bankAccounts);
            objOut.writeObject(holders);
        }
    }


    /**
     * Метод открытия банковского счета
     * @param holder владелец счета
     * @param number номер счета
     * @throws BalanceException ошибка: счёт уже открыт
     */
    public void openingAccount(AccountHolder holder, int number) throws BankException{
        if (bankAccounts.stream().mapToInt(BankAccount::getNumber).anyMatch(i-> i==number)){
            throw new BankException("Account already opened");
        } else {
            BankAccount bankAccount = new BankAccount(holder, number);
            bankAccounts.add(bankAccount);
        }
    }

    /**
     * Метод закрытия банковского счета
     * @param number номер счета
     * @throws BalanceException ошибка: счёт не существует или уже закрыт
     */
    public void closingAccount(int number) throws BankException{
        if (bankAccounts.stream().mapToInt(BankAccount::getNumber).anyMatch(i-> i==number)){
            BankAccount account = bankAccounts.stream().filter(i-> i.getNumber()==number).findFirst().get();
            account.setOpen(false);
            account.addOperationToHistory(new GregorianCalendar(), "Account closed");
        } else {
            throw new BankException("Account does not exist");
        }
    }

    /**
     * Метод регистрации клиента
     * @param name имя клиента
     * @param surname фамилия клиента
     * @param dateOfBirth дата рождения клиента
     * @throws BankException Повторная регистрация
     */
    public void registrationHolder(String name, String surname, Calendar dateOfBirth) throws BankException{
        if (alreadyRegistered(name, surname, dateOfBirth)){ throw new BankException("Already registered");}
        else{
            AccountHolder accHolder = new AccountHolder(name, surname, dateOfBirth);
            holders.add(accHolder);
        }
    }

    /**
     * Метод проверки регистрации клиента
     * @param name имя клиента
     * @param surname фамилия клиента
     * @param dateOfBirth дата рождения клиента
     */
     public boolean alreadyRegistered(String name, String surname, Calendar dateOfBirth){
         return holders.stream().anyMatch(i -> i.equalsHolder(name, surname, dateOfBirth));
     }

    /**
     * Метод перевода средств с одного банковского счета на другой
     * @param money сумма перевода
     * @param numberFrom номер счета источника перевода
     * @param numberTo номер счета назначения перевода
     * @throws BalanceException ошибка недостатка средств на счете, отсутствия счета или счёт назначения закрыт
     */
    public void transferBetweenAccounts( int numberFrom, int numberTo, double money) throws BalanceException {
        if (isExist(numberFrom) & isExist(numberTo)) {
            BankAccount accountFrom = bankAccounts.stream().filter(i -> i.getNumber() == numberFrom).findFirst().get();
            if ((accountFrom.getBalance() >= money) ) {
                if (bankAccounts.stream().filter(i -> i.getNumber() == numberTo).findFirst().get().isOpen()) {
                    BankAccount accountTo = bankAccounts.stream().filter(i -> i.getNumber() == numberTo).findFirst().get();
                    accountFrom.setBalance(accountFrom.getBalance() - money);
                    accountTo.setBalance(accountTo.getBalance() + money);
                    accountFrom.addOperationToHistory(new GregorianCalendar(), "Withdrawing funds from account to account number " + numberTo
                            + ": " + money + " -> Balance is " + accountFrom.getBalance());
                    accountTo.addOperationToHistory(new GregorianCalendar(), "Transferring funds from account number " + numberFrom
                            + ": " + money + " -> Balance is " + accountTo.getBalance());
                } else throw new BalanceException("Destination account closed");
            } else throw new BalanceException("Insufficient funds on source account");
        }else throw new BalanceException("Translation source account or destination account do not exist");
    }

    /**
     * Метод пополнения банковского счета
     * @param numberIn номер счёта
     * @param money сумма пополнения
     */
    public void depositIntoAccount(int numberIn, double money) throws BalanceException {
        if (isExist(numberIn)) {
            BankAccount account = bankAccounts.stream().filter(i -> i.getNumber() == numberIn).findFirst().get();
            account.setBalance(account.getBalance() + money);
            account.addOperationToHistory(new GregorianCalendar(), "Account has deposited " + money + " -> Balance is " + account.getBalance());
        }
        else throw new BalanceException("Account does not exist");
    }

    /**
     * Метод снятия средств с банковского счета
     * @param numberIn номер счёта
     * @param money сумма снятия
     * @throws BalanceException ошибка недостатка средств на счете
     */
    public void withdrawalFromAnAccount (int numberIn, double money) throws BalanceException{
        if (isExist(numberIn)) {
            if (bankAccounts.stream().filter(i-> i.getNumber()==numberIn).findFirst().get().isOpen()) {
                BankAccount account = bankAccounts.stream().filter(i -> i.getNumber() == numberIn).findFirst().get();
                if (account.getBalance() >= money) {
                    account.setBalance(account.getBalance() - money);
                    account.addOperationToHistory(new GregorianCalendar(), "Withdrawal from an account " + money + " -> Balance is " + account.getBalance());
                } else throw new BalanceException();
            }else throw new BalanceException("Account closed");
        }else throw new BalanceException("Account does not exist");
    }

    /**
     * Метод получения истории операций
     * @param numberIn номер счёта
     * @throws BankException ошибка недостатка средств на счете
     */
    public HashMap<Calendar, String> history (int numberIn) throws BankException{
        if (isExist(numberIn)){
            return bankAccounts.stream().filter(i -> i.getNumber() == numberIn).findFirst().get().getHistory();
        } else throw new BankException("Account does not exist");
    }

    /**
     * Метод проверки существования счёта
     * @param numberIn номер счёта
     * @throws BankException ошибка недостатка средств на счете
     */
    public boolean isExist (int numberIn){
        return (bankAccounts.stream().anyMatch(i -> i.getNumber() == numberIn));
    }

    /**
     * Метод получения баланса счёта
     * @param numberIn номер счёта
     * @throws BankException ошибка недостатка средств на счете
     */
    public Double getAccBalance (int numberIn) throws BankException{
        if (isExist(numberIn)){
            return bankAccounts.stream().filter(i -> i.getNumber() == numberIn).findFirst().get().getBalance();
        } else throw new BankException("Account does not exist");
    }




    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public List<AccountHolder> getHolders() {
        return holders;
    }

    public void setHolders(List<AccountHolder> holders) {
        this.holders = holders;
    }
}
