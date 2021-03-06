package com.plexsupply;

import com.plexsupply.property.FileProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		FileProperties.class
})
public class PlexsupplyApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlexsupplyApplication.class, args);
	}

}
