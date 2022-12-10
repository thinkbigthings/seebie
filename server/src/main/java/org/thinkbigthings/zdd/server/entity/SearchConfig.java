package org.thinkbigthings.zdd.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "search_config")
public class SearchConfig {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne(fetch=FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    // use M2M table between searches and stores, do not cascade
    @ManyToMany
    @JoinTable(
            name = "search_config_store",
            joinColumns = { @JoinColumn(name = "search_config_id") },
            inverseJoinColumns = { @JoinColumn(name = "store_id") })
    private Set<Store> searchStores = new HashSet<>();

    @NotNull
    private Instant lastSearch = Instant.now();

    @Basic
    private boolean active = true;

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "searchConfig", cascade = CascadeType.ALL, orphanRemoval=true)
    private Set<SavedSearch> subSearches = new HashSet<>();

    public SearchConfig() {}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Store> getSearchStores() {
        return searchStores;
    }

    public void setSearchStores(Set<Store> searchStores) {
        this.searchStores = searchStores;
    }

    public Instant getLastSearch() {
        return lastSearch;
    }

    public void setLastSearch(Instant lastSearch) {
        this.lastSearch = lastSearch;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<SavedSearch> getSubSearches() {
        return subSearches;
    }

    public void setSubSearches(Set<SavedSearch> subSearches) {
        this.subSearches = subSearches;
    }
}
