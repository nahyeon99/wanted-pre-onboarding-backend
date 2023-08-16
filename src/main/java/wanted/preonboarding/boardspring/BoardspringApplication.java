package wanted.preonboarding.boardspring;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BoardspringApplication {

	public static final String APPLICATION_LOCATIONS = "spring.config.location="
			+ "classpath:application.yml, " +
			" /home/ec2-user/app/application-prod-db.yml";

	public static void main(String[] args) {
		new SpringApplicationBuilder(BoardspringApplication.class)
				.properties(APPLICATION_LOCATIONS)
				.run(args);
	}
}
