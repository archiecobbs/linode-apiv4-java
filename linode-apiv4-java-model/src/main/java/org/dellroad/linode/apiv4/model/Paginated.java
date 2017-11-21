
/*
 * Copyright (C) 2017 Archie L. Cobbs. All rights reserved.
 */

package org.dellroad.linode.apiv4.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Iterator;
import java.util.List;

/**
 * Linode API paginated response.
 *
 * @see <a href="https://developers.linode.com/v4/pagination">Pagination</a>
 */
public abstract class Paginated<T> implements Iterable<T> {

    private final Class<T> elementType;

    private int page;
    private int pages;
    private int results;
    private List<T> data;

    protected Paginated(Class<T> elementType) {
        if (elementType == null)
            throw new IllegalArgumentException("null elementType");
        this.elementType = elementType;
    }

    @JsonIgnore
    public Class<T> getElementType() {
        return this.elementType;
    }

    public int getPage() {
        return this.page;
    }
    public void setPage(final int page) {
        this.page = page;
    }

    public int getPages() {
        return this.pages;
    }
    public void setPages(final int pages) {
        this.pages = pages;
    }

    public int getResults() {
        return this.results;
    }
    public void setResults(final int results) {
        this.results = results;
    }

    public List<T> getData() {
        return this.data;
    }
    public void setData(final List<T> data) {
        this.data = data;
    }

// Iterable

    @Override
    public Iterator<T> iterator() {
        return this.data.iterator();
    }
}
