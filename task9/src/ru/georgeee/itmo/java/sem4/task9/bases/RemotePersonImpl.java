package ru.georgeee.itmo.java.sem4.task9.bases;

import ru.georgeee.itmo.java.sem4.task9.interfaces.LocalPerson;
import ru.georgeee.itmo.java.sem4.task9.interfaces.RemotePerson;

import java.rmi.Remote;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by georgeee on 08.05.14.
 */
public class RemotePersonImpl implements Remote, RemotePerson {
    protected Map<Integer, AtomicInteger> amounts;
    protected long passportId;
    protected String name;
    protected String surname;

    public RemotePersonImpl(LocalPerson person) {
        amounts = new HashMap<Integer, AtomicInteger>();
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
        AtomicInteger amount = amounts.get(accountId);
        if (amount == null) {
            synchronized (amounts) {
                amount = amounts.get(accountId);
                if (amount == null) {
                    amounts.put(accountId, new AtomicInteger(difference));
                }
            }
        }
        if (amount == null) {
            logChangeAmount(accountId, difference, difference);
            return difference;
        }
        int res = amount.addAndGet(difference);
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
