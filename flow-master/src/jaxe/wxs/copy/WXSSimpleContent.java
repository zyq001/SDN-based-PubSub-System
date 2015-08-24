/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conform�ment aux dispositions de la Licence Publique G�n�rale GNU, telle que publi�e par la Free Software Foundation ; version 2 de la licence, ou encore (� votre choix) toute version ult�rieure.

Ce programme est distribu� dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans m�me la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de d�tail, voir la Licence Publique G�n�rale GNU .

Vous devez avoir re�u un exemplaire de la Licence Publique G�n�rale GNU en m�me temps que ce programme ; si ce n'est pas le cas, �crivez � la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs.copy;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class WXSSimpleContent extends WXSAnnotated {
    
    // (restriction|extension)
    protected WXSRestriction restriction = null;
    protected WXSExtension extension = null;
    
    
    public WXSSimpleContent(final Element el, final WXSSchema schema) {
        parseAnnotation(el);
        for (Node n = el.getFirstChild(); n != null; n=n.getNextSibling()) {
            if (n instanceof Element) {
                if ("restriction".equals(n.getLocalName()))
                    restriction = new WXSRestriction((Element)n, null, schema);
                else if ("extension".equals(n.getLocalName()))
                    extension = new WXSExtension((Element)n, null, schema);
            }
        }
    }
    
    public void resoudreReferences(final WXSSchema schema, final WXSThing redefine) {
        if (restriction != null)
            restriction.resoudreReferences(schema, redefine);
        else if (extension != null)
            extension.resoudreReferences(schema, redefine);
    }
    
    public ArrayList<String> listeValeurs() {
        if (restriction != null)
            return(restriction.listeValeurs());
        else if (extension != null)
            return(extension.listeValeurs());
        return(null);
    }
    
    public ArrayList<WXSAttribute> listeAttributs() {
        if (restriction != null)
            return(restriction.listeAttributs());
        else if (extension != null)
            return(extension.listeAttributs());
        return(new ArrayList<WXSAttribute>());
    }
    
    public boolean validerValeur(final String valeur) {
        if (restriction != null)
            return(restriction.validerValeur(valeur));
        if (extension != null)
            return(extension.validerValeur(valeur));
        return(false);
    }
    
}
