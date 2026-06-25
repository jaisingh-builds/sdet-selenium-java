package com.ust.sdet.pages.roles;

public interface Searchable<T> {
    T searchFor(String query);
}
