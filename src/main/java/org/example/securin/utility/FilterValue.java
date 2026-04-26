package org.example.securin.utility;

public record FilterValue(
        FilterOperator operator,
        String value
) {
}
