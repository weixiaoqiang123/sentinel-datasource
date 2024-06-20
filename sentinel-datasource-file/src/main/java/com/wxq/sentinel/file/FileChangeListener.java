package com.wxq.sentinel.file;

import com.alibaba.csp.sentinel.log.RecordLog;
import com.wxq.sentinel.common.exception.SentinelException;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;

/**
 * @author weixiaoqiang
 * @date 2024/5/11
 **/
public class FileChangeListener implements Runnable {

    private Lock readLock;

    private CountDownLatch lock;

    private WatchService watcher;

    private FileReadableDataSource dataSource;

    public FileChangeListener(CountDownLatch lock, Lock readLock, String path, FileReadableDataSource dataSource) {
        this.lock = lock;
        this.readLock = readLock;
        this.watcher = getWatcher();
        this.dataSource = dataSource;
        registerWatcher(path);
    }

    @Override
    public void run() {
        // 线程准备完毕 开始监听文件改动
        lock.countDown();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // 阻塞，直到有事件发生
                WatchKey key = watcher.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    // 上下文是目录条目
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue; // 事件丢失或丢弃
                    }
                    if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        readLock.lock();
                        RecordLog.info("[{}]File changes, update config to client", fileName);
                        try {
                            // dataSource.updateConfig();
                            RecordLog.info("update config successful: {}", fileName);
                        } catch (SentinelException e) {
                            RecordLog.error("update config failure: {}", fileName, e);
                        } finally {
                            readLock.unlock();
                        }
                    }
                }

                // 重置key，以便接收进一步的事件
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            RecordLog.warn("The thread has been interrupted", e);
        }
    }

    private void registerWatcher(String path) {
        Path filePath = Paths.get(path);
        Path dir = filePath.getParent();
        // 注册要监听的事件类型
        try {
            dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            throw new SentinelException("Register watcher failed", e);
        }
    }

    private WatchService getWatcher() {
        try {
            return FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new SentinelException("Failed to initialize the file listener", e);
        }
    }

}
