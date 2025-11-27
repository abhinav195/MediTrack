package org.meditrack.app.entity;

import java.util.UUID;

public class Paitent extends Person {

    String paitentId;
    String MRN;


    public Paitent(String name, String age, String address, String ContactNo, String email) {
        super(name, age, address, ContactNo, email);
        this.paitentId = this.getUniqueID();
        this.MRN = getUniqueID();
    }

    public String getUniqueID()
    {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
