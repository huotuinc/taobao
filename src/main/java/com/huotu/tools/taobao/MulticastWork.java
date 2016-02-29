package com.huotu.tools.taobao;

import com.huotu.tools.taobao.udp.MulticastExchange;
import com.huotu.tools.taobao.udp.WorkMode;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

/**
 * 多播测试
 * 默认多播地址228.9.59.89
 * 默认多播端口22934
 * <p>
 * 分别支持接受者（服务端）和发送者（客户端）模式
 * <p>
 * 可以强制使用本地地址
 *
 * @author CJ
 */
public class MulticastWork {

    public static final String DefaultMulticastAddress = "228.9.59.90";
    public static final int DefaultMulticastPort = 22935;

    public static DatagramSocket toMulticastSocket(int port, String bindAddress, WorkMode mode) throws IOException {
        DatagramSocket socket;
        if (bindAddress != null) {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(bindAddress, port);
            System.out.println("on " + inetSocketAddress);
            if (mode == WorkMode.server)
                socket = new MulticastSocket(inetSocketAddress);
            else
                socket = new DatagramSocket();
        } else {
            System.out.println("on any local address:" + port);
            if (mode == WorkMode.server)
                socket = new MulticastSocket(port);
            else
                socket = new DatagramSocket();
        }

//        socket.joinGroup(InetAddress.getByName(address));
        return socket;
    }

    /**
     * -s 服务端模式
     * -c 客户端模式
     * --multicastAddress
     * --multicastPort
     * --bindAddress
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        String multicastAddress = DefaultMulticastAddress;
        int multicastPort = DefaultMulticastPort;
        String bindAddress = null;
        boolean multicastAddressOn = false;
        boolean multicastPortOn = false;
        boolean bindAddressOn = false;
        WorkMode mode = null;
        for (String str : args) {
            if (multicastAddressOn) {
                multicastAddress = str;
                multicastAddressOn = false;
            } else if (multicastPortOn) {
                multicastPort = Integer.parseInt(str);
                multicastPortOn = false;
            } else if (bindAddressOn) {
                bindAddress = str;
                bindAddressOn = false;
            } else if ("-s".equalsIgnoreCase(str)) {
                mode = WorkMode.server;
            } else if ("-c".equals(str)) {
                mode = WorkMode.client;
            } else if ("--multicastAddress".equalsIgnoreCase(str)) {
                multicastAddressOn = true;
            } else if ("--multicastPort".equalsIgnoreCase(str)) {
                multicastPortOn = true;
            } else if ("--bindAddress".equalsIgnoreCase(str)) {
                bindAddressOn = true;
            }
        }

        if (mode == null)
            throw new IllegalStateException("-s? -c?");

        InetSocketAddress address = new InetSocketAddress(multicastAddress, multicastPort);

        try (DatagramSocket socket = toMulticastSocket(multicastPort, bindAddress, mode)) {
            MulticastExchange exchange = new MulticastExchange(socket, address);

            exchange.start();
        }


    }
}
