package ru.georgeee.itmo.java.sem4.task9;

import ru.georgeee.itmo.java.sem4.task9.bases.LocalPersonImpl;
import ru.georgeee.itmo.java.sem4.task9.interfaces.Bank;
import ru.georgeee.itmo.java.sem4.task9.interfaces.RemotePerson;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by georgeee on 14.05.14.
 */
public class Client {
    Bank bank;

    public Client(Bank bank) {
        this.bank = bank;
    }

    public static void main(String[] args) {
        Transaction transaction;
        try {
            transaction = Transaction.parseFromArgs(args);
        } catch (Exception ex) {
            System.err.println("Error parsing args");
            return;
        }
        try {
            Bank bank;
            try {
                bank = (Bank) Naming.lookup(Server.BANK_URL);
            } catch (NotBoundException e) {
                System.err.println("Bank is not bound");
                return;
            } catch (MalformedURLException e) {
                System.err.println("Malformed url " + Server.BANK_URL);
                return;
            }
            try {
                new Client(bank).runTransaction(transaction);
            } catch (TransactionException e) {
                System.err.println(e.getMessage());
            }
        } catch (RemoteException e) {
            System.err.println("Remote exception occurred: " + e.getMessage());
            return;
        }
    }

    private void runTransaction(Transaction transaction) throws RemoteException, TransactionException {
        RemotePerson person = bank.getVerifyOrCreateRemotePerson(new LocalPersonImpl(transaction.passportId, transaction.name, transaction.surname));
        if (person == null) {
            throw new TransactionException("Authentication error occurred");
        }
        int newBalance = person.changeAmount(transaction.accountId, transaction.difference);
        System.out.println(transaction + " result: new balance " + newBalance);
    }

    public static class TransactionException extends Exception {
        public TransactionException(String message) {
            super(message);
        }

        public TransactionException() {
        }
    }

    public static class Transaction {
        public final String name, surname;
        public final long passportId;
        public final int accountId, difference;

        public Transaction(String name, String surname, long passportId, int accountId, int difference) {
            this.name = name;
            this.surname = surname;
            this.passportId = passportId;
            this.accountId = accountId;
            this.difference = difference;
        }

        public static Transaction parseFromArgs(String[] args) {
            String name = args[0];
            String surname = args[1];
            long passportId = Long.parseLong(args[2]);
            int accountId = Integer.parseInt(args[3]);
            int difference = Integer.parseInt(args[4]);
            return new Transaction(name, surname, passportId, accountId, difference);
        }

        @Override
        public String toString() {
            return "Transaction{" +
                    "name='" + name + '\'' +
                    ", surname='" + surname + '\'' +
                    ", passportId=" + passportId +
                    ", accountId=" + accountId +
                    ", difference=" + difference +
                    '}';
        }
    }
}
