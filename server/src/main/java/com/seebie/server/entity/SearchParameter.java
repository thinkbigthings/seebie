package com.seebie.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "search_parameter")
public class SearchParameter {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, insertable = false, nullable = false)
    private Long id;

    @NotNull
    private String field = "";

    @NotNull
    @Column(name = "operator_id")
    @Enumerated(EnumType.ORDINAL)
    private Operator operator = Operator.EQ;

    @NotNull
    private String value = "";

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "saved_search_id", referencedColumnName = "id")
    private SavedSearch savedSearch;

    protected SearchParameter() {}

    public SearchParameter(String field, Operator operator, String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SavedSearch getSavedSearch() {
        return savedSearch;
    }

    public void setSavedSearch(SavedSearch savedSearch) {
        this.savedSearch = savedSearch;
    }
}
