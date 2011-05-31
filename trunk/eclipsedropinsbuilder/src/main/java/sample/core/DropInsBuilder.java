package sample.core;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import sample.startup.Main;

@Component
public class DropInsBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    @PostConstruct
    public void init() {
        LOGGER.debug("init");
    }

    public void build() {
        LOGGER.debug("build");
    }
}
