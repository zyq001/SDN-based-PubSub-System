/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conform�ment aux dispositions de la Licence Publique G�n�rale GNU, telle que publi�e par la Free Software Foundation ; version 2 de la licence, ou encore (� votre choix) toute version ult�rieure.

Ce programme est distribu� dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans m�me la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de d�tail, voir la Licence Publique G�n�rale GNU .

Vous devez avoir re�u un exemplaire de la Licence Publique G�n�rale GNU en m�me temps que ce programme ; si ce n'est pas le cas, �crivez � la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs.copy;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class WXSAnnotation implements WXSThing {
    
    // (documentation*)
    protected List<WXSDocumentation> documentations;
    
    
    public WXSAnnotation(final Element el) {
        documentations = new ArrayList<WXSDocumentation>();
        for (Node n = el.getFirstChild(); n != null; n=n.getNextSibling()) {
            if (n instanceof Element && "documentation".equals(n.getLocalName())) {
                documentations.add(new WXSDocumentation((Element)n));
                break;
            }
        }
    }
    
    public String getDocumentation() {
        if (documentations == null)
            return(null);
        String s = null;
        for (WXSDocumentation doc: documentations) {
            if (doc.getValeur() != null) {
                if (s == null)
                    s = doc.getValeur();
                else
                    s += doc.getValeur();
            }
        }
        return(s);
    }
}
