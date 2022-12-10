package org.thinkbigthings.zdd.server.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thinkbigthings.zdd.server.service.ItemService;
import org.thinkbigthings.zdd.server.scraper.keystone.Item;


@RestController
public class ItemController {

    private final ItemService itemService;

    // if there's only one constructor, can omit Autowired and Inject
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value="/item", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<Item> getItems(@PageableDefault(page = 0, size = 10, sort = {"strain"}, direction=Sort.Direction.ASC) Pageable page) {

        return itemService.findItems(page);
    }

}