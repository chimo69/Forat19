package proyecto.golfus.forat19.utils;

/**
 * Clase encargada de devolver un tipo de error
 * @author Antonio Rodriguez Sirgado
 */
public class Reply {
    Boolean timeOut;
    int error;

    public int getTypeError() {
        return error;
    }

    public Reply(Boolean timeOut, int error) {
        this.timeOut = timeOut;
        this.error = error;
    }

}
