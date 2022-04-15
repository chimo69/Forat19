package proyecto.golfus.forat19.utils;

/**
 * Clase encargada de devolver un tipo de error
 * @author Antonio Rodriguez Sirgado
 */
public class Reply {
    Boolean timeOut;
    int typeError;

    public int getTypeError() {
        return typeError;
    }

    public void setTypeError(int typeError) {
        this.typeError = typeError;
    }

    public Boolean getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Boolean timeOut) {
        this.timeOut = timeOut;
    }

    public Reply(Boolean timeOut, int typeError) {
        this.timeOut = timeOut;
        this.typeError = typeError;
    }
}
