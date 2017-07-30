package com.pebbledb;

import java.util.Map;

public interface Graph {

    void clear();

    Map<String, Long> getRelationshipTypeCount(String relType);
}
