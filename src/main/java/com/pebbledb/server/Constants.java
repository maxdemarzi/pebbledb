package com.pebbledb.server;

public class Constants {

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String ID="id";
    public static final String KEY="key";
    public static final String TYPE="type";
    public static final String FROM="from";
    public static final String TO="to";
    public static final String NUMBER="number";

    static final String GET="GET";
    static final String POST="POST";
    static final String DELETE="DELETE";
    static final String PUT="PUT";
    static final String PATH_REL_TYPES="/db/relationship_types";
    static final String PATH_REL_TYPES_COUNT="/db/relationship_types/count";
    static final String PATH_REL_TYPE_COUNT="/db/relationship_types/{type}/count";
    static final String PATH_NODE="/db/node/{id}";
    static final String PATH_NODE_PROPERTIES="/db/node/{id}/properties";
    static final String PATH_NODE_PROPERTY="/db/node/{id}/property/{key}";
    static final String PATH_REL="/db/relationship/{type}/{from}/{to}";
    static final String PATH_REL_PROPERTIES ="/db/relationship/{type}/{from}/{to}/properties";
    static final String PATH_REL_PROPERTY="/db/relationship/{type}/{from}/{to}/property/{key}";
    static final String PATH_ADD_REL="/db/relationship/{type}/{from}/{to}/{number}";
    static final String PATH_ADD_REL_PROPERTIES="/db/relationship/{type}/{from}/{to}/{number}/properties";
    static final String PATH_ADD_REL_PROPERTY="/db/relationship/{type}/{from}/{to}/{number}/property/{key}";

}