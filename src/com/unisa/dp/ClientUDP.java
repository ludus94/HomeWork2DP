package com.unisa.dp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ClientUDP {

    private static void sendTable(Scanner input, ByteArrayOutputStream msg_send, String username, String type, DatagramPacket packet_client, DatagramSocket socket_client, Map<String, Integer> colcon) throws IOException {
        int PORT = 7777, buffer_dim = 65507;
        InetAddress addr = InetAddress.getByName("localhost");
        String x_start, y_start, x_end, y_end;
        int y_start_int = -1, y_end_int = -1;
        //Metodo funzionante
        do {
            System.out.println("Insert the start letter of column:");
            x_start = input.next();
            if (!x_start.matches("[a-k]"))
                System.out.println("Retry the letter for column");
        } while (!x_start.matches("[a-k]"));

        do {
            System.out.println("Insert the end letter of column:");
            x_end = input.next();
            if (!x_end.matches("[a-k]"))
                System.out.println("Retry the letter for column");
        } while (!x_end.matches("[a-k]"));

        do {
            System.out.println("Insert the start coordinate of row:");
            y_start = input.next();
            if (y_start.matches("[0-9]")) {
                y_start_int = Integer.parseInt(y_start);
                if (y_start_int < 0 || y_start_int > 9)
                    System.out.println("Retry the coordinate of row");
            } else {
                System.out.println("Retry the coordinate of row");
            }
        } while (y_start_int < 0 || y_start_int > 9);

        do {
            System.out.println("Insert the end coordinate of row:");
            y_end = input.next();
            if (y_end.matches("[0-9]")) {
                y_end_int = Integer.parseInt(y_end);
                if (y_end_int < 0 || y_end_int > 9)
                    System.out.println("Retry the coordinate of row");
            } else {
                System.out.println("Retry the coordinate of row");
            }
        } while (y_end_int < 0 || y_end_int > 9);

        x_start = x_start + "\n";
        x_end = x_end + "\n";
        y_start = y_start + "\n";
        y_end = y_end + "\n";

        msg_send = new ByteArrayOutputStream();
        msg_send.writeBytes("send_table\n".getBytes());
        msg_send.writeBytes(username.getBytes());
        msg_send.writeBytes(type.getBytes());
        msg_send.writeBytes(x_start.getBytes());
        msg_send.writeBytes(x_end.getBytes());
        msg_send.writeBytes(y_start.getBytes());
        msg_send.writeBytes(y_end.getBytes());
        packet_client = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, PORT);
        socket_client.send(packet_client);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int PORT = 7777, buffer_dim = 65507;
        InetAddress addr = InetAddress.getByName("localhost");
        DatagramSocket socket_client = new DatagramSocket();
        Scanner input = new Scanner(System.in).useDelimiter("\n");
        ByteArrayOutputStream msg_send;
        byte[] buff_rec = new byte[buffer_dim];
        DatagramPacket packet_client;
        String resp_user, username;
        Boolean table_setting = false;
        Boolean gameover = false;
        Boolean[] ship = new Boolean[5];
        boolean attack_enable = false;
        for (int i = 0; i < 5; i++) {
            ship[i] = false;
        }
        Map<String, Integer> colcon = new HashMap<>();
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
        while (true) {
            msg_send = new ByteArrayOutputStream();
            System.out.println("Please enter a user name:");
            username = input.next() + "\n";
            msg_send.writeBytes("username\n".getBytes());
            msg_send.writeBytes(username.getBytes());
            packet_client = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, PORT);
            socket_client.send(packet_client);
            buff_rec = new byte[buffer_dim];
            packet_client = new DatagramPacket(buff_rec, buffer_dim);
            socket_client.receive(packet_client);
            resp_user = new String(buff_rec, "UTF-8").trim();
            resp_user = resp_user.replace("\n", " ");
            resp_user = resp_user.trim();
            if (resp_user.equals("user_ok")) {
                break;
            }
        }
        System.out.println("Username is: " + username);
        while (true) {
            buff_rec = new byte[buffer_dim];
            msg_send = new ByteArrayOutputStream();
            packet_client = new DatagramPacket(buff_rec, buffer_dim);
            socket_client.receive(packet_client);
            String resp_server = new String(buff_rec, "UTF-8").trim();
            System.out.println(resp_server);
            if (resp_server.equals("insert_table")) {
                System.out.println("Please enter the location of ships:");
                int i = 0;
                while (i <= 4) {
                    String type = null;
                    do {
                        System.out.println("---Insert type of ship---\n " +
                                "-A for Air Craft\n " +
                                "-B for Battleship\n " +
                                "-D for Destroy\n " +
                                "-P Patrol Boat\n  " +
                                "-S for Submarine\n");
                        type = input.next();
                    } while ((!type.equals("A") || ship[0] == true) && (!type.equals("B") || ship[1] == true) && (!type.equals("D") || ship[2] == true) && (!type.equals("P") || ship[3] == true) && (!type.equals("S") || ship[4] == true));
                    if (!ship[0] || !ship[1] || !ship[2] || !ship[3] || !ship[4]) {
                        type = type + "\n";
                        sendTable(input, msg_send, username, type, packet_client, socket_client, colcon);
                        buff_rec = new byte[buffer_dim];
                        packet_client = new DatagramPacket(buff_rec, buffer_dim);
                        socket_client.receive(packet_client);
                        resp_server = new String(buff_rec, "UTF-8").trim();
                        if (type.equals("A\n") && ship[0] == false && !(resp_server.contains("retry y") == true || resp_server.contains("retry x") == true)) {
                            ship[0] = true;
                            i++;
                        } else if (type.equals("B\n") && ship[1] == false && !(resp_server.contains("retry y") == true || resp_server.contains("retry x") == true)) {
                            ship[1] = true;
                            i++;
                        } else if (type.equals("D\n") && ship[2] == false && !(resp_server.contains("retry y") == true || resp_server.contains("retry x") == true)) {
                            ship[2] = true;
                            i++;
                        } else if (type.equals("P\n") && ship[3] == false && !(resp_server.contains("retry y") == true || resp_server.contains("retry x") == true)) {
                            ship[3] = true;
                            i++;
                        } else if (type.equals("S\n") && ship[4] == false && !(resp_server.contains("retry y") == true || resp_server.contains("retry x") == true)) {
                            ship[4] = true;
                            i++;
                        }
                        if (resp_server.contains("retry y") == false || resp_server.contains("retry x") == false)
                            System.out.println(resp_server);
                    }
                }
                table_setting = true;
                msg_send = new ByteArrayOutputStream();
                msg_send.writeBytes("complete_table\n".getBytes());
                msg_send.writeBytes(username.getBytes());
                packet_client = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, PORT);
                socket_client.send(packet_client);

            } else if (resp_server.equals("wait_game")) {
                do {
                    buff_rec = new byte[buffer_dim];
                    packet_client = new DatagramPacket(buff_rec, buffer_dim);
                    socket_client.receive(packet_client);
                    resp_server = new String(buff_rec, "UTF-8");
                    System.out.print(resp_server);
                } while (!resp_server.contains("attack"));
                attack_enable = true;
            } else if (resp_server.contains("attack") || attack_enable) {
                String columm, row;
                int row_int = -1;
                do {
                    System.out.println("---Attack---");
                    System.out.print("Insert column:");
                    columm = input.next();
                    if (!columm.matches("[a-k]"))
                        System.out.println("Retry the letter for column");
                } while (!columm.matches("[a-k]"));

                do {
                    System.out.print("Insert row:");
                    row = input.next();
                    if (row.matches("[0-9]")) {
                        row_int = Integer.parseInt(row);
                        if (row_int < 0 || row_int > 9)
                            System.out.println("Retry the coordinate of row");
                    } else {
                        System.out.println("Retry the coordinate of row");
                    }
                } while (row_int < 0 || row_int > 9);
                columm = columm + "\n";
                row = row + "\n";
                msg_send = new ByteArrayOutputStream();
                msg_send.writeBytes("attack\n".getBytes());
                msg_send.writeBytes(username.getBytes());
                msg_send.writeBytes(row.getBytes());
                msg_send.writeBytes(columm.getBytes());
                packet_client = new DatagramPacket(msg_send.toByteArray(), msg_send.size(), addr, PORT);
                socket_client.send(packet_client);
                buff_rec = new byte[buffer_dim];
                packet_client = new DatagramPacket(buff_rec, buffer_dim);
                socket_client.receive(packet_client);
                resp_server = new String(buff_rec, "UTF-8");
                System.out.print(resp_server);
                if (resp_server.contains("gameover")) {
                    System.out.println("Game Over, You Lose");
                    gameover = true;
                } else if (resp_server.contains("winner")) {
                    System.out.println("Game Over, You Win");
                    gameover = true;
                }
            }
        }
    }
}
