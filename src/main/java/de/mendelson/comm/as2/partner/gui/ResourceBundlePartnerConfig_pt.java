//$Header: /as2/de/mendelson/comm/as2/partner/gui/ResourceBundlePartnerConfig_pt.java 2     9/12/24 16:02 Heller $
package de.mendelson.comm.as2.partner.gui;

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
public class ResourceBundlePartnerConfig_pt extends MecResourceBundle {

	private static final long serialVersionUID = 1L;

	@Override
	public Object[][] getContents() {
		return CONTENTS;
	}
	/**List of messages in the specific language*/
	private static final Object[][] CONTENTS = {
		{"dialog.partner.deletedir.message", "O parceiro \"{0}\" foi eliminado da configuração. Se o diretório associado\n\"{1}\"\ndeve ser eliminado do disco rígido?"},
		{"event.partner.modified.subject", "O parceiro {0} foi modificado pelo utilizador"},
		{"directory.delete.failure", "O diretório \"{0}\" não pôde ser eliminado: [\"{1}\"]"},
		{"title", "Configuração de parceiros"},
		{"localstation.noprivatekey.title", "Sem chave privada"},
		{"text.configurationproblem", "<HTML>Há erros na configuração do parceiro - corrija-os antes de os guardar.</HTML>"},
		{"dialog.partner.delete.title", "Eliminar um parceiro"},
		{"button.delete", "Eliminar"},
		{"localstation.noprivatekey.message", "Deve ser atribuída uma chave privada à estação local."},
		{"dialog.partner.renamedir.message", "O parceiro \"{0}\" foi renomeado para \"{1}\". Se o diretório correspondente\n\"{2}\"\nno disco rígido deve ser renomeada?"},
		{"dialog.partner.delete.message", "Está prestes a eliminar o parceiro \"{0}\" da configuração de parceiros.\nTodos os dados do parceiro \"{0}\" serão perdidos.\n\nDeseja mesmo eliminar o parceiro \"{0}\"?"},
		{"nolocalstation.message", "Pelo menos um parceiro deve ser definido como uma estação local."},
		{"button.clone", "Cópia"},
		{"event.partner.modified.body", "Dados anteriores do parceiro:\n\n{0}\n\nNovos dados do parceiro:\n\n{1}"},
		{"button.new", "Novo"},
		{"directory.rename.failure", "O diretório \"{0}\" não pode ser renomeado para \"{1}\"."},
		{"event.partner.added.body", "Dados do novo parceiro:\n\n{0}"},
		{"nolocalstation.title", "Nenhuma estação local"},
		{"module.locked", "A administração de parceiros é aberta exclusivamente por outro cliente, não é possível guardar as alterações!"},
		{"event.partner.added.subject", "O parceiro {0} foi adicionado pelo utilizador da administração de parceiros"},
		{"button.cancel", "Cancelar"},
		{"dialog.partner.renamedir.title", "Mudar o nome do diretório de mensagens"},
		{"saving", "Guardar..."},
		{"button.globalchange", "Mundial"},
		{"button.ok", "Ok"},
		{"directory.delete.success", "O diretório \"{0}\" foi eliminado."},
		{"event.partner.deleted.subject", "O parceiro {0} foi eliminado da administração de parceiros pelo utilizador"},
		{"dialog.partner.deletedir.title", "Eliminar um diretório de mensagens"},
		{"event.partner.deleted.body", "Dados do parceiro eliminado:\n\n{0}"},
		{"directory.rename.success", "O diretório \"{0}\" foi renomeado para \"{1}\"."},
	};
}
