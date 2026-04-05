package de.mendelson.util.security.cert;
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
 * @version $Revision: 6 $
 */
public class ResourceBundleKeystoreStorage extends MecResourceBundle{
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {                
        {"error.save.notloaded", "Unable to save the keystore, it has not been loaded so far." },
        {"error.delete.notloaded", "Unable to delete the entry, the underlaying keystore has not been loaded so far." },
        {"error.readaccess", "Unable to read keystore: No read access allowed to \"{0}\"." },
        {"error.filexists", "Unable to read keystore: The keystore file \"{0}\" does not exist." },
        {"error.notafile", "Unable to read keystore: The keystore file \"{0}\" is not a file." },
        {"error.nodata", "Unable to read keystore: No data available" },
        {"error.empty", "Unable to read keystore: Keystore data length must not be 0" },
        {"error.save", "Unable to save the keystore data." },
        {"keystore.read.failure", "The system is unable to read the underlaying certificates. Error message: \"{0}\". Please ensure that you are using the correct keystore password."},
        {"moved.keystore.to.db", "Imported the keystore data from \"{0}\" into the system, the purpose is {1}. Existing keys/certificates have been deleted."},
        {"moved.keystore.reason.commandline", "The import was initiated by a command line parameter at the server start."},
        {"moved.keystore.reason.initial", "The import was performed because there exists currently no internal system keystore. This is an initial process."},
        {"moved.keystore.to.db.title", "Keystore file import ({0})"}    
    };
    
}