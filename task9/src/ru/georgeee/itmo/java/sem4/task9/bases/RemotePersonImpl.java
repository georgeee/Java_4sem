package ru.georgeee.itmo.java.sem4.task9.bases;

import ru.georgeee.itmo.java.sem4.task9.interfaces.LocalPerson;
import ru.georgeee.itmo.java.sem4.task9.interfaces.RemotePerson;

import java.rmi.Remote;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by georgeee on 08.05.14.
 */
public class RemotePersonImpl implements Remote, RemotePerson {
    //Map is assumed to be thread-safe
    protected final ConcurrentMap<Integer, AtomicInteger> amounts;
    protected final long passportId;
    protected final String name;
    protected final String surname;

    public RemotePersonImpl(LocalPerson person) {
        amounts = new ConcurrentHashMap<Integer, AtomicInteger>();
        passportId = person.getPassportId();
        name = person.getName();
        surname = person.getSurname();
    }

    @Override
    public int getAmount(int accountId) {
        AtomicInteger amount = amounts.get(accountId);
        if (amount == null) return 0;
        return amount.get();
    }

    @Override
    public int changeAmount(int accountId, int difference) {
        amounts.putIfAbsent(accountId, new AtomicInteger());
        int res = amounts.get(accountId).addAndGet(difference);
        logChangeAmount(accountId, difference, res);
        return res;
    }

    private void logChangeAmount(int accountId, int difference, int newBalance) {
        System.err.printf("%s %s (passportId: %d) accountId=%d was changed by %d. New balance: %d\n", name, surname, passportId, accountId, difference, newBalance);
    }

    @Override
    public LocalPerson createLocal() {
        return new LocalPersonImpl(passportId, name, surname);
    }

    @Override
    public boolean verify(LocalPerson person) {
        return createLocal().verify(person);
    }

    public String getSurname() {
        return surname;
    }

    public long getPassportId() {
        return passportId;
    }

    public String getName() {
        return name;
    }


}
