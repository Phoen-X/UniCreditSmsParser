package com.vvygulyarniy.to;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 20.07.12
 * Time: 13:37
 */
public class CreditTO implements Comparable<CreditTO>
{
    public enum CreditType {
        ADD, GET;

        public char getSign()
        {
            if(this == ADD)
                return '+';
            else
                return '-';
        }

        public static CreditType getTypeBySign(String sign)
        {
            if("+".equals(sign))
                return ADD;
            if("-".equals(sign))
                return GET;
            return null;
        }
    }

    private int messageId;
    private float amount;
    CreditType type = CreditType.GET;
    private float amountTotal;
    private Date date;
    private String comment;
    private long id;

    public String getComment() {
        return comment;
    }

    public CreditTO setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public long getId() {
        return id;
    }

    public CreditTO setId(long id) {
        this.id = id;
        return this;
    }

    public CreditType getType() {
        return type;
    }

    public CreditTO setType(CreditType type)
    {
        this.type = type;
        return this;
    }

    public int getMessageId() {
        return messageId;
    }

    public CreditTO setMessageId(int messageId) {
        this.messageId = messageId;
        return this;
    }

    public float getAmount() {
        return amount;
    }

    public CreditTO setAmount(float amount) {
        this.amount = amount;
        return this;
    }

    public float getAmountTotal() {
        return amountTotal;
    }

    public CreditTO setAmountTotal(float amountTotal) {
        this.amountTotal = amountTotal;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public String getDateStr()
    {
        if(date != null)
            return new SimpleDateFormat("dd.MM.yyy HH:mm:ss").format(date);
        return "";
    }

    public CreditTO setDate(Date date) {
        this.date = date;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(getDateStr()).append(": ");
        switch (type)
        {
            case ADD: {buffer.append("ADDED ");break;}
            case GET: {buffer.append("GETTED "); break;}
        }

        buffer.append(amount).append("; ");
        buffer.append(String.format("(%s)", comment));

        buffer.append("TOTAL: ").append(amountTotal);
        return buffer.toString().trim();
    }

    public int compareTo(CreditTO creditTO) {
        return this.getDate().compareTo(creditTO.getDate());
    }
}
