package ru.georgeee.itmo.java.sem4.task9.interfaces;

import java.io.Serializable;

/**
 * Created by georgeee on 08.05.14.
 */
public interface LocalPerson extends Serializable {
    public long getPassportId();

    public String getName();

    public String getSurname();

    public boolean verify(LocalPerson person);
}

