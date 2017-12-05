
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.filter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.dellroad.linode.apiv4.Constants;

/**
 * Builds filters for Linode queries.
 */
public class FilterBuilder {

    private Condition condition;
    private String orderBy;
    private boolean ascending;

// And/Or

    public Condition and(Condition... conditions) {
        this.checkConditions(conditions);
        return this.and(Arrays.asList(conditions));
    }

    public Condition and(List<Condition> conditions) {
        this.checkConditions(conditions);
        return new Condition(Constants.FILTER_AND, Collections.unmodifiableList(conditions));
    }

    public Condition or(Condition... conditions) {
        this.checkConditions(conditions);
        return this.and(Arrays.asList(conditions));
    }

    public Condition or(List<Condition> conditions) {
        this.checkConditions(conditions);
        return new Condition(Constants.FILTER_OR, Collections.unmodifiableList(conditions));
    }

// Comparison

    public Condition greaterThan(String attribute, double value) {
        return new Condition(Constants.FILTER_GT, attribute, value);
    }

    public Condition greaterThanOrEqual(String attribute, double value) {
        return new Condition(Constants.FILTER_GTE, attribute, value);
    }

    public Condition lessThan(String attribute, double value) {
        return new Condition(Constants.FILTER_LT, attribute, value);
    }

    public Condition lessThanOrEqual(String attribute, double value) {
        return new Condition(Constants.FILTER_LTE, attribute, value);
    }

// Equal

    public Condition equal(String attribute, boolean value) {
        return new Condition(attribute, value);
    }

    public Condition equal(String attribute, double value) {
        return new Condition(attribute, value);
    }

    public Condition equal(String attribute, String value) {
        if (value == null)
            throw new IllegalArgumentException("null value");
        return new Condition(attribute, value);
    }

    public Condition equal(String attribute, Date date) {
        return this.equal(attribute, Constants.toString(date));
    }

// Not Equal

    public Condition notEqual(String attribute, boolean value) {
        return new Condition(Constants.FILTER_NEQ, attribute, value);
    }

    public Condition notEqual(String attribute, double value) {
        return new Condition(Constants.FILTER_NEQ, attribute, value);
    }

    public Condition notEqual(String attribute, String value) {
        if (value == null)
            throw new IllegalArgumentException("null value");
        return new Condition(Constants.FILTER_NEQ, attribute, value);
    }

    public Condition notEqual(String attribute, Date date) {
        return this.notEqual(attribute, Constants.toString(date));
    }

// Contains

    public Condition contains(String attribute, String substring) {
        if (substring == null)
            throw new IllegalArgumentException("null substring");
        return new Condition(Constants.FILTER_CONTAINS, attribute, substring);
    }

// Sorting

    /**
     * Configure sort ordering.
     *
     * @param attribute which attribute to sort by (ascending)
     * @return this instance
     * @throws IllegalArgumentException if {@code attribute} is null
     */
    public FilterBuilder orderBy(String attribute) {
        return this.orderBy(attribute, true);
    }

    /**
     * Configure sort ordering.
     *
     * @param attribute which attribute to sort by
     * @param ascending true for ascending ordering, false for descending
     * @return this instance
     * @throws IllegalArgumentException if {@code attribute} is null
     */
    public FilterBuilder orderBy(String attribute, boolean ascending) {
        if (attribute == null)
            throw new IllegalArgumentException("null attribute");
        this.orderBy = attribute;
        this.ascending = ascending;
        return this;
    }

// Condition

    /**
     * Configure filter requirement.
     *
     * @param condition condition to apply to restrict results
     * @return this instance
     * @throws IllegalArgumentException if {@code condition} is null
     */
    public FilterBuilder where(Condition condition) {
        if (condition == null)
            throw new IllegalArgumentException("null condition");
        this.condition = condition;
        return this;
    }

// Building

    /**
     * Build a filter based on the current configuration.
     *
     * @return query filter
     */
    public Filter build() {
        final HashMap<String, Object> map = new HashMap<>(3);
        if (this.condition != null)
            map.put(this.condition.getName(), this.condition.getValue());
        if (this.orderBy != null) {
            map.put(Constants.FILTER_ORDER_BY, this.orderBy);
            map.put(Constants.FILTER_ORDER, this.ascending ? Constants.FILTER_ORDER_ASC : Constants.FILTER_ORDER_DESC);
        }
        return new Filter(map);
    }

// Internal methods

    private void checkConditions(Condition[] conditions) {
        if (conditions == null)
            throw new IllegalArgumentException("null conditions");
        this.checkConditions(Arrays.asList(conditions));
    }

    private void checkConditions(List<Condition> conditions) {
        if (conditions == null)
            throw new IllegalArgumentException("null conditions");
        for (Condition condition : conditions) {
            if (condition == null)
                throw new IllegalArgumentException("null condition");
        }
    }

// Condition

    /**
     * Condition (i.e., predicate) that is part of a filter specification.
     *
     * <p>
     * Instances are immutable.
     */
    public static final class Condition {

        private final String name;
        private final Object value;

        private Condition(String name, Object value) {
            if (name == null)
                throw new IllegalArgumentException("null name");
            this.name = name;
            this.value = value;
        }

        private Condition(String operator, String name, Object value) {
            this(name, new Condition(operator, value));
        }

        public String getName() {
            return this.name;
        }

        public Object getValue() {
            return this.value;
        }
    }
}
