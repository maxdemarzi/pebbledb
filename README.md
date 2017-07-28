# Pebble DB

    Benchmark                                                      (friendsCount)  (itemCount)  (likesCount)  (personCount)  (userCount)   Mode  Cnt         Score        Error  Units
    GraphReadBenchmarks.measureFixedSingleTraversalAndGetNodes                100        20000           100            100         1000  thrpt   10     14054.365 ±    199.817  ops/s
    GraphReadBenchmarks.measureFixedSingleTraversalAndGetNodesTwo             100        20000           100            100         1000  thrpt   10     30552.773 ±    443.168  ops/s
    GraphReadBenchmarks.measureFixedSingleTraversalIds                        100        20000           100            100         1000  thrpt   10  11649087.148 ± 223421.621  ops/s
    GraphReadBenchmarks.measureRandomSingleTraversalIds                       100        20000           100            100         1000  thrpt   10   6668307.453 ± 103308.173  ops/s
    GraphReadBenchmarks.measureSingleTraversalAndGetNodes                     100        20000           100            100         1000  thrpt   10     14167.208 ±    309.504  ops/s
    GraphReadBenchmarks.measureSingleTraversalAndGetNodesTwo                  100        20000           100            100         1000  thrpt   10     30291.909 ±    244.282  ops/s
    GraphReadBenchmarks.measureTraverse                                       100        20000           100            100         1000  thrpt   10     87353.687 ±   1093.052  ops/s
    GraphReadBenchmarks.measureTraverseAndGetNodes                            100        20000           100            100         1000  thrpt   10       141.828 ±      1.984  ops/s
    GraphReadBenchmarks.measureTraverseAndGetNodesTwo                         100        20000           100            100         1000  thrpt   10       299.474 ±      6.025  ops/s
        
    Benchmark                                                     (friendsCount)  (itemCount)  (likesCount)  (personCount)  (userCount)   Mode  Cnt        Score        Error  Units
    GraphWriteBenchmarks.measureCreateEmptyNode                              100        20000           100            100         1000  thrpt   10  1497050.946 ± 113563.892  ops/s
    GraphWriteBenchmarks.measureCreateEmptyNodes                             100        20000           100            100         1000  thrpt   10    19459.899 ±    551.819  ops/s
    GraphWriteBenchmarks.measureCreateEmptyNodesAndRelationships             100        20000           100            100         1000  thrpt   10       30.166 ±      1.902  ops/s
    GraphWriteBenchmarks.measureCreateNodeWithProperties                     100        20000           100            100         1000  thrpt   10   935777.229 ± 622202.214  ops/s
    GraphWriteBenchmarks.measureCreateNodesWithProperties                    100        20000           100            100         1000  thrpt   10     1054.875 ±    518.271  ops/s