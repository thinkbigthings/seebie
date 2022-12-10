package org.thinkbigthings.zdd.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "saved_search")
public class SavedSearch {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, insertable = false, nullable = false)
    private Long id;

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "savedSearch", cascade = CascadeType.ALL, orphanRemoval=true)
    private Set<SearchParameter> searchParameters = new HashSet<>();

    @NotNull
    @ManyToOne
    @JoinColumn(name = "search_config_id")
    private SearchConfig searchConfig;

    protected SavedSearch() {}

    public SavedSearch(Collection<SearchParameter> parameters) {
        this.searchParameters.addAll(parameters);
        this.searchParameters.forEach(s -> s.setSavedSearch(this));
    }

    public Set<SearchParameter> getSearchParameters() {
        return searchParameters;
    }

    public void setSearchParameters(Set<SearchParameter> searchParameters) {
        this.searchParameters = searchParameters;
    }

    public SearchConfig getSearchConfig() {
        return searchConfig;
    }

    public void setSearchConfig(SearchConfig searchConfig) {
        this.searchConfig = searchConfig;
    }

}
