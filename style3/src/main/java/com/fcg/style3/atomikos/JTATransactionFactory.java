package com.fcg.style3.atomikos;

import java.util.Properties;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.hibernate.ConnectionReleaseMode;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.TransactionException;
import org.hibernate.jdbc.JDBCContext;
import org.hibernate.transaction.TransactionFactory;
import org.hibernate.util.JTAHelper;

import com.atomikos.icatch.jta.UserTransactionImp;

/**
 * Factory for <tt>JTATransaction</tt>.
 *
 * Reason for implementing this class and JTATransaction class is to fix the bug
 * http://opensource.atlassian.com/projects/hibernate/browse/HHH-3110
 *
 * @see JTATransaction
 */
public class JTATransactionFactory implements TransactionFactory {

    public void configure(Properties props) throws HibernateException {

    }

    public Transaction createTransaction(JDBCContext jdbcContext,
            Context transactionContext) throws HibernateException {
        return new JTATransaction(jdbcContext, transactionContext);
    }

    public ConnectionReleaseMode getDefaultReleaseMode() {
        return ConnectionReleaseMode.AFTER_STATEMENT;
    }

    public boolean isTransactionManagerRequired() {
        return false;
    }

    public boolean areCallbacksLocalToHibernateTransactions() {
        return false;
    }

    public boolean isTransactionInProgress(JDBCContext jdbcContext,
            Context transactionContext, Transaction transaction) {
        try {
            // Essentially:
            // 1) If we have a local (Hibernate) transaction in progress
            // and it already has the UserTransaction cached, use that
            // UserTransaction to determine the status.
            // 2) If a transaction manager has been located, use
            // that transaction manager to determine the status.
            // 3) Finally, as the last resort, try to lookup the
            // UserTransaction via JNDI and use that to determine the
            // status.
            if (transaction != null) {
                UserTransaction ut = ((JTATransaction) transaction)
                        .getUserTransaction();
                if (ut != null) {
                    return JTAHelper.isInProgress(ut.getStatus());
                }
            }

            if (jdbcContext.getFactory().getTransactionManager() != null) {
                return JTAHelper.isInProgress(jdbcContext.getFactory()
                        .getTransactionManager().getStatus());
            } else {
                UserTransaction ut = new UserTransactionImp();
                return ut != null && JTAHelper.isInProgress(ut.getStatus());
            }
        } catch (SystemException se) {
            throw new TransactionException(
                    "Unable to check transaction status", se);
        }
    }

}
