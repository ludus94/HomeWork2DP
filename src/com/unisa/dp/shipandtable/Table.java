package com.unisa.dp.shipandtable;

public class Table {
    private final int dimy;
    private final int dimx;
    private String[][] table;

    public Table() {
        this.table = new String[10][11];
        this.dimy = 11;
        this.dimx = 10;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 11; j++) {
                table[i][j] = "-";
            }
        }
    }

    /***
     * Define the insert in a table, the ship of client.
     *
     * @param  s Insert a Ship
     * @return String "retry x" if the position are occupied on dimension x
     *         String "ok" if the ship are insert in the position
     *         String "retry y" if the position are occupied on dimension y
     *
     */

    public String insertShip(Ship s) {
        boolean insert_x = false;
        boolean insert_y = false;

        for (int i = s.getXStart(); i <= s.getXEnd(); i++) {
            if (!this.table[s.getYStart()][i].equals("-")) {
                insert_x = true;
                break;
            }
        }
        if (insert_x) {
            return "retry x\n";
        }
        for (int j = s.getYStart(); j <= s.getYEnd(); j++) {
            if (!this.table[j][s.getXStart()].equals("-")) {
                insert_y = true;
                break;
            }
        }
        if (insert_y) {
            return "retry y\n";
        }

        if (s.getYEnd() - s.getYStart() - s.getXStart() + s.getXEnd() != s.getDim() - 1)
            return "retry x\n";

        for (int i = s.getXStart(); i <= s.getXEnd(); i++) {
            for (int j = s.getYStart(); j <= s.getYEnd(); j++) {
                this.table[j][i] = s.getType();
            }
        }
        return "";

    }

    public void attack(Integer x, Integer y) {
        if (table[x][y].contains("A") || table[x][y].contains("B") || table[x][y].contains("D") || table[x][y].contains("P") || table[x][y].contains("S")) {
            table[x][y] = "X";
        } else {
            table[x][y] = "0";
        }
    }

    public Boolean gameOver() {
        int count = 0;
        for (int i = 0; i < dimx; i++) {
            for (int j = 0; j < dimy; j++) {
                if (table[i][j].contains("X"))
                    count = count + 1;
            }
        }
        if (count == 17) {
            return true;
        }
        return false;
    }

    /***
     *
     * @return a formated string to player
     */
    public String PlayerPrint() {
        String s_retr = new String();
        s_retr = s_retr + " a b c d e f g h i j k\n";
        for (int i = 0; i < dimx; i++) {
            s_retr = s_retr + (i);
            for (int j = 0; j < dimy; j++) {
                s_retr = s_retr + table[i][j] + " ";
            }
            s_retr = s_retr + "\n";
        }
        return s_retr;
    }

    /***
     *
     * @return a string used during a session of game
     */
    public String ServerPrint() {
        String s_retr = new String();
        s_retr = s_retr + " a b c d e f g h i j k\n";
        for (int i = 0; i < dimx; i++) {
            s_retr = s_retr + i;
            for (int j = 0; j < dimy; j++) {
                if (table[i][j].contains("A") || table[i][j].contains("B") || table[i][j].contains("D") || table[i][j].contains("P") || table[i][j].contains("S"))
                    s_retr = s_retr + "-" + " ";
                else if(table[i][j].contains("X") || table[i][j].contains("-") || table[i][j].contains("0"))
                    s_retr = s_retr + table[i][j] + " ";
            }
            s_retr = s_retr + "\n";
        }
        return s_retr;
    }
}
