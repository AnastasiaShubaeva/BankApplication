package org.openjfx;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class BankAccount implements Serializable{
    private AccountHolder holder;                   //владелец счёта
    private double balance;                         //баланс счёта
    private boolean open;                           //статус счёт (открыт/закрыт)
    private HashMap<Calendar, String> history;      //история операций по счёту
    private final int number;                       //номер счёта

    /**
     * Конструктор банковского счета
     * @param holder имя собственника счеа
     * @param number номер счета
     */
    public BankAccount(AccountHolder holder, int number){
        this.holder = holder;
        this.balance = 0;
        this.open = true;
        this.number = number;
        this.history = new HashMap<>();
        this.history.put(new GregorianCalendar(), "Account of " + this.holder.getName() + " " +
                this.holder.getSurname() + " has opened -> Balance is " + this.balance);
        holder.getAccountList().add(this);
    }




    /**
     * Получение информации о счёте
     * @return Информация о счёте
     */
    @Override
    public String toString() {
        return "BankAccount{" +
                "\tholder=" + holder +
                ", \tbalance=" + balance +
                ", \topen=" + open +
                ", \tnumber=" + number +
                '}';
    }



    public void addOperationToHistory(Calendar date, String operation) {
        this.history.put(date, operation);
    }

    public void setHistory(HashMap<Calendar, String> history) {
        this.history = history;
    }

    public HashMap<Calendar, String> getHistory() {
        return history;
    }

    public int getNumber() {
        return number;
    }
    public AccountHolder getHolder() {
        return holder;
    }
    public void setHolder(AccountHolder holder) {
        this.holder = holder;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }


}

