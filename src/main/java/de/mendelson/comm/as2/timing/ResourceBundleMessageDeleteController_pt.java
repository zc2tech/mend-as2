//$Header: /as2/de/mendelson/comm/as2/timing/ResourceBundleMessageDeleteController_pt.java 2     9/12/24 16:03 Heller $
package de.mendelson.comm.as2.timing;

import de.mendelson.util.MecResourceBundle;
/*
* Copyright (C) mendelson-e-commerce GmbH Berlin Germany
*
* This software is subject to the license agreement set forth in the license.
* Please read and agree to all terms before using this software.
* Other product and brand names are trademarks of their respective owners.
*/

/**
* ResourceBundle to localize a mendelson product
* @author S.Heller
* @version $Revision: 2 $
*/
public class ResourceBundleMessageDeleteController_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"transaction.deleted.user", "{0} Transacções canceladas por interação do utilizador"},
		{"transaction.deleted.system", "Transacções eliminadas pelo processo de manutenção do sistema"},
		{"delete.skipped", "APAGAR SKIPPED"},
		{"delete.ok", "ELIMINAR COM ÊXITO"},
		{"autodelete", "{0}: Esta mensagem é mais antiga do que {1} {2} e foi automaticamente eliminada pelo processo de manutenção do sistema."},
		{"delete.failed", "ELIMINAÇÃO FALHADA"},
		{"transaction.deleted.transactiondate", "Data da transação: {0}"},
		{"transaction.delete.setting.olderthan", "O processo está configurado para eliminar transacções com um estado verde que sejam mais antigas do que {0}."},
	};
}
