package com.pebbledb.benchmarks;

import com.pebbledb.Graph;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class GraphReadBenchmarks {

    private Graph db;
    private Random rand = new Random();

    @Param({"1000"})
    private int userCount;

    @Param({"100"})
    private int personCount;

    @Param({"20000"})
    private int itemCount;

    @Param({"100"})
    private int friendsCount;

    @Param({"100"})
    private int likesCount;

    @Setup(Level.Trial)
    public void prepare() throws IOException {
        db = new Graph();

        for (int item = 0; item < itemCount; item++) {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("id", item);
            properties.put("itemname", "itemname" + item );
            db.addNode("item" + item, properties);
        }

        for (int person = 0; person < personCount; person++) {
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
    public int measureTraverse() throws IOException {
        int person;
        for (person = 0; person < personCount; person++) {
            db.getOutgoingRelationshipNodeIds("LIKES", "person" + person);
        }
        return person;
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 20)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public int measureTraverseAndGetNodes() throws IOException {
        int person;
        for (person = 0; person < personCount; person++) {
            db.getOutgoingRelationshipNodes("LIKES", "person" + person);
        }
        return person;
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 20)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public int measureTraverseAndGetNodesTwo() throws IOException {
        int person;
        for (person = 0; person < personCount; person++) {
            db.getOutgoingRelationshipNodesTwo("LIKES", "person" + person);
        }
        return person;
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 20)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public int measureTraverseAndGetNodesThree() throws IOException {
        int person;
        for (person = 0; person < personCount; person++) {
            db.getOutgoingRelationshipNodesThree("LIKES", "person" + person);
        }
        return person;
    }
    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void measureRandomSingleTraversal() throws IOException {
        db.getOutgoingRelationshipNodeIds("LIKES", "person" + rand.nextInt(personCount));
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void measureFixedSingleTraversal() throws IOException {
        db.getOutgoingRelationshipNodeIds("LIKES", "person0");
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void measureFixedSingleTraversalAndGetNodes() throws IOException {
        db.getOutgoingRelationshipNodes("LIKES", "person0");
    }

    @Benchmark
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void measureSingleTraversalAndGetNodes() throws IOException {
        db.getOutgoingRelationshipNodes("LIKES", "person" + rand.nextInt(personCount));
    }
}
