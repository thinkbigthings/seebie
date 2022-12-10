package org.thinkbigthings.zdd.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "store")
public class Store {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, insertable = false, nullable = false)
    private Long id;

    @NotNull
    @Column(unique=true)
    private String name = "";

    @NotNull
    private String website = "";

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval=true)
    private Set<StoreItem> items = new HashSet<>();

    protected Store() {}

    public Store(String name, String website) {
        this.name = name;
        this.website = website;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Set<StoreItem> getItems() {
        return items;
    }

    public void setItems(Set<StoreItem> items) {
        this.items = items;
    }
}
