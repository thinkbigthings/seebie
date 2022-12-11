package com.seebie.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import com.seebie.dto.ItemSearch;
import com.seebie.dto.SearchItemsInStores;
import com.seebie.dto.SearchParameter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SearchParsingTest {

    private ObjectMapper mapper = new ObjectMapper();

    private String savedSearchJson = """
        {
            "storeNames":["Keystone Devon"],
            "searches":[
                {
                    "parameters":[
                        {"field":"strain","operator":"=","value":"Cherry Diesel"}
                    ]
                },
                {
                    "parameters":[
                        {"field":"thcPercent","operator":">=","value":"20"},
                        {"field":"weightGrams","operator":"=","value":"1"}
                    ]
                }
            ]
        }
        """;

    @Test
    public void testWriteSearchJson() throws Exception {

        SearchItemsInStores userSearches = new SearchItemsInStores(
                List.of("Keystone Devon"),
                List.of(
                        new ItemSearch(List.of(
                                new SearchParameter("strain", "=", "Cherry Diesel"))),
                        new ItemSearch(List.of(
                                new SearchParameter("thcPercent", ">=", "20"),
                                new SearchParameter("weightGrams", "=", "1"))
                ))
        );

        String json = mapper.writeValueAsString(userSearches);
        System.out.println(json);
    }

    @Test
    public void testParseSearchJson() throws Exception {

        SearchItemsInStores userSearches = mapper.readValue(savedSearchJson, SearchItemsInStores.class);

        System.out.println(userSearches);
    }
}
