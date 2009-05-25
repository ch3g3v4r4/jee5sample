package org.freejava.proxy.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;


public class Main {

    public static void main(String[] args) throws IOException {
        // Create the acceptor
        SocketAcceptor acceptor = new NioSocketAcceptor();

        // Add two filters : a logger and a codec
        acceptor.getFilterChain().addLast( "logger", new LoggingFilter() );
        acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "UTF-8" ))));

        // Attach the business logic to the server
        acceptor.setHandler(new EchoProtocolHandler());

        // Configurate the buffer size and the iddle time
        acceptor.getSessionConfig().setReadBufferSize( 2048 );
        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 10 );

        acceptor.setDefaultLocalAddress(new InetSocketAddress(8080));

        acceptor.bind();

    }

}
