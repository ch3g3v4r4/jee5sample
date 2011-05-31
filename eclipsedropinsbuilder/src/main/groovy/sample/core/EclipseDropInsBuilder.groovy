package sample.core

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import sample.startup.Main;

@Component
class EclipseDropInsBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(EclipseDropInsBuilder.class);

    @PostConstruct
    public void init() {
        LOGGER.debug("EclipseDropInsBuilder.init");
    }
    public void build() {
        LOGGER.debug("EclipseDropInsBuilder.build");
    }
}
