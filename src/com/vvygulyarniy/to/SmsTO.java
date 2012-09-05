package com.vvygulyarniy.to;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 20.07.12
 * Time: 12:15
 */
public class SmsTO
{
    long date;
    String from;
    String to;
    String text;
    String serviceCenterAddress;

    public String getServiceCenterAddress() {
        return serviceCenterAddress;
    }

    public SmsTO setServiceCenterAddress(String serviceCenterAddress) {
        this.serviceCenterAddress = serviceCenterAddress;
        return this;
    }

    public long getDate() {
        return date;
    }

    public SmsTO setDate(long date) {
        this.date = date;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public SmsTO setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getTo() {
        return to;
    }

    public SmsTO setTo(String to) {
        this.to = to;
        return this;
    }

    public String getText() {
        return text;
    }

    public SmsTO setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public String toString() {
        return String.format("FROM: %s; TEXT: %s", from, text);
    }
}
