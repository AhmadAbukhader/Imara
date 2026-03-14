package com.Imara.imara.repository.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;

final class RepositoryHelper {

    private RepositoryHelper() {
    }

    static Instant toInstant(Timestamp timestamp) {
        return timestamp != null ? timestamp.toInstant() : null;
    }

    static LocalDate toLocalDate(java.sql.Date date) {
        return date != null ? date.toLocalDate() : null;
    }
}
