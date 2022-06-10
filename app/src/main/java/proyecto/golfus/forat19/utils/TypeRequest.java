package proyecto.golfus.forat19.utils;

import Forat19.Message;

/**
 * @Author Antonio Rodríguez Sirgado
 */
public class TypeRequest {
    Message message;
    String typeRequest;

    public TypeRequest(Message message, String typeRequest) {
        this.message = message;
        this.typeRequest = typeRequest;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getTypeRequest() {
        return typeRequest;
    }

    public void setTypeRequest(String typeRequest) {
        this.typeRequest = typeRequest;
    }
}
