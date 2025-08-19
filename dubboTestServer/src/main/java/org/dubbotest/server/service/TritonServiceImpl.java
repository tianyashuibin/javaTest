package org.dubbotest.server.service;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import inference.GRPCInferenceServiceGrpc;
import inference.GrpcService;
import org.springframework.stereotype.Service;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

@Service
public class TritonServiceImpl {
    private static final String TRITON_SERVER_ADDR = "localhost";
    private static final int TRITON_SERVER_PORT = 8001;
    private ManagedChannel channel;
    private GRPCInferenceServiceGrpc.GRPCInferenceServiceBlockingStub blockingStub;

    public TritonServiceImpl() {
        this.channel = ManagedChannelBuilder.forAddress(TRITON_SERVER_ADDR, TRITON_SERVER_PORT).usePlaintext().build();
        this.blockingStub = GRPCInferenceServiceGrpc.newBlockingStub(channel);
    }

    public String predict() {
        int batchSize = 10;
//        int featureLength = 455;
        int featureLength = 513;
        List<Float> featureList = new ArrayList<>(featureLength * batchSize);
        for (int i = 0; i < featureLength * batchSize; i++) {
            featureList.add((float) Math.random());
        }
        GrpcService.ModelInferRequest.InferInputTensor inputTensor = GrpcService.ModelInferRequest.InferInputTensor.newBuilder()
                .setName("input__0")
                .setDatatype("FP32")
                .addShape(batchSize)  // 批大小
                .addShape(featureLength) // 序列长度
                .setContents(GrpcService.InferTensorContents.newBuilder().addAllFp32Contents(featureList))
                .build();

        GrpcService.ModelInferRequest request = GrpcService.ModelInferRequest.newBuilder()
//                .setModelName("gn_sjtjs_xgb_json")
                .setModelName("model_1")
                .addInputs(inputTensor)
                .build();

        GrpcService.ModelInferResponse response = blockingStub.modelInfer(request);
        ByteString rawOutputContents = response.getRawOutputContents(0);
        List<Float> fp32Results = parseFP32(rawOutputContents.toByteArray());
        return JSONObject.toJSONString(fp32Results);
    }

    public static List<Float> parseFP32(byte[] rawOutput) {
        // 创建ByteBuffer来处理字节数组，假设是小端模式
        ByteBuffer buffer = ByteBuffer.wrap(rawOutput).order(ByteOrder.LITTLE_ENDIAN);

        // 创建一个列表来存储解析出来的浮点数
        List<Float> floatList = new ArrayList<>();

        // 每4个字节解析为一个浮点数FP32
        while (buffer.remaining() >= 4) {
            float value = buffer.getFloat();
            floatList.add(value);
        }

        return floatList;  // 返回解析的浮点数列表
    }
}
