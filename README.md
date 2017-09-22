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
    

### Benchmarks

Reads:
    todo    rerun
    Benchmark                                                   (friendsCount)  (itemCount)  (likesCount)  (personCount)  (userCount)   Mode  Cnt          Score          Error  Units
    GraphReadBenchmarks.measureFixedSingleTraversalAndGetNodes             100        20000           100            100         1000  thrpt   10    2454065.793 ±    58106.632  ops/s
    GraphReadBenchmarks.measureFixedSingleTraversalIds                     100        20000           100            100         1000  thrpt   10   10697736.498 ±   441124.693  ops/s
    GraphReadBenchmarks.measureGetRelationshipTypeCounts                   100        20000           100            100         1000  thrpt   10  366589920.294 ± 12827830.607  ops/s
    GraphReadBenchmarks.measureRandomSingleTraversalIds                    100        20000           100            100         1000  thrpt   10    5669644.824 ±   256630.630  ops/s
    GraphReadBenchmarks.measureSingleTraversalAndGetNodes                  100        20000           100            100         1000  thrpt   10    1529085.651 ±   277345.581  ops/s
    GraphReadBenchmarks.measureTraverse                                    100        20000           100            100         1000  thrpt   10      76809.970 ±     2813.943  ops/s
    GraphReadBenchmarks.measureTraverseAndGetNodes                         100        20000           100            100         1000  thrpt   10      15688.782 ±     2492.089  ops/s        


Writes:

    Benchmark                                                     (friendsCount)  (itemCount)  (likesCount)  (personCount)  (userCount)   Mode  Cnt        Score        Error  Units
    GraphWriteBenchmarks.measureCreateEmptyNode                              100        20000           100            100         1000  thrpt   10  1372448.309 ± 214555.792  ops/s
    GraphWriteBenchmarks.measureCreateEmptyNodes                             100        20000           100            100         1000  thrpt   10     7762.371 ±    255.254  ops/s
    GraphWriteBenchmarks.measureCreateEmptyNodesAndRelationships             100        20000           100            100         1000  thrpt   10        5.036 ±      0.637  ops/s
    GraphWriteBenchmarks.measureCreateNodeWithProperties                     100        20000           100            100         1000  thrpt   10   873191.924 ± 449883.862  ops/s
    GraphWriteBenchmarks.measureCreateNodesWithProperties                    100        20000           100            100         1000  thrpt   10      921.795 ±    478.458  ops/s
    GraphWriteBenchmarks.measureCreateRandomRelationship                     100        20000           100            100         1000  thrpt   10   528108.357 ±  59303.523  ops/s
    GraphWriteBenchmarks.measureCreateRelationships                          100        20000           100            100         1000  thrpt   10       62.601 ±      8.537  ops/s

Traversal:


    Benchmark                                                                      (itemCount)  (likesCount)  (personCount)   Mode  Cnt      Score     Error  Units
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal          200            10           1000  thrpt   10   1865.560 ± 158.288  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal          200            10          10000  thrpt   10     97.424 ±   7.156  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal          200           100           1000  thrpt   10      2.171 ±   0.232  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal          200           100          10000  thrpt   10      0.231 ±   0.004  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal         2000            10           1000  thrpt   10   9709.429 ± 456.358  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal         2000            10          10000  thrpt   10    684.490 ±  59.421  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal         2000           100           1000  thrpt   10     14.956 ±   1.459  ops/s
    GraphTraversalBenchmarks.measureRecommendationRelationshipPropertiesTraversal         2000           100          10000  thrpt   10      1.497 ±   0.063  ops/s
         
    GraphTraversalBenchmarks.measureRecommendationTraversal                                200            10           1000  thrpt   10   2764.156 ±  91.275  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                                200            10          10000  thrpt   10    242.720 ±  15.405  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                                200           100           1000  thrpt   10      4.203 ±   0.163  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                                200           100          10000  thrpt   10      0.404 ±   0.008  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                               2000            10           1000  thrpt   10  12503.298 ± 146.924  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                               2000            10          10000  thrpt   10   1428.722 ±  87.747  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                               2000           100           1000  thrpt   10     40.538 ±   1.402  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal                               2000           100          10000  thrpt   10      3.731 ±   1.005  ops/s

    Node only traversals took a 33%-50% slowdown when using long instead of int in RMM.
    Relationship based traversals are now 2-10x faster specially for dense graphs, only half the speed of node id only traversals.

Aggregation:

    Benchmark                                (personCount)  Mode  Cnt   Score    Error  Units
    AggregationBenchmark.measureAggregation            100  avgt   10   0.003 ±  0.001  ms/op
    AggregationBenchmark.measureAggregation        1632803  avgt   10  61.788 ± 46.586  ms/op

### Server Benchmarks

First Create some data:

    curl -H "Content-Type: application/json" -X POST -d '{"name":"Max"}' http://localhost:8080/db/Node/node/max

Then query it:  

    wrk -t1 -c100 -d30s http://127.0.0.1:8080/db/node/max
    Running 30s test @ http://127.0.0.1:8080/db/node/max
      1 threads and 100 connections
      Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency     1.67ms  341.09us  13.85ms   81.09%
        Req/Sec    57.23k     3.31k   68.50k    68.33%
      1708257 requests in 30.01s, 237.85MB read
      Socket errors: connect 0, read 1, write 0, timeout 0
    Requests/sec:  56931.39
    Transfer/sec:      7.93MB

Shouldn't 4 worker threads get more r/s? 
What am I doing wrong?
    
    wrk -t4 -c100 -d30s http://127.0.0.1:8080/db/node/max
    Running 30s test @ http://127.0.0.1:8080/db/node/max
      4 threads and 100 connections
      Thread Stats   Avg      Stdev     Max   +/- Stdev
        Latency     1.79ms  271.05us  12.05ms   87.64%
        Req/Sec    14.04k     1.04k   16.46k    77.33%
      1681519 requests in 30.10s, 234.13MB read
    Requests/sec:  55859.02
    Transfer/sec:      7.78MB



max.json = {"name":"Tim"}
ab -n 100000 -c 32 -T "application/json"  -u max.json http://127.0.0.1:8080/db/node/Node/max/properties
