package sample.core;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import sample.startup.Main;

@Component
class EclipseDropInsBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(EclipseDropInsBuilder.class);

//    @PostConstruct
//    public void init() {
//        LOGGER.debug("EclipseDropInsBuilder.init");
//    }
    public void build() {

        // Director application
        def directorUrl = "http://mirror-fpt-telecom.fpt.net/eclipse/tools/buckminster/products/director_latest.zip"
        // Eclipse JEE zip file for x86_64
        def platformUrl = "http://mirror-fpt-telecom.fpt.net/eclipse/technology/epp/downloads/release/helios/SR2/eclipse-jee-helios-SR2-win32-x86_64.zip"
        // working firectory
        def targetDir = "/tmp/"

        def ant = new AntBuilder()
        ant.mkdir (dir: targetDir)
        ant.get (src: directorUrl, dest: targetDir, usetimestamp: true, verbose: true)
        ant.get (src: platformUrl, dest: targetDir, usetimestamp: true, verbose: true)

    }
}
