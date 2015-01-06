package org.thinkbigthings.boot.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Email;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

public class UserResource extends ResourceSupport {

    public static final String REL_SLEEP = "sleepresource";
    public static final String REL_ROLES = "roles";
    public static final String REL_PASSWORD_UPDATE = "password";

    @NotNull
    @Size(min = 3, message = "must be at least three characters")
    @Email(message = "Name must be in email format")
    private final String username;

    @NotNull
    @Size(min = 3, message = "must be at least three characters")
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
        this(name, display, Arrays.asList(selfLink, appendProperty(selfLink, REL_SLEEP), appendProperty(selfLink, REL_ROLES), appendProperty(selfLink, REL_PASSWORD_UPDATE)));
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
