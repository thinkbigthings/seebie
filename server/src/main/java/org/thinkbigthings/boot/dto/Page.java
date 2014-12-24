package org.thinkbigthings.boot.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.PageImpl;

/**
 * This class only exists because PageImpl couldn't be deserialized by a JSON parser.
 * @param <T> 
 */
public class Page<T> extends PageImpl<T> {
    
    public static final long serialVersionUID = 1L;
    
    public Page() {
        super(new ArrayList<>());
    }

    @JsonCreator
    public Page(@JsonProperty("content") List<T> content) {
        super(content);
    }
}
