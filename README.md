# Pebble DB


    Pebble DB is a Proof of Concept in building an in-memory, single threaded, replicated database system.
    Following some of the same ideas as VoltDB, as explained http://slideshot.epfl.ch/play/suri_stonebraker
    with a short version https://www.youtube.com/watch?v=0wcNqez6bMU
    
    It is meant to handle small graphs or lots of small isolated sub-graphs (like those found in SaaS Applications).



    
### Todos
     
- [ ] Build Graph
- [ ] Finish Graph Tests
- [ ] Add Server
- [ ] Verify all return codes for http requests
- [ ] Add Query Language (Cypher, Graphql)
- [ ] Compare Search Capabilities (Lucene and http://mg4j.di.unimi.it/)
    

### Benchmarks

    Benchmark                                                   (friendsCount)  (itemCount)  (likesCount)  (personCount)  (userCount)   Mode  Cnt          Score          Error  Units
    GraphReadBenchmarks.measureFixedSingleTraversalAndGetNodes             100        20000           100            100         1000  thrpt   10    2454065.793 ±    58106.632  ops/s
    GraphReadBenchmarks.measureFixedSingleTraversalIds                     100        20000           100            100         1000  thrpt   10   10697736.498 ±   441124.693  ops/s
    GraphReadBenchmarks.measureGetRelationshipTypeCounts                   100        20000           100            100         1000  thrpt   10  366589920.294 ± 12827830.607  ops/s
    GraphReadBenchmarks.measureRandomSingleTraversalIds                    100        20000           100            100         1000  thrpt   10    5669644.824 ±   256630.630  ops/s
    GraphReadBenchmarks.measureSingleTraversalAndGetNodes                  100        20000           100            100         1000  thrpt   10    1529085.651 ±   277345.581  ops/s
    GraphReadBenchmarks.measureTraverse                                    100        20000           100            100         1000  thrpt   10      76809.970 ±     2813.943  ops/s
    GraphReadBenchmarks.measureTraverseAndGetNodes                         100        20000           100            100         1000  thrpt   10      15688.782 ±     2492.089  ops/s        

        
    Benchmark                                                     (friendsCount)  (itemCount)  (likesCount)  (personCount)  (userCount)   Mode  Cnt        Score        Error  Units
    GraphWriteBenchmarks.measureCreateEmptyNode                              100        20000           100            100         1000  thrpt   10  1497050.946 ± 113563.892  ops/s
    GraphWriteBenchmarks.measureCreateEmptyNodes                             100        20000           100            100         1000  thrpt   10    19459.899 ±    551.819  ops/s
    GraphWriteBenchmarks.measureCreateEmptyNodesAndRelationships             100        20000           100            100         1000  thrpt   10       30.166 ±      1.902  ops/s
    GraphWriteBenchmarks.measureCreateNodeWithProperties                     100        20000           100            100         1000  thrpt   10   935777.229 ± 622202.214  ops/s
    GraphWriteBenchmarks.measureCreateNodesWithProperties                    100        20000           100            100         1000  thrpt   10     1054.875 ±    518.271  ops/s


    Benchmark                                                (itemCount)  (likesCount)  (personCount)   Mode  Cnt     Score     Error  Units
    GraphTraversalBenchmarks.measureRecommendationTraversal          200            10           1000  thrpt   10  1695.320 ±  77.257  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal          200            10          10000  thrpt   10   164.601 ±   5.097  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal          200           100           1000  thrpt   10     3.635 ±   0.111  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal          200           100          10000  thrpt   10     0.385 ±   0.046  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal         2000            10           1000  thrpt   10  9033.580 ± 418.925  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal         2000            10          10000  thrpt   10  1035.752 ±  13.313  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal         2000           100           1000  thrpt   10    19.120 ±   0.816  ops/s
    GraphTraversalBenchmarks.measureRecommendationTraversal         2000           100          10000  thrpt   10     2.012 ±   0.085  ops/s


### Server Benchmarks

First Create some data:

    curl -H "Content-Type: application/json" -X POST -d '{"name":"Max"}' http://localhost:8080/db/node/max

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



