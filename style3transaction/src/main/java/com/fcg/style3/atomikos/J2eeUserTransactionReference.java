package com.fcg.style3.atomikos;

import com.atomikos.icatch.jta.J2eeUserTransaction;

public class J2eeUserTransactionReference {
    private static J2eeUserTransaction userTransaction;

    public void setUserTransaction(J2eeUserTransaction userTransaction) {
        J2eeUserTransactionReference.userTransaction = userTransaction;
    }

    public static J2eeUserTransaction getUserTransaction() {
        return userTransaction;
    }
}
