package de.mendelson.util.country;

import java.text.Collator;
import java.util.Locale;
import java.util.Objects;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Class to display a country in a list
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class DisplayCountry implements Comparable<DisplayCountry> {

    private final String countryCode;
    private final String displayString;

    public DisplayCountry(String countryCode) {
        this.countryCode = countryCode.toUpperCase();
        Locale locale = new Locale(Locale.getDefault().getLanguage(), countryCode);
        this.displayString = locale.getDisplayCountry() + " (" + countryCode + ")";
    }

    @Override
    public String toString() {
        return (this.displayString);
    }

    @Override
    public boolean equals(Object anObject) {
        if (anObject == this) {
            return (true);
        }
        if (anObject != null && anObject instanceof DisplayCountry) {
            DisplayCountry entry = (DisplayCountry) anObject;
            return (entry.getCountryCode().equals(this.getCountryCode()));
        }
        return (false);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.getCountryCode());
        return hash;
    }

    @Override
    public int compareTo(DisplayCountry displayCountry) {
        Collator collator = Collator.getInstance(Locale.getDefault());
        //include french and german special chars into the sort mechanism
        return (collator.compare(this.displayString, displayCountry.displayString));
    }

    /**
     * @return the countryCode
     */
    public String getCountryCode() {
        return countryCode;
    }

   

}
