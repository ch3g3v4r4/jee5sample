package org.builder.eclipsebuilder.beans;

import org.builder.eclipsebuilder.beans.Configuration.BuildType;

import junit.framework.TestCase;

public class DownloadLinkUtilsTest extends TestCase {

    public void testParseDownloadLink1() throws Exception {
        String urlStr;
        Artifact artifact;

        urlStr = "http://download.eclipse.org/eclipse/downloads/drops/S-3.4M5-200802071530/index.php";
        artifact = DownloadLinkUtils.parseDownloadLink(urlStr);
        assertEquals(BuildType.STABLE, artifact.getBuildType());
        assertEquals("3.4M5", artifact.getVersion());
        assertEquals("200802071530", artifact.getBuildDate());
        assertNull(artifact.getArtifactId());
        assertNull(artifact.getFileName());
    }

    public void testParseDownloadLink21() throws Exception {
        String urlStr;
        Artifact artifact;

        urlStr = "http://download.eclipse.org/eclipse/downloads/drops/S-3.4M5-200802071530/download.php?dropFile=eclipse-SDK-3.4M5-win32.zip";
        artifact = DownloadLinkUtils.parseDownloadLink(urlStr);
        assertEquals(BuildType.STABLE, artifact.getBuildType());
        assertEquals("3.4M5", artifact.getVersion());
        assertEquals("200802071530", artifact.getBuildDate());
        assertEquals("eclipse-SDK", artifact.getArtifactId());
        assertEquals("eclipse-SDK-3.4M5-win32.zip", artifact.getFileName());
    }

    public void testParseDownloadLink22() throws Exception {
        String urlStr;
        Artifact artifact;

        urlStr = "http://www.eclipse.org/downloads/download.php?file=/eclipse/downloads/drops/S-3.4M5-200802071530/eclipse-SDK-3.4M5-win32.zip";
        artifact = DownloadLinkUtils.parseDownloadLink(urlStr);
        assertEquals(BuildType.STABLE, artifact.getBuildType());
        assertEquals("3.4M5", artifact.getVersion());
        assertEquals("200802071530", artifact.getBuildDate());
        assertEquals("eclipse-SDK", artifact.getArtifactId());
        assertEquals("eclipse-SDK-3.4M5-win32.zip", artifact.getFileName());

        urlStr = "http://www.eclipse.org/downloads/download.php?file=/webtools/downloads/drops/R3.0/S-3.0M5-20080218021547/wtp-sdk-S-3.0M5-20080218021547.zip";
        artifact = DownloadLinkUtils.parseDownloadLink(urlStr);
        assertEquals(BuildType.STABLE, artifact.getBuildType());
        assertEquals("3.0M5", artifact.getVersion());
        assertEquals("20080218021547", artifact.getBuildDate());
        assertEquals("wtp-sdk", artifact.getArtifactId());
        assertEquals("wtp-sdk-S-3.0M5-20080218021547.zip", artifact.getFileName());

    }


    public void testParseDownloadLink23() throws Exception {
        String urlStr;
        Artifact artifact;

        urlStr = "http://www.eclipse.org/downloads/download.php?file=/eclipse/downloads/drops/S-3.4M5-200802071530/eclipse-SDK-3.4M5-win32.zip&url=http://download.eclipse.org/eclipse/downloads/drops/S-3.4M5-200802071530/eclipse-SDK-3.4M5-win32.zip&mirror_id=1";
        artifact = DownloadLinkUtils.parseDownloadLink(urlStr);
        assertEquals(BuildType.STABLE, artifact.getBuildType());
        assertEquals("3.4M5", artifact.getVersion());
        assertEquals("200802071530", artifact.getBuildDate());
        assertEquals("eclipse-SDK", artifact.getArtifactId());
        assertEquals("eclipse-SDK-3.4M5-win32.zip", artifact.getFileName());
    }

    public void testParseDownloadLink3() throws Exception {
        String urlStr;
        Artifact artifact;

        urlStr = "http://download.eclipse.org/eclipse/downloads/drops/S-3.4M5-200802071530/checksum/eclipse-SDK-3.4M5-win32.zip.md5";
        artifact = DownloadLinkUtils.parseDownloadLink(urlStr);
        assertEquals(BuildType.STABLE, artifact.getBuildType());
        assertEquals("3.4M5", artifact.getVersion());
        assertEquals("200802071530", artifact.getBuildDate());
        assertEquals("eclipse-SDK", artifact.getArtifactId());
        assertEquals("eclipse-SDK-3.4M5-win32.zip.md5", artifact.getFileName());

        urlStr = "http://download.eclipse.org/modeling/emf/emf/downloads/drops/2.3.1/R200709252135/emf-sdo-xsd-SDK-2.3.1.zip.md5";
        artifact = DownloadLinkUtils.parseDownloadLink(urlStr);
        assertEquals(BuildType.RELEASE, artifact.getBuildType());
        assertEquals("2.3.1", artifact.getVersion());
        assertEquals("200709252135", artifact.getBuildDate());
        assertEquals("emf-sdo-xsd-SDK", artifact.getArtifactId());
        assertEquals("emf-sdo-xsd-SDK-2.3.1.zip.md5", artifact.getFileName());

        urlStr = "http://www.eclipse.org/downloads/download.php?file=/modeling/emf/emf/downloads/drops/2.3.2/M200802051830/emf-sdo-xsd-SDK-M200802051830.zip";
        artifact = DownloadLinkUtils.parseDownloadLink(urlStr);
        assertEquals("2.3.2", artifact.getVersion());
        assertEquals("200802051830", artifact.getBuildDate());
        assertEquals("emf-sdo-xsd-SDK", artifact.getArtifactId());
        assertEquals("emf-sdo-xsd-SDK-M200802051830.zip", artifact.getFileName());

    }


}
