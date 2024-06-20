package com.wxq.sentinel.file;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.wxq.sentinel.common.datasource.AbstractWritableDataSource;
import com.wxq.sentinel.common.exception.SentinelException;
import com.wxq.sentinel.common.properties.FileConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.Lock;

/**
 * @author weixiaoqiang
 * @date 2024/5/11
 **/
public class FileWriteableDataSource<T> extends AbstractWritableDataSource<T, FileConfigProperties> {

    private static final Logger logger = LoggerFactory.getLogger(FileWriteableDataSource.class);

    private final File file;

    public FileWriteableDataSource(File file, Converter<T, String> parser, Lock writeLock, FileConfigProperties properties) {
        super(parser, writeLock, properties);
        this.file = file;
    }

    @Override
    public void write(T value) throws SentinelException {
        writeLock.lock();
        try {
            String convertResult = converter.convert(value);
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
                byte[] bytesArray = convertResult.getBytes(StandardCharsets.UTF_8);
                logger.info("[FileWritableDataSource] Writing to file {}: {}", file.getName(), convertResult);
                outputStream.write(bytesArray);
                outputStream.flush();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (Exception ignore) {
                        // nothing
                    }
                }
            }
        } catch (Exception e) {
            throw new SentinelException("Write config failed", e);
        } finally {
            writeLock.unlock();
        }
    }
}
