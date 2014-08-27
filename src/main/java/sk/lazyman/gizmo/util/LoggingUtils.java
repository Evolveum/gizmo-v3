package sk.lazyman.gizmo.util;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lazyman
 */
public class LoggingUtils {

    public static void logException(Logger LOGGER, String message, Throwable ex, Object... objects) {
        Validate.notNull(LOGGER, "Logger can't be null.");
        Validate.notNull(ex, "Exception can't be null.");

        List<Object> args = new ArrayList<Object>();
        args.addAll(Arrays.asList(objects));
        args.add(ex.getMessage());

        LOGGER.error(message + ", reason: {}", args.toArray());
        // Add exception to the list. It will be the last argument without {} in the message,
        // therefore the stack trace will get logged
        args.add(ex);
        LOGGER.debug(message + ".", args.toArray());
    }
}
