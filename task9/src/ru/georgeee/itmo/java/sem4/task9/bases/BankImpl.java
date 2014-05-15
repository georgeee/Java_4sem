package ru.georgeee.itmo.java.sem4.task9.bases;

import ru.georgeee.itmo.java.sem4.task9.interfaces.Bank;
import ru.georgeee.itmo.java.sem4.task9.interfaces.LocalPerson;
import ru.georgeee.itmo.java.sem4.task9.interfaces.RemotePerson;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by georgeee on 08.05.14.
 */
public class BankImpl implements Bank {
    Map<Long, RemotePerson> clients;

    public BankImpl() {
        clients = new HashMap<Long, RemotePerson>();
    }

    @Override
    public LocalPerson getVerifyLocalPerson(LocalPerson person) throws RemoteException {
        LocalPerson realPerson = getLocalPerson(person.getPassportId());
        if (realPerson != null && realPerson.verify(person)) return realPerson;
        return null;
    }

    @Override
    public RemotePerson getVerifyRemotePerson(LocalPerson person) throws RemoteException {
        RemotePerson realPerson = getRemotePerson(person.getPassportId());
        if (realPerson != null && realPerson.verify(person)) return realPerson;
        return null;
    }

    @Override
    public LocalPerson getLocalPerson(long passportId) throws RemoteException {
        RemotePerson remotePerson = getRemotePerson(passportId);
        return remotePerson == null ? null : remotePerson.createLocal();
    }

    @Override
    public RemotePerson getRemotePerson(long passportId) {
        return clients.get(passportId);
    }

    @Override
    public boolean createPerson(LocalPerson person) throws RemoteException {
        if (clients.containsKey(person.getPassportId()))
            return false;
        synchronized (clients) {
            if (clients.containsKey(person.getPassportId()))
                return false;
            RemotePersonImpl remotePerson = new RemotePersonImpl(person);
            UnicastRemoteObject.exportObject(remotePerson);
            clients.put(person.getPassportId(), remotePerson);
        }
        return true;
    }

    @Override
    public LocalPerson getVerifyOrCreateLocalPerson(LocalPerson person) throws RemoteException {
        createPerson(person);
        return getVerifyLocalPerson(person);
    }

    @Override
    public RemotePerson getVerifyOrCreateRemotePerson(LocalPerson person) throws RemoteException {
        createPerson(person);
        return getVerifyRemotePerson(person);
    }

    @Override
    public LocalPerson getOrCreateLocalPerson(LocalPerson person) throws RemoteException {
        createPerson(person);
        return getLocalPerson(person.getPassportId());
    }

    @Override
    public RemotePerson getOrCreateRemotePerson(LocalPerson person) throws RemoteException {
        createPerson(person);
        return getRemotePerson(person.getPassportId());
    }
}
