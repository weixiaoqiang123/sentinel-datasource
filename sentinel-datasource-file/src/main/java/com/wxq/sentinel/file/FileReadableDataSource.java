package com.wxq.sentinel.file;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.wxq.sentinel.common.datasource.AbstractReadableDataSource;
import com.wxq.sentinel.common.exception.SentinelException;
import com.wxq.sentinel.common.properties.FileConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;

/**
 * @author weixiaoqiang
 * @date 2024/5/11
 **/
public class FileReadableDataSource<T> extends AbstractReadableDataSource<T, FileConfigProperties> {

    private static final Logger logger = LoggerFactory.getLogger(FileReadableDataSource.class);

    private final File file;

    private Thread fileChangeTask;

    public FileReadableDataSource(File file, Converter<String, T> parser, Lock readLock, FileConfigProperties properties) {
        super(parser, readLock, properties);
        this.file = file;
        initFileChangeListener();
        // 初始化加载
        updateConfig();
    }

    @Override
    public String readSource() throws SentinelException {
        if (!file.exists()) {
            // Will throw FileNotFoundException later.
            logger.warn("[FileRefreshableDataSource] File does not exist: {}", file.getAbsolutePath());
        }
        FileInputStream inputStream = null;
        // 1MB
        byte[] buffer = new byte[1024 * 1024];

        readLock.lock();
        try {
            inputStream = new FileInputStream(file);
            FileChannel channel = inputStream.getChannel();
            if (channel.size() > buffer.length) {
                throw new SentinelException(file.getAbsolutePath() + " file size=" + channel.size()
                        + ", is bigger than bufSize=" + buffer.length + ". Can't read");
            }
            int len = inputStream.read(buffer);
            if(len == -1) {
                return "[]";
            }
            return new String(buffer, 0, len, StandardCharsets.UTF_8);
        } catch (IOException e) {
           throw new SentinelException("Read file failed", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception ignore) {
                }
            }
            readLock.unlock();
        }
    }

    @Override
    public void close() throws SentinelException {
        // 优雅终止线程
        fileChangeTask.interrupt();
    }

    private void initFileChangeListener() {
        CountDownLatch lock = new CountDownLatch(1);
        fileChangeTask = new Thread(new FileChangeListener(lock, readLock, file.getPath(), this));
        fileChangeTask.setName(file.getName() + " Listener");
        fileChangeTask.start();
        // 等待线程准备就绪
        try {
            lock.await();
        } catch (InterruptedException ignore) {}
    }
}
