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


public class WXSList extends WXSAnnotated {
    
    protected WXSSimpleType simpleType = null;
    protected String itemType = null;
    
    protected Element domElement;
    
    
    public WXSList(final Element el, final WXSSchema schema) {
        parseAnnotation(el);
        for (Node n = el.getFirstChild(); n != null; n=n.getNextSibling()) {
            if (n instanceof Element && "simpleType".equals(n.getLocalName())) {
                simpleType = new WXSSimpleType((Element)n, null, schema);
                break;
            }
        }
        if (el.getAttributeNode("itemType") != null)
            itemType = el.getAttribute("itemType");
        
        domElement = el;
    }
    
    public void resoudreReferences(final WXSSchema schema, final WXSThing redefine) {
        if (simpleType != null)
            simpleType.resoudreReferences(schema, redefine);
        if (itemType != null && simpleType == null) {
            final String tns = domElement.lookupNamespaceURI(JaxeWXS.prefixeNom(itemType));
            final WXSType wxsType = schema.resoudreReferenceType(JaxeWXS.valeurLocale(itemType), tns, redefine);
            if (wxsType instanceof WXSSimpleType)
                simpleType = (WXSSimpleType)wxsType;
            else {
                final String espaceSchema = domElement.getNamespaceURI();
                if (!espaceSchema.equals(tns))
                    itemType = null; // si le type n'a pas été résolu il doit être un type des schémas XML
            }
        }
    }
    
    public boolean validerValeur(final String valeur) {
        if (simpleType == null && itemType == null)
            return(false);
        if (valeur == null)
            return(false);
        final String[] items = valeur.trim().split("\\s");
        for (String item : items) {
            if (simpleType != null) {
                if (!simpleType.validerValeur(item))
                    return(false);
            } else {
                if (!WXSSimpleType.validerValeur(JaxeWXS.valeurLocale(itemType), item))
                    return(false);
            }
        }
        return(true);
    }
}
