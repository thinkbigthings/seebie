package com.seebie.server;

import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * Some properties are not available through spring directly,
 * and spring can't reference any flyway.conf, so I can't bundle the config.
 *
 * Using a FlywayConfigurationCustomizer is the recommended approach to get full control over all flyway properties.
 * We don't necessarily want to mix Flyway up with production code, so this is not as safe as it could be.
 * The protection comes from flyway being enabled or disabled by profile.
 * And by running updates and migrations through a staging process which would quickly uncover misconfigurations.
 *
 * Possible future improvements:
 *  put flyway properties directly in application.properties, read them into config record, inject here, and set props
 *  set flyway enabled programmatically based on profile, so it would require a code change to mix it up.
 *
 * It would be ideal to have migrations in a separate subproject.
 * But running integration tests with the db is so compelling and the simplification of keeping things together is
 * so powerful. We haven't figured out a way yet to put the migrations in a separate db project and still have
 * easy integration testing with it.
 *
*/
@Configuration
public class FlywayConfig implements FlywayConfigurationCustomizer {

    @Override
    public void customize(FluentConfiguration fluent) {

        var flywayReplacementProps = new HashMap<String,String>();

        flywayReplacementProps.put("cleanDisabled", "false");

        // this is so we can run non-transactional migrations like CREATE INDEX CONCURRENTLY
        flywayReplacementProps.put("postgresql.transactional.lock", "false");

        fluent.configuration(flywayReplacementProps);
    }
}
