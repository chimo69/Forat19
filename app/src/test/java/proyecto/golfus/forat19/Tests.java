package proyecto.golfus.forat19;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import Forat19.Message;
import proyecto.golfus.forat19.utils.RequestServer;

/**
 * @Author Antonio Rodríguez Sirgado
 */
public class Tests {
    private final String IP = "54.216.204.8";
    private final int PORT = 5050;
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void loginUser() throws InterruptedException, IOException, ClassNotFoundException {
        String sendMessage = "chimo" + "¬" + "1234";
        String device = "CHIMO Galaxy S20 Ultra 5G";

        Message mMessage = new Message(null + "¬" + device, Global.LOGIN, sendMessage, null);

        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(IP, PORT), Global.TIME_OUT_LIMIT);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(mMessage);
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        Object input = in.readObject();
        if (input instanceof Message) {
            Message returnMessage = (Message) input;
            // Log.d("INFO", "Received message: " + returnMessage.getToken());


        }


        //assertEquals(respuesta.getParameters(), Global.OK);

    }


}