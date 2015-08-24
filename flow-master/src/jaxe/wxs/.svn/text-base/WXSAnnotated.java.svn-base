/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conformément aux dispositions de la Licence Publique Générale GNU, telle que publiée par la Free Software Foundation ; version 2 de la licence, ou encore (à votre choix) toute version ultérieure.

Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans même la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de détail, voir la Licence Publique Générale GNU .

Vous devez avoir reçu un exemplaire de la Licence Publique Générale GNU en même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


public abstract class WXSAnnotated implements WXSThing {
    
    protected WXSAnnotation annotation = null;
    
    
    public WXSAnnotation getAnnotation() {
        return(annotation);
    }
    
    protected void parseAnnotation(final Element el) {
        for (Node n = el.getFirstChild(); n != null; n=n.getNextSibling()) {
            if (n instanceof Element && "annotation".equals(n.getLocalName())) {
                annotation = new WXSAnnotation((Element)n);
                break;
            }
        }
    }
    
    public String getDocumentation() {
        if (annotation == null)
            return(null);
        return(annotation.getDocumentation());
    }
}
