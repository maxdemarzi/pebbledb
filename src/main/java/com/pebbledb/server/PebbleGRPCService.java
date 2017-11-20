package com.pebbledb.server;

import com.pebbledb.proto.GetRelationshipTypesRequest;
import com.pebbledb.proto.GetRelationshipTypesResponse;
import com.pebbledb.proto.PebbleServerGrpc;
import io.grpc.stub.StreamObserver;

public class PebbleGRPCService extends PebbleServerGrpc.PebbleServerImplBase {
    @Override
    public void getRelationshipTypes(GetRelationshipTypesRequest req, StreamObserver<GetRelationshipTypesResponse> responseObserver) {
        for (String relType : PebbleServer.graphs[0].getRelationshipTypes()) {
            GetRelationshipTypesResponse reply = GetRelationshipTypesResponse.newBuilder().setResponse(relType).build();
            responseObserver.onNext(reply);
        }
        responseObserver.onCompleted();
    }
}
