package org.jeecg.config;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * Flyway 数据库版本管理
 */
@Slf4j
@Configuration
public class FlywayConfig {

	@Autowired
	private DataSource dataSource;

	@PostConstruct
	public void migrate() {
		Flyway flyway = Flyway.configure()
				.dataSource(dataSource)
				.locations("db/migration")
				.baselineOnMigrate(true)
				.load();
		flyway.migrate();
	}
}
