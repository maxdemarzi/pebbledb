# Pebble DB


    Pebble DB is a Proof of Concept in building an in-memory, single threaded, replicated database system.
    Following some of the same ideas as VoltDB, as explained http://slideshot.epfl.ch/play/suri_stonebraker
    with a short version https://www.youtube.com/watch?v=0wcNqez6bMU
    
    It is meant to handle small graphs or lots of small isolated sub-graphs (like those found in SaaS Applications).

[![Coverage Status](https://coveralls.io/repos/github/maxdemarzi/pebbledb/badge.svg?branch=master)](https://coveralls.io/github/maxdemarzi/pebbledb?branch=master)
[![Build Status](https://travis-ci.org/maxdemarzi/pebbledb.svg?branch=master)](https://travis-ci.org/maxdemarzi/pebbledb)
[![Quality Gate](https://sonarcloud.io/api/badges/measure?key=pebbledb&metric=ncloc)](https://sonarcloud.io/dashboard/index/pebbledb)
[![Quality Gate](https://sonarcloud.io/api/badges/measure?key=pebbledb&metric=coverage)](https://sonarcloud.io/dashboard/index/pebbledb)
[![Quality Gate](https://sonarcloud.io/api/badges/measure?key=pebbledb&metric=new_bugs)](https://sonarcloud.io/dashboard/index/pebbledb)
[![Quality Gate](https://sonarcloud.io/api/badges/measure?key=pebbledb&metric=new_code_smells)](https://sonarcloud.io/dashboard/index/pebbledb)
[![Quality Gate](https://sonarcloud.io/api/badges/measure?key=pebbledb&metric=new_vulnerabilities)](https://sonarcloud.io/dashboard/index/pebbledb)
[![Quality Gate](https://sonarcloud.io/api/badges/measure?key=pebbledb&metric=sqale_debt_ratio)](https://sonarcloud.io/dashboard/index/pebbledb)
[![Quality Gate](https://sonarcloud.io/api/badges/measure?key=pebbledb&metric=function_complexity)](https://sonarcloud.io/dashboard/index/pebbledb)


    
### Todos
     
- [ ] Build Graph
- [ ] Try a compressed int library for keeping nodes/rels instead of Multimap<Integer, Integer>
- [ ] Try roaring bitmap in rels + nodes in array.
- [ ] Finish Graph Tests
- [ ] Add Server
- [ ] Verify all return codes for http requests
- [X] Add Labels
- [ ] Add "related?" capabilities
- [ ] Add Swagger UI
- [ ] Add Query Language (Cypher, Graphql)
- [ ] Compare Search Capabilities (http://javatechniques.com/blog/lucene-in-memory-text-search-example/ and http://mg4j.di.unimi.it/)
- [ ] Add Metrics ( http://metrics.dropwizard.io/3.1.0/getting-started/ )
- [ ] Add Dagger for DI of Metrics and others
- [ ] Add gRPC
- [ ] Add GraphQL ( https://www.howtographql.com/graphql-java/0-introduction/ )    

### Benchmarks

Reads:

    Benchmark                                                   (friendsCount)  (itemCount)  (likesCount)  (personCount)  (userCount)   Mode  Cnt          Score        Error  Units
    GraphReadBenchmarks.measureFixedSingleTraversalAndGetNodes             100        20000           100            100         1000  thrpt   10    2563159.143 ±  59010.480  ops/s
    GraphReadBenchmarks.measureFixedSingleTraversalIds                     100        20000           100            100         1000  thrpt   10    9181760.531 ± 434822.302  ops/s
    GraphReadBenchmarks.measureGetRelationshipTypeCounts                   100        20000           100            100         1000  thrpt       322568007.229               ops/s
    GraphReadBenchmarks.measureRandomSingleTraversalIds                    100        20000           100            100         1000  thrpt   10    5179175.318 ± 170287.091  ops/s
    GraphReadBenchmarks.measureSingleTraversalAndGetNodes                  100        20000           100            100         1000  thrpt   10    1644924.054 ± 400288.934  ops/s
    GraphReadBenchmarks.measureTraverse                                    100        20000           100            100         1000  thrpt   10      58974.473 ±   3026.723  ops/s
    GraphReadBenchmarks.measureTraverseAndGetNodes                         100        20000           100            100         1000  thrpt   10      15076.746 ±   3649.439  ops/s        


Writes:

    Benchmark                                                     (friendsCount)  (itemCount)  (likesCount)  (personCount)  (userCount)   Mode  Cnt        Score        Error  Units
    GraphWriteBenchmarks.measureCreateEmptyNode                              100        20000           100            100         1000  thrpt   10  1427600.403 ±  98529.040  ops/s
    GraphWriteBenchmarks.measureCreateEmptyNodes                             100        20000           100            100         1000  thrpt   10     8097.980 ±    129.279  ops/s
    GraphWriteBenchmarks.measureCreateEmptyNodesAndRelationships             100        20000           100            100         1000  thrpt   10        5.706 ±      0.383  ops/s
    GraphWriteBenchmarks.measureCreateNodeWithProperties                     100        20000           100            100         1000  thrpt   10  1039897.005 ± 687365.340  ops/s
    GraphWriteBenchmarks.measureCreateNodesWithProperties                    100        20000           100            100         1000  thrpt   10      911.730 ±    638.677  ops/s
    GraphWriteBenchmarks.measureCreateRandomRelationship                     100        20000           100            100         1000  thrpt   10   619116.884 ±  79544.314  ops/s
    GraphWriteBenchmarks.measureCreateRelationships                          100        20000           100            100         1000  thrpt   10       68.562 ±      5.977  ops/s

Traversal:

    Benchmark                                                                      (itemCount)  (likesCount)  (personCount)   Mode  Cnt      Score     Error  Units
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal          200            10           1000  thrpt   10   2261.290 ±  39.471  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal          200            10          10000  thrpt   10    105.496 ±   5.922  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal          200           100           1000  thrpt   10      2.179 ±   0.201  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal          200           100          10000  thrpt   10      0.188 ±   0.003  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal         2000            10           1000  thrpt   10  10670.666 ± 709.569  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal         2000            10          10000  thrpt   10    770.355 ±  60.328  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal         2000           100           1000  thrpt   10     16.292 ±   1.381  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal         2000           100          10000  thrpt   10      1.248 ±   0.051  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                                200            10           1000  thrpt   10   5004.603 ±  89.177  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                                200            10          10000  thrpt   10    520.181 ±  45.594  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                                200           100           1000  thrpt   10      8.541 ±   0.211  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                                200           100          10000  thrpt   10      0.787 ±   0.015  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                               2000            10           1000  thrpt   10  17380.405 ± 289.100  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                               2000            10          10000  thrpt   10   2102.294 ± 150.040  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                               2000           100           1000  thrpt   10     66.361 ±   2.105  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                               2000           100          10000  thrpt   10      5.722 ±   0.138  ops/s


Aggregation:

    Benchmark                                (personCount)  Mode  Cnt   Score    Error  Units
    AggregationBenchmark.measureAggregation            100  avgt   10   0.003 ±  0.001  ms/op
    AggregationBenchmark.measureAggregation        1632803  avgt   10  54.298 ±  5.004  ms/op

### Server Benchmarks

First Create some data:

    curl -H "Content-Type: application/json" -X POST -d '{"name":"Max"}' http://localhost:8080/db/node/User/max

Then query it:  

    wrk -t1 -c100 -d30s http://127.0.0.1:8080/db/node/User/max
    Running 30s test @ http://127.0.0.1:8080/db/node/User/max
      1 threads and 100 connections
      Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency     1.82ms  774.41us  40.28ms   97.13%
        Req/Sec    55.50k     5.75k   63.19k    95.33%
      1656491 requests in 30.00s, 230.64MB read
    Requests/sec:  55210.34
    Transfer/sec:      7.69MB

Shouldn't 4 worker threads get more r/s? 
What am I doing wrong?
    
    wrk -t4 -c100 -d30s http://127.0.0.1:8080/db/node/User/max
    Running 30s test @ http://127.0.0.1:8080/db/node/User/max
      4 threads and 100 connections
      Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency     1.85ms  251.17us  17.78ms   97.22%
        Req/Sec    13.54k   612.84    14.49k    95.02%
      1621876 requests in 30.10s, 225.82MB read
    Requests/sec:  53879.97
    Transfer/sec:      7.50MB



max.json = {"name":"Tim"}
ab -n 100000 -c 32 -T "application/json"  -u max.json http://127.0.0.1:8080/db/node/User/max/properties
