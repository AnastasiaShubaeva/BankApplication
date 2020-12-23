package org.openjfx;

import java.io.Serializable;
import java.util.*;

public class AccountHolder implements Serializable {
    private String name;
    private String surname;
    private Calendar dateOfBirth;
    private List<BankAccount> accountList;

    public AccountHolder(String name, String surname, Calendar dateOfBirth){
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.accountList = new ArrayList<BankAccount>();
    }

    /**
     * Получение информации о владельце счета
     * @return Информация о владельце счета
     */
    @Override
    public String toString() {
        return "AccountHolder{" +
                "\tname='" + name + '\'' +
                ", \tsurname='" + surname + '\'' +
                ", \tdateOfBirth=" + dateOfBirth +
                '}';
    }

    /**
     * Получение информации о регистрации владельц счета
     * @return true - уже зарегистрирован, false - ещё не зарегистрирован
     */
    public boolean equalsHolder(String name, String surname, Calendar dateOfBirth){
        return ((this.name.equals(name))&(this.surname.equals(surname))&(this.dateOfBirth.equals(dateOfBirth)));
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Calendar getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Calendar dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<BankAccount> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<BankAccount> accountList) {
        this.accountList = accountList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
   }
}
