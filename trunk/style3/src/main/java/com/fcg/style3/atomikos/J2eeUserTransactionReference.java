package com.fcg.style3.atomikos;

import javax.transaction.UserTransaction;

public class J2eeUserTransactionReference {
    private static UserTransaction userTransaction;

    public void setUserTransaction(UserTransaction userTransaction) {
        J2eeUserTransactionReference.userTransaction = userTransaction;
    }

    public static UserTransaction getUserTransaction() {
        return userTransaction;
    }
}
