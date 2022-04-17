package me.willkroboth.ConfigCommands.HelperClasses;

import java.util.logging.Level;
import java.util.logging.Logger;

public class IndentedLogger {
    private final Logger logger;
    private int indentation = 0;

    public IndentedLogger(Logger l){ logger = l; }

    public void setIndentation(int i){ indentation = i; }

    public int getIndentation(){ return indentation; }

    public void increaseIndentation(){ indentation++; }

    public void decreaseIndentation(){ indentation--; }

    public void info(String message){ logger.info("\t".repeat(indentation) + message); }

    public void warn(boolean red, String message){
        logger.log(red ? Level.SEVERE:Level.WARNING, "\t".repeat(indentation) + message);
    }

    public Logger getLogger() { return logger; }
}
