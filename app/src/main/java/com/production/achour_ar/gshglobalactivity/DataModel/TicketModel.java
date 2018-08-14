package com.production.achour_ar.gshglobalactivity.DataModel;

public class TicketModel {
    String titreTicket;
    String slaTicket;
    String DateTicket;

    String UrgenceTicket;

    boolean ticketEnRetard;

    String TempsRestantTicket;
    String idTicket;

    public TicketModel(String titreTicket, String slaTicket, String dateTicket, String tempsRestantTicket, String idticket) {
        this.titreTicket = titreTicket;
        this.slaTicket = slaTicket;
        DateTicket = dateTicket;
        TempsRestantTicket = tempsRestantTicket;
        idTicket = idticket;
    }

    public String getTitreTicket() {
        return titreTicket;
    }

    public String getSlaTicket() {
        return slaTicket;
    }

    public String getDateTicket() {
        return DateTicket;
    }

    public String getUrgenceTicket() {
        return UrgenceTicket;
    }

    public void setUrgenceTicket(String urgenceTicket) {
        UrgenceTicket = urgenceTicket;
    }

    public void setTempsRestantTicket(String tempsRestantTicket) {
        TempsRestantTicket = tempsRestantTicket;
    }

    public String getTempsRestantTicket() {
        return TempsRestantTicket;
    }

    public boolean isTicketEnRetard() {
        return ticketEnRetard;
    }

    public void setTicketEnRetard(boolean ticketEnRetard) {
        this.ticketEnRetard = ticketEnRetard;
    }

    public String getIdTicket() {
        return idTicket;
    }
}