package org.thinkbigthings.zdd.server.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thinkbigthings.zdd.dto.*;
import org.thinkbigthings.zdd.server.service.StoreService;


@RestController
public class StoreController {

    private final StoreService storeService;

    // if there's only one constructor, can omit Autowired and Inject
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/store", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void createStore(@RequestBody StoreRecord newStore) {

        storeService.saveNewStore(newStore);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value="/store", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<StoreRecord> getStores(@PageableDefault(page = 0, size = 10, sort = {"name"}, direction=Sort.Direction.ASC) Pageable page) {

        return storeService.getStores(page);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="/store/scrape", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void scrapeStore() {

        storeService.scrapeStores();
    }
}