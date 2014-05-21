package ru.georgeee.itmo.java.sem4.task9.bases;

import ru.georgeee.itmo.java.sem4.task9.interfaces.Bank;
import ru.georgeee.itmo.java.sem4.task9.interfaces.LocalPerson;
import ru.georgeee.itmo.java.sem4.task9.interfaces.RemotePerson;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by georgeee on 08.05.14.
 */
public class BankImpl implements Bank {
    private final ConcurrentMap<Long, RemotePerson> clients;

    public BankImpl() {
        clients = new ConcurrentHashMap<Long, RemotePerson>();
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
        RemotePersonImpl remotePerson = new RemotePersonImpl(person);
        RemotePerson prevPerson = clients.putIfAbsent(person.getPassportId(), remotePerson);
        if (prevPerson == null) {
            UnicastRemoteObject.exportObject(remotePerson);
        }
        return prevPerson == null;
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
