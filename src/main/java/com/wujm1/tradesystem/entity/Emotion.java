package com.wujm1.tradesystem.entity;

import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@ToString
public class Emotion implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column emotion.date
     *
     * @mbg.generated
     */
    private String date;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column emotion.open
     *
     * @mbg.generated
     */
    private BigDecimal open;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column emotion.close
     *
     * @mbg.generated
     */
    private BigDecimal close;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column emotion.high
     *
     * @mbg.generated
     */
    private BigDecimal high;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column emotion.low
     *
     * @mbg.generated
     */
    private BigDecimal low;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column emotion.ma3
     *
     * @mbg.generated
     */
    private BigDecimal ma3;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column emotion.ma5
     *
     * @mbg.generated
     */
    private BigDecimal ma5;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column emotion.last_date
     *
     * @mbg.generated
     */
    private String lastDate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column emotion.chg
     *
     * @mbg.generated
     */
    private BigDecimal chg;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table emotion
     *
     * @mbg.generated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column emotion.date
     *
     * @return the value of emotion.date
     *
     * @mbg.generated
     */
    public String getDate() {
        return date;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column emotion.date
     *
     * @param date the value for emotion.date
     *
     * @mbg.generated
     */
    public void setDate(String date) {
        this.date = date == null ? null : date.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column emotion.open
     *
     * @return the value of emotion.open
     *
     * @mbg.generated
     */
    public BigDecimal getOpen() {
        return open;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column emotion.open
     *
     * @param open the value for emotion.open
     *
     * @mbg.generated
     */
    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column emotion.close
     *
     * @return the value of emotion.close
     *
     * @mbg.generated
     */
    public BigDecimal getClose() {
        return close;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column emotion.close
     *
     * @param close the value for emotion.close
     *
     * @mbg.generated
     */
    public void setClose(BigDecimal close) {
        this.close = close;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column emotion.high
     *
     * @return the value of emotion.high
     *
     * @mbg.generated
     */
    public BigDecimal getHigh() {
        return high;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column emotion.high
     *
     * @param high the value for emotion.high
     *
     * @mbg.generated
     */
    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column emotion.low
     *
     * @return the value of emotion.low
     *
     * @mbg.generated
     */
    public BigDecimal getLow() {
        return low;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column emotion.low
     *
     * @param low the value for emotion.low
     *
     * @mbg.generated
     */
    public void setLow(BigDecimal low) {
        this.low = low;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column emotion.ma3
     *
     * @return the value of emotion.ma3
     *
     * @mbg.generated
     */
    public BigDecimal getMa3() {
        return ma3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column emotion.ma3
     *
     * @param ma3 the value for emotion.ma3
     *
     * @mbg.generated
     */
    public void setMa3(BigDecimal ma3) {
        this.ma3 = ma3;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column emotion.ma5
     *
     * @return the value of emotion.ma5
     *
     * @mbg.generated
     */
    public BigDecimal getMa5() {
        return ma5;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column emotion.ma5
     *
     * @param ma5 the value for emotion.ma5
     *
     * @mbg.generated
     */
    public void setMa5(BigDecimal ma5) {
        this.ma5 = ma5;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column emotion.last_date
     *
     * @return the value of emotion.last_date
     *
     * @mbg.generated
     */
    public String getLastDate() {
        return lastDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column emotion.last_date
     *
     * @param lastDate the value for emotion.last_date
     *
     * @mbg.generated
     */
    public void setLastDate(String lastDate) {
        this.lastDate = lastDate == null ? null : lastDate.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column emotion.chg
     *
     * @return the value of emotion.chg
     *
     * @mbg.generated
     */
    public BigDecimal getChg() {
        return chg;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column emotion.chg
     *
     * @param chg the value for emotion.chg
     *
     * @mbg.generated
     */
    public void setChg(BigDecimal chg) {
        this.chg = chg;
    }
}