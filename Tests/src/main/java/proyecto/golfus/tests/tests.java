package proyecto.golfus.tests;

/**
 * @Author Antonio Rodríguez Sirgado
 */
import proyecto.golfus.forat19.*;
public class tests {
    String sendMessage = "chimo" + "¬" + "1234";
    String device = "CHIMO Galaxy S20 Ultra 5G";


    Message mMessage = new Message(null + "¬" + device, Global.LOGIN, sendMessage, null);

    RequestServer request = new RequestServer();
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            request.request(mMessage);
        }
    });
        thread.start();
        thread.join();

    Message respuesta = request.retrieveData();

}
