package org.example.securin.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.example.securin.utility.FilterOperator;
import org.example.securin.utility.FilterValue;
import org.example.securin.model.Recipe;
import org.springframework.data.jpa.domain.Specification;


// Expression -> An Expression represents a column, a calculation, or a function in your database. It is a "path" to a piece of data (root.get("title")). It is like a variable x in math that can represent different values depending on the row being evaluated.
// Predicate -> A Predicate is a logical statement that evaluates to either True or False. It is the result of comparing two Expressions. It is used to build the WHERE clause of a SQL query.
// Specification -> A Specification is a functional interface that represents a query specification. It is a function that takes a Root, a CriteriaQuery, and a CriteriaBuilder, and returns a Predicate. It is used to define reusable query conditions that can be combined using .and() / .or().


public class RecipeSpecification {

    public static Specification<Recipe> hasTitle(String title) {
        return (root, query, cb) -> title == null ? null :
                cb.like(root.get("title"), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Recipe> hasCuisine(String cuisine) {
        return (root, query, cb) -> cuisine == null ? null :
                cb.equal(root.get("cuisine"), cuisine);
    }

    public static Specification<Recipe> withNumericFilter(String fieldName, String filterInput) {
        return (root, query, cb) -> {
            FilterValue filterValue = FilterOperator.parse(filterInput);
            if(filterValue == null) return null;

            Expression<Number> path = root.get(fieldName);
            return buildNumericPredicate(cb, path, filterValue);
        };
    }

    public static Specification<Recipe> withCalories(String filterInput){
        return (root, query, cb) -> {
            FilterValue filterValue = FilterOperator.parse(filterInput);
            if (filterValue == null) return null;

            // extracts the text "398 kcal" from nutrition JSONB column
            // if nutrients column is null or doesn't contain "calories", jsonb_extract_path_text will return null, and then coalesce will turn that null into "0"
            Expression<String> rawCalories = cb.function(
                    "coalesce",
                    String.class,
                    cb.function(
                        "jsonb_extract_path_text",
                        String.class,
                        root.get("nutrients"), // the JSONB column
                        cb.literal("calories")
                    ),
                    cb.literal("0") // if the JSON path doesn't exist, return an "0" string instead of null
            );

            // find and replace all non-digit characters in the extracted text, leaving only "398"
            Expression<String> digitsOnly = cb.function(
                    "regexp_replace",
                    String.class,
                    rawCalories,
                    cb.literal("[^0-9]"), // find all non-digit characters
                    cb.literal(""), // replace them with an empty string
                    cb.literal("g") // global flag to replace all non-digit characters, not just the first one
            );

            // if calories was originally null or didn't contain digits, digitsOnly will be an empty string.
            // We want to treat that as zero for filtering purposes, so we use coalesce again to turn an empty string into "0".
            Expression<String> nonEmptyDigits = cb.function(
                    "coalesce",
                    String.class,
                    cb.function(
                            "nullif",
                            String.class,
                            digitsOnly,
                            cb.literal("") // if the result is an empty string, treat it as null
                    ),
                    cb.literal("0")
            );

            //  convert the resulting string of digits to a double expression
            // tells JPA to add "CAST(... AS INTEGER)" to the SQL query

            Expression<Double> caloriesPath = cb.function(
                    "to_number",
                    Double.class,
                    nonEmptyDigits,
                    cb.literal("99999999") // template must be at least as long as the longest possible number string, to ensure it can handle all values without truncation
            );

            return buildNumericPredicate(cb, caloriesPath, filterValue);
        };
    }

    private static Predicate buildNumericPredicate(CriteriaBuilder cb, Expression<? extends Number> path, FilterValue filterValue) {
        Double value = Double.parseDouble(filterValue.value().replaceAll("[^0-9.]", ""));
        return switch (filterValue.operator()) {
            case GT -> cb.gt(path, value);
            case GTE -> cb.ge(path, value);
            case LT -> cb.lt(path, value);
            case LTE -> cb.le(path, value);
            default -> cb.equal(path, value);
        };
    }


//    public static Specification<Recipe> ratingLessThan(float rating) {
//        return new Specification<Recipe>() {
//            @Override
//            public Predicate toPredicate(Root<Recipe> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
//                return criteriaBuilder.lessThan(root.<Float>get("rating"), rating);
//            }
//        };
//    }

}
