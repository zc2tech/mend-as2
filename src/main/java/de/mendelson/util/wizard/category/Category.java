//$Header: /as2/de/mendelson/util/wizard/category/Category.java 3     2/11/23 14:03 Heller $
package de.mendelson.util.wizard.category;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Object that stores the contents of a category. A category is a tab in the
 * JDialogCategory panel
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class Category{
    
    private final List<Subcategory> subcategoryList = new ArrayList<Subcategory>();
    
    /**The categories title
     */
    private String title = null;
    
    public Category(){
    }
    
    public Subcategory[] getSubcategories(){
        Subcategory[] subcategories = new Subcategory[this.subcategoryList.size()];
        this.subcategoryList.toArray( subcategories );
        return( subcategories );
    }
    
    public void addSubcategory( Subcategory subcategory ){
        this.subcategoryList.add( subcategory );
    }
    
    public String getTitle(){
        return( this.title );
    }
    
    public void setTitle( String title ){
        this.title = title;
    }
    
}