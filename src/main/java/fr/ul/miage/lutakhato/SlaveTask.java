package fr.ul.miage.lutakhato;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.TimerTask;


public class SlaveTask extends TimerTask {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    public SlaveTask(String host , int port) throws Exception {

        socket = new Socket(host , port);

        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {

        while(Server.slave) {

            try {
                //get command from DataInputStream object !

            }
            catch(Exception ignored) {

            }
        }

    }
}
