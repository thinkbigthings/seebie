package com.seebie.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "store_item")
public class StoreItem {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, insertable = false, nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    private Store store;

    @Column(name = "subspecies_id")
    @Enumerated(EnumType.ORDINAL)
    private Subspecies subspecies;

    @NotNull
    private String strain = "";

    @NotNull
    private BigDecimal thcPercent;

    @NotNull
    private BigDecimal cbdPercent;


    @NotNull private BigDecimal bisabololPercent;

    @NotNull private BigDecimal caryophyllenePercent;

    @NotNull private BigDecimal humulenePercent;

    @NotNull private BigDecimal limonenePercent;

    @NotNull private BigDecimal linaloolPercent;

    @NotNull private BigDecimal myrcenePercent;

    @NotNull private BigDecimal pinenePercent;

    @NotNull private BigDecimal terpinolenePercent;


    @NotNull
    private BigDecimal weightGrams;

    @NotNull
    private Long priceDollars;

    @NotNull
    private String vendor = "";

    @Basic
    @NotNull
    private Instant added;

    public StoreItem() {

    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Subspecies getSubspecies() {
        return subspecies;
    }

    public void setSubspecies(Subspecies subspecies) {
        this.subspecies = subspecies;
    }

    public String getStrain() {
        return strain;
    }

    public void setStrain(String strain) {
        this.strain = strain;
    }

    public BigDecimal getThcPercent() {
        return thcPercent;
    }

    public void setThcPercent(BigDecimal thcPercent) {
        this.thcPercent = thcPercent;
    }

    public BigDecimal getCbdPercent() {
        return cbdPercent;
    }

    public void setCbdPercent(BigDecimal cbdPercent) {
        this.cbdPercent = cbdPercent;
    }

    public BigDecimal getWeightGrams() {
        return weightGrams;
    }

    public void setWeightGrams(BigDecimal weightGrams) {
        this.weightGrams = weightGrams;
    }

    public Long getPriceDollars() {
        return priceDollars;
    }

    public void setPriceDollars(Long priceDollars) {
        this.priceDollars = priceDollars;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public Instant getAdded() {
        return added;
    }

    public void setAdded(Instant updated) {
        this.added = updated;
    }

    public BigDecimal getBisabololPercent() {
        return bisabololPercent;
    }

    public void setBisabololPercent(BigDecimal bisabololPercent) {
        this.bisabololPercent = bisabololPercent;
    }

    public BigDecimal getCaryophyllenePercent() {
        return caryophyllenePercent;
    }

    public void setCaryophyllenePercent(BigDecimal caryophyllenePercent) {
        this.caryophyllenePercent = caryophyllenePercent;
    }

    public BigDecimal getHumulenePercent() {
        return humulenePercent;
    }

    public void setHumulenePercent(BigDecimal humulenePercent) {
        this.humulenePercent = humulenePercent;
    }

    public BigDecimal getLimonenePercent() {
        return limonenePercent;
    }

    public void setLimonenePercent(BigDecimal limonenePercent) {
        this.limonenePercent = limonenePercent;
    }

    public BigDecimal getLinaloolPercent() {
        return linaloolPercent;
    }

    public void setLinaloolPercent(BigDecimal linaloolPercent) {
        this.linaloolPercent = linaloolPercent;
    }

    public BigDecimal getMyrcenePercent() {
        return myrcenePercent;
    }

    public void setMyrcenePercent(BigDecimal myrcenePercent) {
        this.myrcenePercent = myrcenePercent;
    }

    public BigDecimal getPinenePercent() {
        return pinenePercent;
    }

    public void setPinenePercent(BigDecimal pinenePercent) {
        this.pinenePercent = pinenePercent;
    }

    public BigDecimal getTerpinolenePercent() {
        return terpinolenePercent;
    }

    public void setTerpinolenePercent(BigDecimal terpinolenePercent) {
        this.terpinolenePercent = terpinolenePercent;
    }
}
