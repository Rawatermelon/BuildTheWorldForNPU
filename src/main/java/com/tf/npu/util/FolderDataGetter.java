package com.tf.npu.util;


import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class FolderDataGetter<T> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final String dataPath;
    private final Class<T> tClass;
    private final ArrayList<T> data;


    public FolderDataGetter(String dataPathFolder, Class<T> tClass) {
        this.dataPath = dataPathFolder;
        this.tClass = tClass;
        data = new ArrayList<>(0);

        initialize();
    }

    private void initialize() {
        LOGGER.info("Preparing to get data from folder: {}", dataPath);
        try {
            // 获取类加载器
            ClassLoader classLoader = FolderDataGetter.class.getClassLoader();

            URL url = classLoader.getResource(dataPath);
            if (url != null && url.getProtocol().equals("file")) {
                // 文件系统模式
                try {
                    Path rootPath = Paths.get(url.toURI());
                    if (!rootPath.isAbsolute() || !Files.exists(rootPath)) {
                        throw new IOException("Invalid path: " + rootPath);
                    }

                    try (Stream<Path> paths = Files.walk(rootPath)) {
                        paths.filter(Files::isRegularFile)
                                .filter(p -> p.toString().endsWith(".json"))
                                .forEach(file -> {
                                    try {
                                        String resourcePath = dataPath + "/" + rootPath.relativize(file).toString().replace("\\", "/");
                                        data.add(new FileDataGetter<>(resourcePath, tClass).getData());
                                    } catch (Exception e) {
                                        // 记录单个文件处理失败，但不中断整个过程
                                        LOGGER.error("Failed to process file: {}, error: {}", file, e.getMessage());
                                    }
                                });
                    }
                } catch (URISyntaxException e) {
                    throw new IOException("Invalid URL format: " + url, e);
                } catch (IOException e) {
                    throw new IOException("Failed to access file system path: " + url, e);
                }
            } else {
                // JAR模式
                String path = dataPath;
                if (!path.endsWith("/")) {
                    path += "/";
                }
                
                // 获取当前类所在的JAR文件
                String classPath = FolderDataGetter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                classPath = java.net.URLDecoder.decode(classPath, "UTF-8");
                try (JarFile jarFile = new JarFile(classPath)) {
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();
                        
                        // 检查是否是需要的资源文件
                        if (entryName.startsWith(path) && entryName.endsWith(".json") && !entry.isDirectory()) {
                            try {
                                InputStream inputStream = classLoader.getResourceAsStream(entryName);
                                if (inputStream != null) {
                                    T item = new FileDataGetter<>(entryName, tClass).getData();
                                    data.add(item);
                                    inputStream.close();
                                } else {
                                    LOGGER.warn("Failed to load resource as stream: {}", entryName);
                                }
                            } catch (Exception e) {
                                LOGGER.error("Failed to load resource: {}", entryName, e);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to initialize FolderDataGetter for path: {}", dataPath, e);
            throw new RuntimeException(e);
        }
    }

    public List<T> getList() {
        return data;
    }

}