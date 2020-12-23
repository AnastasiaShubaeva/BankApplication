package org.openjfx;

/**
 * Внутреннее исключение для пакета
 */

public class BalanceException extends RuntimeException{
    /**
     * Стандартный конструктор исключения
     */
    public BalanceException() {
        super("Ошибка: недостаточно средств на счёте");
    }

    /**
     * Конструктор исключения с сообщением
     * @param msg Сообщение об ошибке
     */
    public BalanceException(String msg) {
        super(msg);
    }
}
