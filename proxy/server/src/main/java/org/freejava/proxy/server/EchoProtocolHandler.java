package org.freejava.proxy.server;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.ssl.SslFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoProtocolHandler extends IoHandlerAdapter {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void sessionCreated(IoSession session) {
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);

        // We're going to use SSL negotiation notification.
        session.setAttribute(SslFilter.USE_NOTIFICATION);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        logger.info("CLOSED");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        logger.info("OPENED");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        logger.info("*** IDLE #" + session.getIdleCount(IdleStatus.BOTH_IDLE) + " ***");
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        session.close(true);
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
        logger.info( "Received : " + message );
        // Write the received data back to remote peer
        session.write((message));
    }
}