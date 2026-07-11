package com.ucc.oegs.util;

import java.util.UUID;

/**
 * Central source of unique identifiers for domain entities.
 *
 * <p>Prefixing the id with the entity kind (e.g. {@code EXM-}) keeps stored
 * data readable when inspecting the serialized files during development.</p>
 */
public final class IdGenerator {

    private IdGenerator() {
        // utility class — no instances
    }

    public static String userId() {
        return "USR-" + shortUuid();
    }

    public static String examId() {
        return "EXM-" + shortUuid();
    }

    public static String questionId() {
        return "QST-" + shortUuid();
    }

    public static String submissionId() {
        return "SUB-" + shortUuid();
    }

    private static String shortUuid() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
