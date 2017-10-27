package com.pebbledb.benchmarks;

import com.pebbledb.graphs.FastUtilGraph;
import com.pebbledb.graphs.FastUtilGraphHash;
import com.pebbledb.graphs.FastUtilGraphLong;
import com.pebbledb.graphs.Graph;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

@State(Scope.Benchmark)
public class MultipleGraphTraversalBenchmarks {
    private Graph db;
    private Graph db2;
    private FastUtilGraphHash db3;
    private Random rand = new Random();

    @Param({"1000"})
    private int personCount;

    @Param({"200"})
    private int itemCount;

    @Param({"100"})
    private int likesCount;

    @Setup
    public void prepare() throws IOException {
        db = new FastUtilGraph();
        db2 = new FastUtilGraphLong();
        db3 = new FastUtilGraphHash();

        for (int item = 0; item < itemCount; item++) {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("id", item);
            properties.put("itemname", "itemname" + item );
            db.addNode("Item", "item" + item, properties);
            db2.addNode("Item", "item" + item, properties);
            db3.addNode("Item-item" + item, properties);
        }

        for (int person = 0; person < personCount; person++) {
            db.addNode("Person", "person" + person);
            db2.addNode("Person", "person" + person);
            db3.addNode("Person-person" + person);
            for (int like = 0; like < likesCount; like++) {
                HashMap<String, Object> props  = new HashMap<>();
                props.put("weight", rand.nextInt(10));
                db.addRelationship("LIKES", "Person", "person" + person, "Item", "item" + rand.nextInt(itemCount), props);
                db2.addRelationship("LIKES", "Person", "person" + person, "Item", "item" + rand.nextInt(itemCount), props);
                db3.addRelationship("LIKES", "Person-person" + person, "Item-item" + rand.nextInt(itemCount), props);
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
        Collection<Integer> itemsYouLike = db.getOutgoingRelationshipNodeIds("LIKES", "Person", "person" + rand.nextInt(personCount));
        Map<Integer, LongAdder> occurrences = new HashMap<>();
        for (Integer item : itemsYouLike) {
            for (Integer person : db.getIncomingRelationshipNodeIds("LIKES", item)) {
                db.getOutgoingRelationshipNodeIds("LIKES", person)
                        .forEach(i-> occurrences.computeIfAbsent(i, (t) -> new LongAdder())
                                .increment());
            }
        }
        itemsYouLike.forEach(occurrences::remove);
        List<Map.Entry<Integer, LongAdder>> itemList = new ArrayList<>(occurrences.entrySet());
        itemList.sort((a, b) -> (b.getValue().intValue() - a.getValue().intValue()));
        return itemList.subList(0, Math.min(itemList.size(), 10));
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public List measureRecommendationTraversalDB2() throws IOException {
        Collection<Integer> itemsYouLike = db2.getOutgoingRelationshipNodeIds("LIKES", "Person", "person" + rand.nextInt(personCount));
        Map<Integer, LongAdder> occurrences = new HashMap<>();
        for (Integer item : itemsYouLike) {
            for (Integer person : db2.getIncomingRelationshipNodeIds("LIKES", item)) {
                db2.getOutgoingRelationshipNodeIds("LIKES", person)
                        .forEach(i-> occurrences.computeIfAbsent(i, (t) -> new LongAdder())
                                .increment());
            }
        }
        itemsYouLike.forEach(occurrences::remove);
        List<Map.Entry<Integer, LongAdder>> itemList = new ArrayList<>(occurrences.entrySet());
        itemList.sort((a, b) -> (b.getValue().intValue() - a.getValue().intValue()));
        return itemList.subList(0, Math.min(itemList.size(), 10));
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public List measureRecommendationTraversalDB3() throws IOException {
        Collection<Integer> itemsYouLike = db3.getOutgoingRelationshipNodeIds("LIKES", "Person-person" + rand.nextInt(personCount));
        Map<Integer, LongAdder> occurrences = new HashMap<>();
        for (Integer item : itemsYouLike) {
            for (Integer person : db3.getIncomingRelationshipNodeIds("LIKES", item)) {
                db3.getOutgoingRelationshipNodeIds("LIKES", person)
                        .forEach(i-> occurrences.computeIfAbsent(i, (t) -> new LongAdder())
                                .increment());
            }
        }
        itemsYouLike.forEach(occurrences::remove);
        List<Map.Entry<Integer, LongAdder>> itemList = new ArrayList<>(occurrences.entrySet());
        itemList.sort((a, b) -> (b.getValue().intValue() - a.getValue().intValue()));
        return itemList.subList(0, Math.min(itemList.size(), 10));
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public List measureRecommendationRelationshipPropertiesTraversal() throws IOException {
        Collection<Integer> itemsYouLike = new HashSet<>();
        Collection<Map<String,Object>> likes = db.getOutgoingRelationships("LIKES", "Person", "person" + rand.nextInt(personCount));
        Map<Integer, LongAdder> occurrences = new HashMap<>();
        for (Map<String,Object> like : likes) {
            //if ((int) like.get("weight") > 8 ) {
            int item = (int)like.get("_outgoing_node_id");
            itemsYouLike.add(item);
            for (Map<String, Object> like2 : db.getIncomingRelationships("LIKES", item)) {
                //if ((int) like2.get("weight") > 8 ) {
                int person = (int)like2.get("_incoming_node_id");
                for ( Map<String, Object> like3 : db.getOutgoingRelationships("LIKES", person)){
                    //if ((int) like3.get("weight") > 8 ) {
                    occurrences.computeIfAbsent((int)like3.get("_outgoing_node_id"), (t) -> new LongAdder())
                            .increment();
                    //}
                }
                //}
            }
            //}
        }
        itemsYouLike.forEach(occurrences::remove);
        List<Map.Entry<Integer, LongAdder>> itemList = new ArrayList<>(occurrences.entrySet());
        itemList.sort((a, b) -> (b.getValue().intValue() - a.getValue().intValue()));
        return itemList.subList(0, Math.min(itemList.size(), 10));
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public List measureRecommendationRelationshipPropertiesTraversalDB2() throws IOException {
        Collection<Integer> itemsYouLike = new HashSet<>();
        Collection<Map<String,Object>> likes = db2.getOutgoingRelationships("LIKES", "Person", "person" + rand.nextInt(personCount));
        Map<Integer, LongAdder> occurrences = new HashMap<>();
        for (Map<String,Object> like : likes) {
            //if ((int) like.get("weight") > 8 ) {
            int item = (int)like.get("_outgoing_node_id");
            itemsYouLike.add(item);
            for (Map<String, Object> like2 : db2.getIncomingRelationships("LIKES", item)) {
                //if ((int) like2.get("weight") > 8 ) {
                int person = (int)like2.get("_incoming_node_id");
                for ( Map<String, Object> like3 : db2.getOutgoingRelationships("LIKES", person)){
                    //if ((int) like3.get("weight") > 8 ) {
                    occurrences.computeIfAbsent((int)like3.get("_outgoing_node_id"), (t) -> new LongAdder())
                            .increment();
                    //}
                }
                //}
            }
            //}
        }
        itemsYouLike.forEach(occurrences::remove);
        List<Map.Entry<Integer, LongAdder>> itemList = new ArrayList<>(occurrences.entrySet());
        itemList.sort((a, b) -> (b.getValue().intValue() - a.getValue().intValue()));
        return itemList.subList(0, Math.min(itemList.size(), 10));
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public List measureRecommendationRelationshipPropertiesTraversalDB3() throws IOException {
        Collection<Integer> itemsYouLike = new HashSet<>();
        Collection<Map<String,Object>> likes = db3.getOutgoingRelationships("LIKES", "Person-person" + rand.nextInt(personCount));
        Map<Integer, LongAdder> occurrences = new HashMap<>();
        for (Map<String,Object> like : likes) {
            //if ((int) like.get("weight") > 8 ) {
            int item = (int)like.get("_outgoing_node_id");
            itemsYouLike.add(item);
            for (Map<String, Object> like2 : db3.getIncomingRelationships("LIKES", item)) {
                //if ((int) like2.get("weight") > 8 ) {
                int person = (int)like2.get("_incoming_node_id");
                for ( Map<String, Object> like3 : db3.getOutgoingRelationships("LIKES", person)){
                    //if ((int) like3.get("weight") > 8 ) {
                    occurrences.computeIfAbsent((int)like3.get("_outgoing_node_id"), (t) -> new LongAdder())
                            .increment();
                    //}
                }
                //}
            }
            //}
        }
        itemsYouLike.forEach(occurrences::remove);
        List<Map.Entry<Integer, LongAdder>> itemList = new ArrayList<>(occurrences.entrySet());
        itemList.sort((a, b) -> (b.getValue().intValue() - a.getValue().intValue()));
        return itemList.subList(0, Math.min(itemList.size(), 10));
    }
}
