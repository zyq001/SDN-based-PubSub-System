/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conform�ment aux dispositions de la Licence Publique G�n�rale GNU, telle que publi�e par la Free Software Foundation ; version 2 de la licence, ou encore (� votre choix) toute version ult�rieure.

Ce programme est distribu� dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans m�me la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de d�tail, voir la Licence Publique G�n�rale GNU .

Vous devez avoir re�u un exemplaire de la Licence Publique G�n�rale GNU en m�me temps que ce programme ; si ce n'est pas le cas, �crivez � la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs.copy;

import org.w3c.dom.Element;

import jaxe.Config;


/**
 * Repr�sente un �l�ment XML d�fini dans un sch�ma qui est r�f�renc� dans une autre configuration Jaxe.
 */
public class ElementExterne implements ToutElement {
    
    private Element ref;
    private Config cfg;
    
    public ElementExterne(final Element ref, final Config cfg) {
        this.ref = ref;
        this.cfg = cfg;
    }
    
    public Element getDOMElement() {
        return(ref);
    }
    
    public String getName() {
        return(cfg.nomElement(ref));
    }
    
    public String getNamespace() {
        return(cfg.espaceElement(ref));
    }
}
