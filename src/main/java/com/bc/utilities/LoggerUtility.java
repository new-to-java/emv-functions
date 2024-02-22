package com.bc.utilities;

import org.slf4j.Logger;
/**
 * This interface defines a standard set of default methods that will be used for logging informational, warning and debug messages.
 */
public interface LoggerUtility {

    /**
     * Method for logging debug messages, when the debug log level is enabled.
     */
    default void logDebug(Logger log,
                          String message,
                          Object... objectsToLog){
        log.debug(getUnqualifiedClassName() + " log --> " +
                message,
                objectsToLog
        );
    }
    /**
     * Method for logging warning messages.
     */
    default void logWarning(Logger log,
                            String message,
                            Object... objectsToLog){
        log.debug(getUnqualifiedClassName() + " log --> " +
                message,
                objectsToLog
        );
    }
    /**
     * Method for logging informational messages.
     */
    default void logInfo(Logger log,
                         String message,
                         Object... objectsToLog){
        log.info(getUnqualifiedClassName() + " log --> " +
                message,
                objectsToLog
        );
    }
    /**
     * Method to get unqualified class name, i.e., class name without the package prefix.
     * @return Unqualified class name.
     */
    private String getUnqualifiedClassName(){
        return getClass().getSimpleName();
    }
}