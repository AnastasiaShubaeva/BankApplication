package org.openjfx;

/**
 * Внутреннее исключение для пакета
 */

public class BankException extends RuntimeException{
    /**
     * Стандартный конструктор исключения
     */
    public BankException() {
        super("Ошибка: Исключение в системе банка");
    }

    /**
     * Конструктор исключения с сообщением
     * @param msg Сообщение об ошибке
     */
    public BankException(String msg) {
        super(msg);
    }
}
