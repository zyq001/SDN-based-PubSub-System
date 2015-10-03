/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conform?ment aux dispositions de la Licence Publique G?n?rale GNU, telle que publi?e par la Free Software Foundation ; version 2 de la licence, ou encore (? votre choix) toute version ult?rieure.

Ce programme est distribu? dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans m?me la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de d?tail, voir la Licence Publique G?n?rale GNU .

Vous devez avoir re?u un exemplaire de la Licence Publique G?n?rale GNU en m?me temps que ce programme ; si ce n'est pas le cas, ?crivez ? la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs;

import org.w3c.dom.Element;


public class WXSDocumentation implements WXSThing {
    
    protected String source = null; // URI
    protected String lang = null; // xml:lang
    protected String valeur = null;
    
    
    public WXSDocumentation(final Element el) {
        if (el.getAttributeNode("source") != null)
            source = el.getAttribute("source");
        if (el.getAttributeNode("xml:lang") != null)
            lang = el.getAttribute("xml:lang");
        if (el.getFirstChild() != null)
            valeur = el.getFirstChild().getNodeValue();
    }
    
    public String getValeur() {
        return(valeur);
    }
}
