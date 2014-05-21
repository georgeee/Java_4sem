package ru.georgeee.itmo.java.sem4.task9.bases;

import ru.georgeee.itmo.java.sem4.task9.interfaces.LocalPerson;

/**
 * Created by georgeee on 08.05.14.
 */
public class LocalPersonImpl implements LocalPerson {
    protected final long passportId;
    protected final String name;
    protected final String surname;

    public LocalPersonImpl(long passportId, String name, String surname) {
        this.passportId = passportId;
        this.name = name;
        this.surname = surname;
    }

    public String getSurname() {
        return surname;
    }

    @Override
    public boolean verify(LocalPerson person) {
        return person.equals(this);
    }

    public long getPassportId() {
        return passportId;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocalPersonImpl)) return false;

        LocalPersonImpl that = (LocalPersonImpl) o;

        if (passportId != that.passportId) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(surname != null ? !surname.equals(that.surname) : that.surname != null);

    }
}
