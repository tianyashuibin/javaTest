package org.dubbotest.server.conf.localconfig;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.dubbo.common.utils.ConcurrentHashSet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JsonFileWatcher {
    private final String XGB_TO_TRITON_MODELS = "xgb_to_triton_models";

    private final String filePath;
    private String lastMd5;
    private JSONObject jsonData;

    private Set<String> xgbToTritonModels = new ConcurrentHashSet<>();

    public JsonFileWatcher(String filePath) {
        this.filePath = filePath;
    }

    // 计算文件的 MD5 值
    private String calculateMD5() throws IOException, NoSuchAlgorithmException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // 读取文件内容并解析为 JSONObject
    private JSONObject loadJsonFromFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            return JSON.parseObject(content.toString());
        }
    }

    // 检查文件是否变更，若变更则重新加载
    public void checkAndReload() {
        try {
            String currentMd5 = calculateMD5();

            if (lastMd5 == null || !lastMd5.equals(currentMd5)) {
                System.out.println("文件已变更，正在重新加载...");

                JSONObject newJson = loadJsonFromFile();
                synchronized (this) {
                    this.jsonData = newJson;
                    this.lastMd5 = currentMd5;
                }

                System.out.println("文件加载成功，当前 JSON: " + jsonData);

                parseXgbToTritonModels();
            }
            // else 可选：输出“无变更”
            // else { System.out.println("文件未变更，无需加载"); }

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // 获取当前加载的 JSON 数据（线程安全）
    public JSONObject getJsonData() {
        synchronized (this) {
            return jsonData != null ? jsonData.clone() : null; // clone 可选，防止外部修改
        }
    }

    // 启动定时检查（例如每 5 秒检查一次）
    public void startWatching(long intervalSeconds) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::checkAndReload, 0, intervalSeconds, TimeUnit.SECONDS);
    }

    // 手动触发一次加载（可用于初始化）
    public void load() {
        checkAndReload();
    }

    private void parseXgbToTritonModels() {
        JSONArray xgbToTritonModelsArray = jsonData.getJSONArray(XGB_TO_TRITON_MODELS);
        if (xgbToTritonModelsArray != null) {
            Set<String> xgbToTritonModelsTmp = new ConcurrentHashSet<>();
            for (Object model : xgbToTritonModelsArray) {
                xgbToTritonModelsTmp.add(model.toString());
            }
            xgbToTritonModels = xgbToTritonModelsTmp;
            System.out.println("已加载的模型：" + xgbToTritonModels);
        }
    }

    public boolean isXgbToTritonModel(String modelName) {
        return xgbToTritonModels.contains(modelName);
    }


    // 示例主函数
    public static void main(String[] args) throws InterruptedException {
        String filePath = "dubboTestServer/src/main/resources/local_config.json"; // 替换为你的文件路径

        JsonFileWatcher watcher = new JsonFileWatcher(filePath);

        // 首次加载
        watcher.load();

        // 启动后台监控，每 5 秒检查一次
        watcher.startWatching(2);

        // 模拟主线程运行
//        try {
//            Thread.currentThread().join();
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }

        while (true) {
            Thread.sleep(2000);
            System.out.println("当前 JSON 数据：" + watcher.getJsonData());
            System.out.println("当前模型：" + watcher.isXgbToTritonModel("model_1"));
        }
    }
}
