package nl.tudelft.ewi.git;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Config {

	private final Properties properties;
	
	public Config() {
		this.properties = new Properties();
		reload();
	}
	
	public void reload() {
		try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/config.properties"))) {
			properties.load(reader);
		}
		catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	public int getHttpPort() {
		return Integer.parseInt(properties.getProperty("http.port", "8080"));
	}
	
	public String getGitoliteRepoUrl() {
		return properties.getProperty("gitolite.repo-url", "git@localhost:gitolite-admin");
	}

	public String getPassphrase() {
		return properties.getProperty("gitolite.passphrase", null);
	}

	public String getRepositoriesDirectory() {
		return properties.getProperty("gitolite.repositories");
	}

	public String getMirrorsDirectory() {
		return properties.getProperty("gitolite.mirrors");
	}
	
}
