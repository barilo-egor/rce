package tgb.btc.rce.service;

import com.google.common.collect.Lists;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public interface PropertiesReader {

    Logger logger = LoggerFactory.getLogger(PropertiesReader.class);

    String getFileName();

    char getListDelimiter();


    default String getString(String key, String defaultValue) {
        PropertiesConfiguration instance = ReaderSupport.getInstance(this);
        return instance.getString(key, defaultValue);
    }

    default String getString(String key) {
        PropertiesConfiguration instance = ReaderSupport.getInstance(this);
        return instance.getString(key, null);
    }

    default String[] getStringArray(String key) {
        PropertiesConfiguration instance = ReaderSupport.getInstance(this);
        return StringUtils.split(StringUtils.trimToEmpty(getString(key)), this.getListDelimiter());
    }

    default List<String> getStringList(String key) {
        PropertiesConfiguration instance = ReaderSupport.getInstance(this);
        return instance.getList(key).stream().map(Object::toString).collect(Collectors.toList());
    }

    default boolean isNotBlank(String key) {
        return StringUtils.isNotBlank(getString(key));
    }

    default Integer getInteger(String key, Integer defaultValue) {
        String value = getString(key);
        if (StringUtils.isNotBlank(value) && StringUtils.isNumeric(value)) {
            return Integer.valueOf(value);
        }
        return defaultValue;
    }

    default Long getLong(String key, Long defaultValue) {
        String value = getString(key);
        if (StringUtils.isNotBlank(value) && StringUtils.isNumeric(value)) {
            return Long.valueOf(value);
        }
        return defaultValue;
    }

    default boolean isNotBlankSafely(String key) {
        try {
            return StringUtils.isNotBlank(getString(key));
        } catch (Exception e) {
            logger.error("Ошибки при вызове PropertiesEnum.isNotBlankSafely для " + getFileName(), e);
            return false;
        }
    }

    default Boolean getBoolean(String key, boolean defaultValue) {
        Boolean result = BooleanUtils.toBooleanObject(getString(key));
        return result != null ? result : defaultValue;
    }

    default Boolean getBoolean(String key) {
        return BooleanUtils.toBooleanObject(getString(key));
    }

    default void reload() {
        ReaderSupport.properties.remove(this);
        ReaderSupport.getInstance(this);
    }

    class ReaderSupport {
        private static final Map<PropertiesReader, PropertiesConfiguration> properties = new HashMap<>();

        private static PropertiesConfiguration getInstance(PropertiesReader propertiesReader) {
            PropertiesConfiguration instance = properties.get(propertiesReader);
            if (instance == null) {
                try {
                    instance = new PropertiesConfiguration();
                    instance.setFileName(propertiesReader.getFileName());
                    instance.setListDelimiter(propertiesReader.getListDelimiter());
                    instance.setDelimiterParsingDisabled(true);
                    instance.load();
                    instance.setReloadingStrategy(new FileChangedReloadingStrategy());
                    properties.put(propertiesReader, instance);
                } catch (ConfigurationException e) {
                    logger.error("Произошла ошибка при чтении параметров из " + propertiesReader.getFileName(), e);
                    throw new RuntimeException("Произошла ошибка при чтении параметров из " + propertiesReader.getFileName(), e);
                }
            }
            return instance;
        }
    }
}
