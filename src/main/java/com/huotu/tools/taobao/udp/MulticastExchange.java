package com.huotu.tools.taobao.udp;

import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.Random;

/**
 * @author CJ
 */
public class MulticastExchange {

    private final DatagramSocket socket;
    private final InetSocketAddress address;

    public MulticastExchange(DatagramSocket socket, InetSocketAddress address) {
        this.socket = socket;
        this.address = address;
    }

    public void start() throws IOException {
        byte[] buffer = new byte[32];
        new Random().nextBytes(buffer);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        if (socket instanceof MulticastSocket) {
            ((MulticastSocket) socket).joinGroup(address.getAddress());
            System.out.println("Joined " + address);
        }
        try {

            if (socket instanceof MulticastSocket) {
                System.out.println("waiting for message");
                socket.receive(packet);
                System.out.println(Hex.encodeHexString(packet.getData()));
                System.out.println("Received from " + packet.getSocketAddress());
            } else {
                packet.setSocketAddress(address);
//                socket.connect(address,22935);
                socket.send(packet);
                System.out.println(Hex.encodeHexString(buffer));
                System.out.println("Sent to " + address);
            }

        } finally {
            if (socket instanceof MulticastSocket) {
                ((MulticastSocket) socket).leaveGroup(address.getAddress());
            }
        }

//        socket.leaveGroup(socket.getInetAddress());
    }
}
