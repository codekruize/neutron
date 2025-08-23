package org.neutron.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
public class BuildVersion {

	public static String getVersion() {
		InputStream buildVersionInputStream = BuildVersion.class
				.getResourceAsStream("/META-INF/Neutron-build.version");
		if (buildVersionInputStream != null) {
			Properties projectProperties = new Properties();
			try {
				projectProperties.load(buildVersionInputStream);
				String version = projectProperties.getProperty("build.version");
				if (version != null) {
					String buildNumber = projectProperties.getProperty("build.buildNum");
					if (buildNumber != null) {
						version += "." + buildNumber;
					}
					return version;
				}
			} catch (IOException ignore) {
			} finally {
				IOUtils.closeQuietly(buildVersionInputStream);
			}
		}

		InputStream mavenDataInputStream = BuildVersion.class
				.getResourceAsStream("/META-INF/maven/org.neutron/neutron-javase/pom.properties");
		if (mavenDataInputStream != null) {
			Properties projectProperties = new Properties();
			try {
				projectProperties.load(mavenDataInputStream);
				String version = projectProperties.getProperty("version");
				if (version != null) {
					return version;
				}
			} catch (IOException ignore) {
			} finally {
				IOUtils.closeQuietly(mavenDataInputStream);
			}
		}
		return "n/a";
	}
}
