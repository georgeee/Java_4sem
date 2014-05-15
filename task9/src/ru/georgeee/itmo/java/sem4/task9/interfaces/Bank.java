package ru.georgeee.itmo.java.sem4.task9.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by georgeee on 08.05.14.
 */
public interface Bank extends Remote{
    public LocalPerson getVerifyLocalPerson(LocalPerson person) throws RemoteException;
    public RemotePerson getVerifyRemotePerson(LocalPerson person) throws RemoteException;
    public LocalPerson getLocalPerson(long passportId) throws RemoteException;
    public RemotePerson getRemotePerson(long passportId) throws RemoteException;
    public boolean createPerson(LocalPerson person) throws RemoteException;
    public LocalPerson getVerifyOrCreateLocalPerson(LocalPerson person) throws RemoteException;
    public RemotePerson getVerifyOrCreateRemotePerson(LocalPerson person) throws RemoteException;
    public LocalPerson getOrCreateLocalPerson(LocalPerson person) throws RemoteException;
    public RemotePerson getOrCreateRemotePerson(LocalPerson person) throws RemoteException;
}
