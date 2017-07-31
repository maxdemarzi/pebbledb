package com.pebbledb.benchmarks;

import com.pebbledb.Graph;
import com.pebbledb.graphs.FastUtilGraph;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

@State(Scope.Benchmark)
public class GraphTraversalBenchmarks {
    private Graph db;
    private Random rand = new Random();

    @Param({"1000", "10000"})
    //@Param({"10000"})
    private int personCount;

    @Param({"200", "2000"})
    //@Param({"200"})
    private int itemCount;

    @Param({"10", "100"})
    //@Param({"100"})
    private int likesCount;

    @Setup
    public void prepare() throws IOException {
        db = new FastUtilGraph();

        for (int item = 0; item < itemCount; item++) {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("id", item);
            properties.put("itemname", "itemname" + item );
            db.addNode("item" + item, properties);
        }

        for (int person = 0; person < personCount; person++) {
            db.addNode("person" + person);
            for (int like = 0; like < likesCount; like++) {
                db.addRelationship("LIKES", "person" + person, "item" + rand.nextInt(itemCount));
            }
        }
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public List measureRecommendationTraversal() throws IOException {
        Collection<Integer> itemsYouLike = db.getOutgoingRelationshipNodeIds("LIKES", "person" + rand.nextInt(personCount));
        Map<Integer, LongAdder> occurrences = new HashMap<>();
        for (Integer item : itemsYouLike) {
            for (Integer person : db.getIncomingRelationshipNodeIds("LIKES", item)) {
                db.getOutgoingRelationshipNodeIds("LIKES", person)
                        .forEach(i-> occurrences.computeIfAbsent(i, (t) -> new LongAdder())
                                .increment());

            }
        }
        occurrences.remove(itemsYouLike);
        List<Map.Entry<Integer, LongAdder>> itemList = new ArrayList<>(occurrences.entrySet());
        Collections.sort(itemList, (a, b) -> ( b.getValue().intValue() - a.getValue().intValue() ));
        return itemList.subList(0, Math.min(itemList.size(), 10));
    }
}
