/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conformément aux dispositions de la Licence Publique Générale GNU, telle que publiée par la Free Software Foundation ; version 2 de la licence, ou encore (à votre choix) toute version ultérieure.

Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans même la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de détail, voir la Licence Publique Générale GNU .

Vous devez avoir reçu un exemplaire de la Licence Publique Générale GNU en même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs;

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
