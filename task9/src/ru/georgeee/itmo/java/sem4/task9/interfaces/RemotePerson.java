package ru.georgeee.itmo.java.sem4.task9.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by georgeee on 08.05.14.
 */
public interface RemotePerson extends Remote {
    public long getPassportId() throws RemoteException;

    public String getName() throws RemoteException;

    public String getSurname() throws RemoteException;

    public int getAmount(int accountId) throws RemoteException;

    public int changeAmount(int accountId, int difference) throws RemoteException;

    public LocalPerson createLocal() throws RemoteException;

    public boolean verify(LocalPerson person) throws RemoteException;
}
