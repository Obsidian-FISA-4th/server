package Obsidian.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "custom")
@Getter
@Setter
public class CustomProperties {
	private String mode;
}
