package com.seebie.server.scraper.keystone;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.seebie.server.entity.StoreItem;
import com.seebie.server.entity.Subspecies;


import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

@Component
public class EntityExtractor {

    private static Logger LOG = LoggerFactory.getLogger(EntityExtractor.class);

    // object mapper is thread safe
    private ObjectMapper mapper = new ObjectMapper();

    private Map<String, Subspecies> labelToEnum = new HashMap<>();

    private TypeReference<List<HashMap<String, String>>> parseType = new TypeReference<>() {};

    public EntityExtractor() {
        labelToEnum.put("Sativa",        Subspecies.SATIVA);
        labelToEnum.put("Sative-Hybrid", Subspecies.SATIVA_HYBRID);
        labelToEnum.put("Hybrid",        Subspecies.HYBRID);
        labelToEnum.put("Indica-Hybrid", Subspecies.INDICA_HYBRID);
        labelToEnum.put("Indica",        Subspecies.INDICA);
        labelToEnum.put("High-CBD",      Subspecies.HIGH_CBD);
    }

    public Optional<BigDecimal> parsePercentageNumber(String percentage) {
        if(percentage.isBlank()) {
            return Optional.of(BigDecimal.ZERO);
        }
        try {
            // DecimalFormat is not thread safe
            DecimalFormat decimalFormat = new DecimalFormat("0.0#%");
            decimalFormat.setParseBigDecimal(true);
            decimalFormat.setMaximumFractionDigits(3);
            decimalFormat.setMultiplier(1); // so the number comes out in units of percent
            return Optional.of((BigDecimal) decimalFormat.parse(percentage));
        }
        catch (ParseException e) {
            LOG.info("Could not parse percentage: " + percentage);
            return Optional.empty();
        }
    }

    public BigDecimal extractPercent(String key, HashMap<String, String> item) {
        return parsePercentageNumber(item.get(key)).orElse(BigDecimal.ZERO);
    }

    public String extractStrainFromStrainImg(String strainWithTags) {
        return Jsoup.clean(strainWithTags, Safelist.none()).trim();
    }

    public Subspecies extractSubspeciesFromStrainImg(String strain) {

        Document doc = Jsoup.parseBodyFragment(strain);

        return doc.getElementsByTag("img").stream()
                .map(element -> element.attr("alt"))
                .filter(alt -> labelToEnum.containsKey(alt))
                .map(labelToEnum::get)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Could not extract subspecies from Strain img"));
    }

    /**
     * price is a required field, so throw an exception if it doesn't parse (e.g. the field is empty).
     *
     * @param priceDollars
     * @return the dollars portion of a price decimal string.
     */
    public Long parsePrice(String priceDollars) {
        try {
            // DecimalFormat is not thread safe
            NumberFormat format = NumberFormat.getCurrencyInstance();
            format.setParseIntegerOnly(true);
            return (Long) format.parse(priceDollars);
        }
        catch(ParseException e) {
            throw new IllegalArgumentException("Could not parse price from " + priceDollars, e);
        }
    }

    /**
     * weight is a required field, so throw an exception if it doesn't parse (e.g. the field is empty).
     *
     * @param weight in grams
     * @return the numeric value in grams.
     */
    public BigDecimal parseGrams(String weight) {
        try {
            // DecimalFormat is not thread safe
            DecimalFormat decimalFormat = new DecimalFormat("0.0#g");
            decimalFormat.setParseBigDecimal(true);
            return (BigDecimal) decimalFormat.parse(weight);
        }
        catch(ParseException e) {
            throw new IllegalArgumentException("Could not parse weight from " + weight, e);
        }
    }

    public Optional<StoreItem> extractItem(HashMap<String, String> item) {

        try {
            StoreItem storeItem = new StoreItem();

            storeItem.setSubspecies(extractSubspeciesFromStrainImg(item.get("strain")));
            storeItem.setStrain(extractStrainFromStrainImg(item.get("strain")));
            storeItem.setThcPercent(parsePercentageNumber(item.get("thc")).get());
            storeItem.setCbdPercent(parsePercentageNumber(item.get("cbd")).get());
            storeItem.setBisabololPercent(extractPercent("bisabolol", item));
            storeItem.setCaryophyllenePercent(extractPercent("caryophyllene", item));
            storeItem.setHumulenePercent(extractPercent("humulene", item));
            storeItem.setLimonenePercent(extractPercent("limonene", item));
            storeItem.setLinaloolPercent(extractPercent("linalool", item));
            storeItem.setMyrcenePercent(extractPercent("myrcene", item));
            storeItem.setPinenePercent(extractPercent("pinene", item));
            storeItem.setTerpinolenePercent(extractPercent("terpinolene", item));
            storeItem.setPriceDollars(parsePrice(item.get("price")));
            storeItem.setVendor(item.get("vendor"));
            storeItem.setWeightGrams(parseGrams(item.get("wt")));

            return Optional.of(storeItem);
        }
        catch(Exception e) {
            System.out.println("Couldn't parse item " + item + ", exception message: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<StoreItem> extractItems(String unparsedData) {

        try {
            var parser = mapper.createParser(unparsedData);
            var items = mapper.readValue(parser, parseType);
            return items.stream()
                    .flatMap(item -> extractItem(item).stream())
                    .toList();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
