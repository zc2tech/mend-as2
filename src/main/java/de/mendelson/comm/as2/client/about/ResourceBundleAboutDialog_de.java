//$Header: /mec_as2/de/mendelson/comm/as2/client/about/ResourceBundleAboutDialog_de.java 4     20/03/25 14:45 Heller $ 
package de.mendelson.comm.as2.client.about;
import de.mendelson.util.MecResourceBundle;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/** 
 * ResourceBundle to localize gui entries
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class ResourceBundleAboutDialog_de extends MecResourceBundle{

  public Object[][] getContents() {
    return CONTENTS;
  }

  /**List of messages in the specific language*/
  private static final Object[][] CONTENTS = {
        
    {"title", "Über" },  
    {"builddate", "Versionsdatum: {0}" }, 
    {"button.ok", "Ok" },
    {"tab.about", "Version" },
    {"tab.license", "License" },
  };		
  
}