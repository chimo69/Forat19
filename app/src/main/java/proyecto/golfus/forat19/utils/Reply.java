package proyecto.golfus.forat19.utils;

import java.util.Observable;

public class Reply {
    String command;
    String parameters;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Reply(String command, String parameters) {
    this.command=command;
    this.parameters=parameters;

    }
}
