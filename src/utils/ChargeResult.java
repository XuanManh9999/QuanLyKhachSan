/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

/**
 *
 * @author Admin
 */
import java.math.BigDecimal;
import java.math.RoundingMode;
public class ChargeResult {
     public final boolean success;
    public final int minutesUsed;
    public final BigDecimal ratePerHour;
    public final BigDecimal amount; // tiền cuối cùng

    public ChargeResult(boolean success, int minutesUsed, BigDecimal ratePerHour, BigDecimal amount) {
        this.success = success;
        this.minutesUsed = minutesUsed;
        this.ratePerHour = ratePerHour;
        this.amount = amount;
    }
}
