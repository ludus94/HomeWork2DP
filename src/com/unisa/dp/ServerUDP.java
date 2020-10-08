package com.unisa.dp;

import com.unisa.dp.shipandtable.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class ServerUDP {
    private static Map<String, Table> mapgame = new HashMap<>();
    private static Map<String, String> match = new HashMap<>();
    private static Map<String, DatagramPacket> userconnect = new HashMap<>();
    private static ArrayList<String> wait_game = new ArrayList<>();
    private static Map<String, Integer> colcon = new HashMap<>();
    private static ArrayList<String> inserttable = new ArrayList<>();
    private static Logger log = Logger.getLogger("global");

    private static void placeShip(Ship a, String user, ByteArrayOutputStream msg_send, DatagramPacket packet_server_send, DatagramSocket socket_server, InetAddress addr) throws IOException {
        Table t = mapgame.get(user);
        String insert = t.insertShip(a);
        if (insert.equals("")) {
            log.info("L' utente " + user + " ha inserito un " + a.getName() + " nella sua tavola");
            int port_send = userconnect.get(user).getPort();
            msg_send = new ByteArrayOutputStream();
            msg_send.writeBytes(t.PlayerPrint().getBytes());
            packet_server_send = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, port_send);
            socket_server.send(packet_server_send);
            log.info("Invio la stampa della tavola");
        } else {
            msg_send = new ByteArrayOutputStream();
            msg_send.writeBytes(insert.getBytes());
            int port_send = userconnect.get(user).getPort();
            packet_server_send = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, port_send);
            socket_server.send(packet_server_send);
        }
    }


    public static void main(String[] args) throws IOException {
        colcon.put("a", 0);
        colcon.put("b", 1);
        colcon.put("c", 2);
        colcon.put("d", 3);
        colcon.put("e", 4);
        colcon.put("f", 5);
        colcon.put("g", 6);
        colcon.put("h", 7);
        colcon.put("i", 8);
        colcon.put("j", 9);
        colcon.put("k", 10);
        int PORT = 7777, buffer_dim = 65507;
        InetAddress addr = InetAddress.getByName("localhost");
        DatagramSocket socket_server = new DatagramSocket(PORT);
        byte[] buff_rec;
        DatagramPacket packet_server_recive, packet_server_send = null;
        ByteArrayOutputStream msg_send;

        while (true) {
            buff_rec = new byte[buffer_dim];
            packet_server_recive = new DatagramPacket(buff_rec, buffer_dim);
            log.info("Sono in attesa di ricevere pacchetti");
            socket_server.receive(packet_server_recive);
            msg_send = new ByteArrayOutputStream();
            String recived = new String(buff_rec, "UTF-8");
            recived = recived.replace("\n", " ");
            int index = recived.indexOf(" ");
            String chose = recived.substring(0, index);
            recived = recived.substring(index + 1);
            if (chose.equals("username")) {
                int index_u = recived.indexOf(" ");
                String username = recived.trim();
                if (mapgame.containsKey(username) && userconnect.containsKey(username)) {
                    log.info("Il nome utente " + username + " è gia connesso");
                    String resp = "retry";
                    msg_send = new ByteArrayOutputStream();
                    msg_send.writeBytes(resp.getBytes());
                    int port_send = packet_server_recive.getPort();
                    packet_server_send = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, port_send);
                    socket_server.send(packet_server_send);
                } else if (!wait_game.contains(username)) {
                    Table table = new Table();
                    mapgame.put(username, table);
                    wait_game.add(username);
                    userconnect.put(username, packet_server_recive);
                    String resp = "user_ok\n";
                    msg_send = new ByteArrayOutputStream();
                    msg_send.writeBytes(resp.getBytes());
                    int port_send = packet_server_recive.getPort();
                    packet_server_send = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, port_send);
                    socket_server.send(packet_server_send);
                    log.info("Connessione avvenuta con successo. L' utente " + username + " è stato aggiunto in wait");
                }
            } else if (chose.equals("send_table")) {
                int index_u = recived.indexOf(" ");
                String user = recived.substring(0, index_u);
                recived = recived.substring(index_u + 1);
                int index_t = recived.indexOf(" ");
                String type = recived.substring(0, index_t);
                recived = recived.substring(index_t + 1);
                int index_x_s = recived.indexOf(" ");
                Integer x_s = colcon.get(recived.substring(0, index_x_s));
                recived = recived.substring(index_x_s + 1);
                int index_x_e = recived.indexOf(" ");
                Integer x_e = colcon.get(recived.substring(0, index_x_e));
                recived = recived.substring(index_x_e + 1);
                int index_y_s = recived.indexOf(" ");
                Integer y_s = Integer.parseInt(recived.substring(0, index_y_s));
                recived = recived.substring(index_y_s + 1);
                int index_y_e = recived.indexOf(" ");
                Integer y_e = Integer.parseInt(recived.substring(0, index_y_e).trim());
                if (type.equals("A")) {
                    AirCraftCurrier a = new AirCraftCurrier(x_s, x_e, y_s, y_e);
                    placeShip(a, user, msg_send, packet_server_send, socket_server, addr);
                } else if (type.equals("B")) {
                    Battleship a = new Battleship(x_s, x_e, y_s, y_e);
                    placeShip(a, user, msg_send, packet_server_send, socket_server, addr);
                } else if (type.equals("D")) {
                    Destroyer a = new Destroyer(x_s, x_e, y_s, y_e);
                    placeShip(a, user, msg_send, packet_server_send, socket_server, addr);
                } else if (type.equals("P")) {
                    PatrolBoat a = new PatrolBoat(x_s, x_e, y_s, y_e);
                    placeShip(a, user, msg_send, packet_server_send, socket_server, addr);
                } else if (type.equals("S")) {
                    Submarine a = new Submarine(x_s, x_e, y_s, y_e);
                    placeShip(a, user, msg_send, packet_server_send, socket_server, addr);
                }

            } else if (chose.equals("complete_table")) {
                int index_user = recived.indexOf(" ");
                String user = recived.substring(0, index_user);
                inserttable.add(user);

                if (inserttable.contains(match.get(user))) {
                    msg_send = new ByteArrayOutputStream();
                    msg_send.writeBytes("attack\n".getBytes());
                    packet_server_send = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, userconnect.get(user).getPort());
                    socket_server.send(packet_server_send);
                    packet_server_send = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, userconnect.get(match.get(user)).getPort());
                    socket_server.send(packet_server_send);
                } else {
                    msg_send = new ByteArrayOutputStream();
                    msg_send.writeBytes("wait_game\n".getBytes());
                    packet_server_send = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, userconnect.get(user).getPort());
                    socket_server.send(packet_server_send);
                }


                //packet_server_send.setPort(userconnect.get(match.get(user)).getPort());
                //socket_server.send(packet_server_send);

            } else if (chose.equals("attack")) {
                int index_u = recived.indexOf(" ");
                String user = recived.substring(0, index_u);
                recived = recived.substring(index_u + 1);
                String adv = match.get(user);
                int index_r = recived.indexOf(" ");
                Integer row = Integer.parseInt(recived.substring(0, index_r).trim());
                recived = recived.substring(index_r + 1);
                int index_c = recived.indexOf(" ");
                Integer col = colcon.get(recived.substring(0, index_c));
                Table t1 = mapgame.get(adv);
                Table t2 = mapgame.get(user);
                if (t1.gameOver()) {
                    match.remove(user);
                    match.remove(adv);
                    mapgame.remove(user);
                    mapgame.remove(adv);
                    DatagramPacket packet_user = userconnect.remove(user);
                    DatagramPacket packet_adv = userconnect.remove(adv);
                    int send_port_user = packet_user.getPort();
                    int send_port_adv = packet_adv.getPort();
                    msg_send = new ByteArrayOutputStream();
                    msg_send.writeBytes("winner".getBytes());
                    packet_server_send = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, send_port_user);
                    socket_server.send(packet_server_send);
                    msg_send = new ByteArrayOutputStream();
                    msg_send.writeBytes("gameover".getBytes());
                    packet_server_send = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, send_port_adv);
                    socket_server.send(packet_server_send);
                    log.info("L' utente " + user + " ha vinto contro il suo avversario " + adv);

                } else if (t2.gameOver()) {
                    match.remove(user);
                    match.remove(adv);
                    mapgame.remove(user);
                    mapgame.remove(adv);
                    DatagramPacket packet_user = userconnect.remove(user);
                    DatagramPacket packet_adv = userconnect.remove(adv);
                    int send_port_user = packet_user.getPort();
                    int send_port_adv = packet_adv.getPort();
                    msg_send = new ByteArrayOutputStream();
                    msg_send.writeBytes("winner".getBytes());
                    packet_server_send = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, send_port_adv);
                    socket_server.send(packet_server_send);
                    msg_send = new ByteArrayOutputStream();
                    msg_send.writeBytes("gameover".getBytes());
                    packet_server_send = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, send_port_user);
                    socket_server.send(packet_server_send);
                    log.info("L' utente " + user + " ha perso il suo avversario" + adv);
                } else if (inserttable.contains(adv) && inserttable.contains(user)) {
                    int send_port = userconnect.get(user).getPort();
                    int send_port_adw=userconnect.get(adv).getPort();
                    msg_send=new ByteArrayOutputStream();
                    msg_send.writeBytes(t1.ServerPrint().getBytes());
                    packet_server_send = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, send_port);
                    socket_server.send(packet_server_send);
                    msg_send=new ByteArrayOutputStream();
                    msg_send.writeBytes("attack\n".getBytes());
                    msg_send.writeBytes(t2.ServerPrint().getBytes());
                    packet_server_send = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, send_port_adw);
                    socket_server.send(packet_server_send);
                    t1.attack(row, col);

                    log.info("L' utente " + user + " attacca il suo avversario" + adv);
                }
            }

            if (wait_game.size() % 2 == 0 && wait_game.size() > 0) {
                msg_send = new ByteArrayOutputStream();
                String gamer1 = wait_game.remove(0);
                String gamer2 = wait_game.remove(0);
                log.info("Gli utenti " + gamer1 + " e " + gamer2 + " si sono seduti al tavolo");
                match.put(gamer1, gamer2);
                match.put(gamer2, gamer1);
                DatagramPacket packet_gamer1 = userconnect.get(gamer1);
                DatagramPacket packet_gamer2 = userconnect.get(gamer2);
                msg_send.writeBytes("START\n".getBytes());
                int port_send_gamer1 = packet_gamer1.getPort();
                int port_send_gamer2 = packet_gamer2.getPort();
                packet_server_send = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, port_send_gamer1);
                socket_server.send(packet_server_send);
                packet_server_send = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, port_send_gamer2);
                socket_server.send(packet_server_send);
                msg_send = new ByteArrayOutputStream();
                msg_send.writeBytes("insert_table".getBytes());
                packet_server_send = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, port_send_gamer1);
                socket_server.send(packet_server_send);
                packet_server_send = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, port_send_gamer2);
                socket_server.send(packet_server_send);

            }
        }
    }
}