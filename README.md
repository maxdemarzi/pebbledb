# Pebble DB


    Pebble DB is a Proof of Concept in building an in-memory, single threaded, replicated database system.
    Following some of the same ideas as VoltDB, as explained http://slideshot.epfl.ch/play/suri_stonebraker
    with a short version https://www.youtube.com/watch?v=0wcNqez6bMU
    
    It is meant to handle small graphs or lots of small isolated sub-graphs (like those found in SaaS Applications).



    
### Todos
     
    - [] Build Graph
    - [] Finish Graph Tests
    - [] Add Server
    - [] Add Query Language (Cypher, Graphql)
    - [] Compare Search Capabilities (Lucene and http://mg4j.di.unimi.it/)
    

### Benchmarks

    Benchmark                                                      (friendsCount)  (itemCount)  (likesCount)  (personCount)  (userCount)   Mode  Cnt         Score         Error  Units
    GraphReadBenchmarks.measureFixedSingleTraversalAndGetNodes                100        20000           100            100         1000  thrpt   10    427352.070 ±    7236.334  ops/s
    GraphReadBenchmarks.measureFixedSingleTraversalAndGetNodesTwo             100        20000           100            100         1000  thrpt   10   2239232.743 ±   40433.968  ops/s
    GraphReadBenchmarks.measureFixedSingleTraversalIds                        100        20000           100            100         1000  thrpt   10    841944.337 ±   17699.521  ops/s
    GraphReadBenchmarks.measureGetRelationshipTypeCounts                      100        20000           100            100         1000  thrpt   10  26631245.864 ± 1003750.147  ops/s
    GraphReadBenchmarks.measureRandomSingleTraversalIds                       100        20000           100            100         1000  thrpt   10   1785571.669 ±   55685.241  ops/s
    GraphReadBenchmarks.measureRandomSingleTraversalIds2                      100        20000           100            100         1000  thrpt   10    342575.348 ±   25010.224  ops/s
    GraphReadBenchmarks.measureSingleTraversalAndGetNodes                     100        20000           100            100         1000  thrpt   10    783197.953 ±   65303.831  ops/s
    GraphReadBenchmarks.measureSingleTraversalAndGetNodesTwo                  100        20000           100            100         1000  thrpt   10   1783393.543 ±   65442.713  ops/s
    GraphReadBenchmarks.measureTraverse                                       100        20000           100            100         1000  thrpt   10     71618.164 ±    1717.757  ops/s
    GraphReadBenchmarks.measureTraverseAndGetNodes                            100        20000           100            100         1000  thrpt   10      7760.404 ±     476.626  ops/s
    GraphReadBenchmarks.measureTraverseAndGetNodesTwo                         100        20000           100            100         1000  thrpt   10     18436.220 ±     566.005  ops/s
        
        
    Benchmark                                                     (friendsCount)  (itemCount)  (likesCount)  (personCount)  (userCount)   Mode  Cnt        Score        Error  Units
    GraphWriteBenchmarks.measureCreateEmptyNode                              100        20000           100            100         1000  thrpt   10  1497050.946 ± 113563.892  ops/s
    GraphWriteBenchmarks.measureCreateEmptyNodes                             100        20000           100            100         1000  thrpt   10    19459.899 ±    551.819  ops/s
    GraphWriteBenchmarks.measureCreateEmptyNodesAndRelationships             100        20000           100            100         1000  thrpt   10       30.166 ±      1.902  ops/s
    GraphWriteBenchmarks.measureCreateNodeWithProperties                     100        20000           100            100         1000  thrpt   10   935777.229 ± 622202.214  ops/s
    GraphWriteBenchmarks.measureCreateNodesWithProperties                    100        20000           100            100         1000  thrpt   10     1054.875 ±    518.271  ops/s
