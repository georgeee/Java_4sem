package ru.georgeee.itmo.java.sem4.task9;

import ru.georgeee.itmo.java.sem4.task9.bases.BankImpl;
import ru.georgeee.itmo.java.sem4.task9.interfaces.Bank;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by georgeee on 14.05.14.
 */
public class Server {
    public static final String BANK_URL = "rmi://localhost/bank";

    public static void main(String[] args) {
        Bank bank = new BankImpl();
        try {
            UnicastRemoteObject.exportObject(bank);
            Naming.rebind(BANK_URL, bank);
        } catch (RemoteException e) {
            System.err.println("Can't export Bank object");
        } catch (MalformedURLException e) {
            System.err.println("Malformed url " + BANK_URL);
        }
    }
}
