package de.mendelson.util.security.cert;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.modulelock.LockClientInformation;
import de.mendelson.util.modulelock.message.ModuleLockRequest;
import de.mendelson.util.modulelock.message.ModuleLockResponse;
import de.mendelson.util.security.cert.clientserver.KeyCopyRequest;
import de.mendelson.util.security.cert.clientserver.KeyCopyResponse;
import de.mendelson.util.security.cert.gui.ResourceBundleCertificates;
import de.mendelson.util.uinotification.UINotification;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * This is the standard key copy handler that allows to copy keys/certificates
 * to the other keystore manager of the system
 *
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class DefaultKeyCopyHandler implements KeyCopyHandler {

    private final BaseClient baseClient;    
    private final String targetModuleLockId;
    private final int sourceKeystoreUsage;
    private final int targetKeystoreUsage;
    private final MecResourceBundle rb;

    /**
     *
     * @param baseClient
     * @param targetKeystoreUsage What purpose has the target keystore, e.g.
     * KeystoreStorageImplClientServer.KEYSTORE_USAGE_ENC_SIGN
     * @param sourceKeystoreUsage What purpose has the source keystore, e.g.
     * KeystoreStorageImplClientServer.KEYSTORE_USAGE_ENC_SIGN
     * @param targetModuleLockId The module lock String required to lock the
     * access to the target keystore, e.g. ModuleLock.MODULE_ENCSIGN_KEYSTORE.
     */
    public DefaultKeyCopyHandler(BaseClient baseClient, 
            int sourceKeystoreUsage, 
            int targetKeystoreUsage, 
            String targetModuleLockId) {
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCertificates.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.baseClient = baseClient;
        this.targetKeystoreUsage = targetKeystoreUsage;
        this.sourceKeystoreUsage = sourceKeystoreUsage;
        this.targetModuleLockId = targetModuleLockId;
    }

    @Override
    public void copyEntry(KeystoreCertificate sourceCertificate) throws Throwable {
        ModuleLockRequest lockRequest;
        ModuleLockResponse lockResponse;
        try {
            //try to lock the target keystore module for write access
            lockRequest = new ModuleLockRequest(this.targetModuleLockId, ModuleLockRequest.TYPE_SET);
            lockResponse = (ModuleLockResponse) this.baseClient.sendSync(lockRequest);            
            boolean hasLock = lockResponse.wasSuccessful();
            if (!hasLock) {
                LockClientInformation lockKeeper = lockResponse.getLockKeeper();
                UINotification.instance().addNotification(
                        null,
                        UINotification.TYPE_ERROR,
                        this.rb.getResourceString("module.locked.title"),
                        this.rb.getResourceString("module.locked.text",
                                new Object[]{
                                    this.targetModuleLockId,
                                    lockKeeper.getClientIP()
                                }));
                return;
            }
            KeyCopyRequest request = new KeyCopyRequest(
                    this.sourceKeystoreUsage, 
                    this.targetKeystoreUsage, 
                    sourceCertificate.getFingerPrintSHA1()
            );
            KeyCopyResponse response = (KeyCopyResponse)this.baseClient.sendSync(request);     
            if( response.getException() != null ){
                throw(response.getException());
            }
            UINotification.instance().addNotification(
                        null,
                        UINotification.TYPE_SUCCESS,
                        null,
                        this.rb.getResourceString("keycopy.success.text",
                                response.getUsedTargetAlias()));
        }catch( Throwable e){
            UINotification.instance().addNotification(e);
        } finally {
            //release the target module lock
            lockRequest = new ModuleLockRequest(targetModuleLockId, ModuleLockRequest.TYPE_RELEASE);
            lockResponse = (ModuleLockResponse) this.baseClient.sendSync(lockRequest);
        }
    }
    
}
