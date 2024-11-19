package tgb.btc.rce.service.impl.module;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.library.service.properties.ModulesPropertiesReader;
import tgb.btc.rce.enums.ReviewPriseType;

import java.util.Objects;

@Service
@Slf4j
public class ReviewPriseModule implements IModule<ReviewPriseType> {

    private ReviewPriseType current;

    private ModulesPropertiesReader modulesPropertiesReader;

    @Autowired
    public void setModulesPropertiesReader(ModulesPropertiesReader modulesPropertiesReader) {
        this.modulesPropertiesReader = modulesPropertiesReader;
    }

    @Override
    public ReviewPriseType getCurrent() {
        if (Objects.nonNull(current))
            return current;
        String type = modulesPropertiesReader.getString("review.prise", ReviewPriseType.STANDARD.name());
        try {
            ReviewPriseType reviewPriseType = ReviewPriseType.valueOf(type);
            current = reviewPriseType;
            return reviewPriseType;
        } catch (IllegalArgumentException e) {
            String message = "В проперти review.prise из modules.properties установлено невалидное значение.";
            log.error(message);
            throw new BaseException(message, e);
        }
    }

    @Override
    public void set(ReviewPriseType reviewPriseType) {
        modulesPropertiesReader.setProperty("review.prise", reviewPriseType.name());
        current = reviewPriseType;
    }
}
