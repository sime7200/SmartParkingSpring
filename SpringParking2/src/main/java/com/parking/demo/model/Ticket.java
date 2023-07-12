package com.parking.demo.model;


public class Ticket {

    private int idTicket;
    private boolean pagato;

    public Ticket(int idTicket) {
        this.idTicket = idTicket;
        this.pagato = false;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public boolean isPagato() {
        return pagato;
    }

    public void markAsPaid() {
        pagato = true;
    }
}
