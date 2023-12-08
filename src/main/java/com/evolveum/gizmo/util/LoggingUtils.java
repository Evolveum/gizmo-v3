/*
 * Copyright 2015 Viliam Repan (lazyman)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evolveum.gizmo.util;

import org.apache.commons.lang3.Validate;
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
