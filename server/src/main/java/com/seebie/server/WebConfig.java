package com.seebie.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * Spring will transform the Page or PageImpl instances into a more stable and predictable
 * JSON structure (using PagedModel) before serializing the response.
 * We can remove this config if we use PagedResourcesAssembler to generate and return PagedModel
 * instances in our controllers.
 */
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig {

}
