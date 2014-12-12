
package org.thinkbigthings.boot.assembler;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;


// TODO 3 open a ticket for unflattened resource in hateoas?
/**
 * The sole purpose for this class is to remove @JsonUnwrapped from the Spring HATEOAS Resource.
 * Otherwise any statically-typed client has a hard time de-serializing the resource and its content.
 * 
 * @param <T> type that this class wraps.
 */
@XmlRootElement
public class Resource<T extends Object> extends ResourceSupport {

    protected final T content;

    /** 
     * For serialization
     */
    protected Resource() {
        content = null;
    }

    public Resource(T newContent, Link[] links) {
        content = newContent;
        for(Link n : links) {
            add(n);
        }
    }

    public Resource(T newContent, Iterable<Link> links) {
        content = newContent;
        for(Link n : links) {
            add(n);
        }
    }

    @XmlAnyElement
    public T getContent() {
        return content;
    }

    @Override
    public String toString() {
        return content.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Resource) {
            Resource<T> rhs = (Resource<T>) obj;
            return new EqualsBuilder()
                    .appendSuper(super.equals(obj))
                    .append(content, rhs.content)
                    .isEquals();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(content).toHashCode();
    }
}
