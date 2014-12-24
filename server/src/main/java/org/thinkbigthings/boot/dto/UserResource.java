package org.thinkbigthings.boot.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.List;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

public class UserResource extends ResourceSupport {
    public static final String REL_SLEEP = "sleepresource";
    public static final String REL_ROLES = "roles";
    private final String username;
    private final String displayName;

    @JsonCreator
    protected UserResource( @JsonProperty(value = "username") String name, 
                            @JsonProperty(value = "displayName") String display,
                            @JsonProperty(value = "links") List<Link> links) 
    {
        username = name;
        displayName = display;
        add(links);
    }

    public UserResource(String name, String display, Link selfLink) {
        this(name, display, Arrays.asList(selfLink, appendProperty(selfLink, REL_SLEEP), appendProperty(selfLink, REL_ROLES)));
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public static Link appendProperty(Link link, String property) {
        return new Link(link.getHref() + "/" + property, property);
    }
    
}
