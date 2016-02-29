package com.huotu.tools.taobao;

import com.huotu.tools.taobao.udp.MulticastExchange;
import com.huotu.tools.taobao.udp.WorkMode;
import org.junit.Test;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * @author CJ
 */
public class MulticastTest {

    @Test
    public void startServer() throws IOException {
        InetSocketAddress address = new InetSocketAddress(MulticastWork.DefaultMulticastAddress, MulticastWork.DefaultMulticastPort);
        try (DatagramSocket socket = MulticastWork.toMulticastSocket(MulticastWork.DefaultMulticastPort, null, WorkMode.server)) {
            MulticastExchange exchange = new MulticastExchange(socket, address);

            exchange.start();
        }
    }

    @Test
    public void sendPacket() throws IOException {
        InetSocketAddress address = new InetSocketAddress(MulticastWork.DefaultMulticastAddress, MulticastWork.DefaultMulticastPort);
//        System.setProperty("java.net.preferIPv4Stack","true");
        try (DatagramSocket socket = MulticastWork.toMulticastSocket(MulticastWork.DefaultMulticastPort, null, WorkMode.client)) {

            MulticastExchange exchange = new MulticastExchange(socket, address);

            exchange.start();
        }
    }
}
