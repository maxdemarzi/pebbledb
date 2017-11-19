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
     
- [X] Build Graph
- [X] Finish Graph Tests
- [X] Add Server
- [ ] Verify all return codes for http requests
- [X] Add Labels
- [X] Add "related" capabilities
- [X] Add "related" http endpoints
- [X] Split relationshipKeys by type, try arraylist of count of longs
- [ ] Use relationship IDs instead of from-to-type-count and relationshipKeys 
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
    GraphReadBenchmarks.measureFixedSingleTraversalAndGetNodes             100        20000           100            100         1000  thrpt   10    2759647.128 ±  81088.707  ops/s
    GraphReadBenchmarks.measureFixedSingleTraversalIds                     100        20000           100            100         1000  thrpt   10   10670306.763 ± 313581.670  ops/s
    GraphReadBenchmarks.measureGetRelationshipTypeCounts                   100        20000           100            100         1000  thrpt       291047228.037               ops/s
    GraphReadBenchmarks.measureRandomSingleTraversalIds                    100        20000           100            100         1000  thrpt   10    6906189.922 ± 135208.298  ops/s
    GraphReadBenchmarks.measureSingleTraversalAndGetNodes                  100        20000           100            100         1000  thrpt   10    1760981.519 ± 457417.654  ops/s
    GraphReadBenchmarks.measureTraverse                                    100        20000           100            100         1000  thrpt   10      83191.091 ±   2446.644  ops/s
    GraphReadBenchmarks.measureTraverseAndGetNodes                         100        20000           100            100         1000  thrpt   10      18275.594 ±   6174.574  ops/s


Writes:

    Benchmark                                                     (friendsCount)  (itemCount)  (likesCount)  (personCount)  (userCount)   Mode  Cnt        Score        Error  Units
    GraphWriteBenchmarks.measureCreateEmptyNode                              100        20000           100            100         1000  thrpt   10  1423975.617 ± 105009.782  ops/s
    GraphWriteBenchmarks.measureCreateEmptyNodes                             100        20000           100            100         1000  thrpt   10     9798.026 ±    151.473  ops/s
    GraphWriteBenchmarks.measureCreateEmptyNodesAndRelationships             100        20000           100            100         1000  thrpt   10        8.314 ±      0.719  ops/s
    GraphWriteBenchmarks.measureCreateNodeWithProperties                     100        20000           100            100         1000  thrpt   10   903322.946 ± 592658.801  ops/s
    GraphWriteBenchmarks.measureCreateNodesWithProperties                    100        20000           100            100         1000  thrpt   10      913.720 ±    588.493  ops/s
    GraphWriteBenchmarks.measureCreateRandomRelationship                     100        20000           100            100         1000  thrpt   10   697265.875 ±  79542.838  ops/s
    GraphWriteBenchmarks.measureCreateRelationships                          100        20000           100            100         1000  thrpt   10       79.469 ±      8.043  ops/s
    
Traversal:

    Benchmark                                                                      (itemCount)  (likesCount)  (personCount)   Mode  Cnt      Score      Error  Units
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal          200            10           1000  thrpt   10   1907.291 ±  160.916  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal          200            10          10000  thrpt   10     88.323 ±    3.386  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal          200           100           1000  thrpt   10      1.568 ±    0.124  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal          200           100          10000  thrpt   10      0.159 ±    0.003  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal         2000            10           1000  thrpt   10   8324.429 ± 1280.521  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal         2000            10          10000  thrpt   10    649.692 ±   65.077  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal         2000           100           1000  thrpt   10     13.202 ±    1.471  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal         2000           100          10000  thrpt   10      1.118 ±    0.021  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                                200            10           1000  thrpt   10   4796.834 ±  164.478  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                                200            10          10000  thrpt   10    420.357 ±   60.361  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                                200           100           1000  thrpt   10      7.841 ±    0.313  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                                200           100          10000  thrpt   10      0.724 ±    0.029  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                               2000            10           1000  thrpt   10  15483.338 ±  760.692  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                               2000            10          10000  thrpt   10   1664.362 ±  155.678  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                               2000           100           1000  thrpt   10     60.135 ±    2.922  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                               2000           100          10000  thrpt   10      5.303 ±    0.210  ops/s
    
Aggregation:

    Benchmark                                (personCount)  Mode  Cnt   Score    Error  Units
    AggregationBenchmark.measureAggregation            100  avgt   10   0.003 ±  0.001  ms/op
    AggregationBenchmark.measureAggregation        1632803  avgt   10  52.183 ±  7.787  ms/op

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
