package com.tf.npu.util;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileDataGetter<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson gson = new Gson();

    private final String dataPath;
    private final Class<T> tClass;

    private T data;

    public FileDataGetter(String dataPath, Class<T> tClass) {
        this.dataPath = dataPath;
        this.tClass = tClass;

        initialize();
    }

    private void initialize() {
        try {
            InputStream inputStream = FileDataGetter.class.getClassLoader().getResourceAsStream(dataPath);
            if (inputStream != null) {
                BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));
                boolean readerReady = fileReader.ready();
                if (readerReady){
                    JsonReader reader = new JsonReader(fileReader);
                    data = gson.fromJson(reader, tClass);
                    reader.close();
                }
                else {
                    LOGGER.info("Failed to get data from file: {}, Reason: File not exist", dataPath);
                }
            } else {
                LOGGER.error("Failed to get data from file: {}, Reason: File not found", dataPath);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to get data from file: {}, Reason: {}", dataPath, e.getMessage());
            throw new RuntimeException(e);
        }
    }
    public T getData() {
        return data;
    }
}