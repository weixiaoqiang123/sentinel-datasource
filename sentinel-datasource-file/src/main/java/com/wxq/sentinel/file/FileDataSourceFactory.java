package com.wxq.sentinel.file;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import com.wxq.sentinel.common.datasource.DataSourceFactory;
import com.wxq.sentinel.common.exception.SentinelException;
import com.wxq.sentinel.common.properties.FileConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;


/**
 * @author weixiaoqiang
 * @date 2024/5/11
 **/
public class FileDataSourceFactory extends DataSourceFactory<FileConfigProperties> {

    private static Logger logger = LoggerFactory.getLogger(FileDataSourceFactory.class);

    private static final String RULE_DIR = "rules";

    private final File file;

    public FileDataSourceFactory(String ruleName, Object properties) {
        super(ruleName, (FileConfigProperties) properties);
        String rootDir = getRootDirectory(this.properties.getLocation());
        File ruleDir = new File(rootDir + RULE_DIR);
        if(!ruleDir.exists()) {
            ruleDir.mkdirs();
        }
        file = createFileIfNotExists(ruleDir.getPath(), ruleName, this.properties.getExtension());
    }

    private File createFileIfNotExists(String dir, String fileName, String extension) {
        String fullFileName = fileName + "." + extension;
        String filePath = dir + File.separator + fullFileName;
        File file = new File(filePath);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new SentinelException("Create file failed, filename: " + fullFileName);
            }
            logger.info("create file successful, path: " + filePath);
        }
        return file;
    }

    private String getRootDirectory(String location) {
        if(location.startsWith("classpath:/")) {
            return this.getClass().getClassLoader().getResource("").getPath();
        }
        throw new SentinelException("Unsupported file location");
    }

    @Override
    public WritableDataSource<FileConfigProperties> createWritableDataSource(Converter converter) {
        return new FileWriteableDataSource<>(file, converter, writeLock, properties);
    }

    @Override
    public ReadableDataSource<String, FileConfigProperties> createReadableDataSource(Converter parser) {
        return new FileReadableDataSource<>(file, parser, readLock, properties);
    }
}
