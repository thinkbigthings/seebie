package com.seebie.server.controller;

import com.seebie.dto.SearchItemsInStores;
import com.seebie.server.scraper.keystone.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thinkbigthings.zdd.dto.*;
import com.seebie.server.service.SearchConfigService;
import com.seebie.server.service.SearchService;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;


@RestController
public class SearchController {

    private static Logger LOG = LoggerFactory.getLogger(SearchController.class);

    private final SearchService searchService;
    private final SearchConfigService searchConfigService;

    // if there's only one constructor, can omit Autowired and Inject
    public SearchController(SearchService searchService, SearchConfigService searchConfigService) {
        this.searchService = searchService;
        this.searchConfigService = searchConfigService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/searchconfig", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SearchItemsInStores getSearchConfig(@PathVariable String username) {
        LOG.info("Getting search config for " + username);
        return searchConfigService.getSearches(username);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/searchconfig", method = PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void updateSearchConfig(@RequestBody SearchItemsInStores searchConfig, @PathVariable String username) {
        LOG.info("Updating search config for " + username);
        searchConfigService.updateSearchConfig(username, searchConfig);
    }

    /**
     * Using POST instead of GET since we want a more complex query and need a request body to do that.
     *
     * @param searchConfig
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value="/search", method = POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Item> search(@RequestBody SearchItemsInStores searchConfig) {
        return searchService.search(searchConfig);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') || #username == authentication.name")
    @RequestMapping(value="/user/{username}/search", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Item> runSavedSearch(@PathVariable String username) {
        return searchService.search(username);
    }
}