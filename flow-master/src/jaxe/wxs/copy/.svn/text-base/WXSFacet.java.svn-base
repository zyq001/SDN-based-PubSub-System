/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conform�ment aux dispositions de la Licence Publique G�n�rale GNU, telle que publi�e par la Free Software Foundation ; version 2 de la licence, ou encore (� votre choix) toute version ult�rieure.

Ce programme est distribu� dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans m�me la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de d�tail, voir la Licence Publique G�n�rale GNU .

Vous devez avoir re�u un exemplaire de la Licence Publique G�n�rale GNU en m�me temps que ce programme ; si ce n'est pas le cas, �crivez � la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs.copy;

import org.w3c.dom.Element;


public class WXSFacet extends WXSAnnotated {
    
    protected String facet; // (minExclusive|minInclusive|maxExclusive|maxInclusive|totalDigits|fractionDigits|length|minLength|maxLength|enumeration|pattern)
    // A FAIRE: ajouter whiteSpace (avant pattern)
    protected String value = null;
    protected boolean fixed = false;
    
    protected int iparam = 0;
    
    
    public WXSFacet(final Element el) {
        parseAnnotation(el);
        facet = el.getLocalName();
        if (el.getAttributeNode("value") != null) {
            value = el.getAttribute("value");
            try {
                iparam = Integer.parseInt(value);
            } catch (final NumberFormatException ex) {
            }
            if ("pattern".equals(facet)) {
                // remplacements tr�s approximatifs de \i, \I, \c et \C
                value = value.replace("\\i", "[^<>&#!/?'\",0-9.\\-\\s]");
                value = value.replace("\\I", "[^a-zA-Z]");
                value = value.replace("\\c", "[^<>&#!/?'\",\\s]");
                value = value.replace("\\C", "\\W");
            }
        }
        if (el.getAttributeNode("fixed") != null)
            fixed = "true".equals(el.getAttribute("fixed")) || "1".equals(el.getAttribute("fixed"));
    }
    
    public String getFacet() {
        return(facet);
    }
    
    public String getValue() {
        return(value);
    }
    
    public boolean validerValeur(final String valeur) {
        if ("minExclusive".equals(facet)) {
            try {
                final double val = Double.parseDouble(valeur);
                return(val > iparam);
            } catch (final NumberFormatException ex) {
                return(false);
            }
        } else if ("minInclusive".equals(facet)) {
            try {
                final double val = Double.parseDouble(valeur);
                return(val >= iparam);
            } catch (final NumberFormatException ex) {
                return(false);
            }
        } else if ("maxExclusive".equals(facet)) {
            try {
                final double val = Double.parseDouble(valeur);
                return(val < iparam);
            } catch (final NumberFormatException ex) {
                return(false);
            }
        } else if ("maxInclusive".equals(facet)) {
            try {
                final double val = Double.parseDouble(valeur);
                return(val <= iparam);
            } catch (final NumberFormatException ex) {
                return(false);
            }
        } else if ("totalDigits".equals(facet)) {
            int nb = 0;
            for (int i=0; i<valeur.length(); i++)
                if (valeur.charAt(i) >= '0' && valeur.charAt(i) <= '9')
                    nb++;
            return(nb <= iparam);
        } else if ("fractionDigits".equals(facet)) {
            int nb = 0;
            boolean apres = false;
            for (int i=0; i<valeur.length(); i++) {
                if (!apres) {
                    if (valeur.charAt(i) == '.')
                        apres = true;
                } else if (valeur.charAt(i) >= '0' && valeur.charAt(i) <= '9')
                    nb++;
            }
            return(nb <= iparam);
        } else if ("length".equals(facet))
            return(valeur.length() == iparam);
        else if ("minLength".equals(facet))
            return(valeur.length() >= iparam);
        else if ("maxLength".equals(facet))
            return(valeur.length() <= iparam);
        else if ("enumeration".equals(facet)) {
            return(value != null && value.equals(valeur)); // A FAIRE: enumeration bas�e sur des entiers, par ex. 02 valide pour 2
        } else if ("whiteSpace".equals(facet)) {
            return(true);
            /*
            ?!?
            if ("collapse".equals(value))
                return(!"replace".equals(valeur) && !"preserve".equals(valeur));
            else if ("replace".equals(value))
                return(!"preserve".equals(valeur));
            else
                return(true);
            */
        } else if ("pattern".equals(facet)) {
            return(WXSSimpleType.verifExpr(valeur, value));
        } else
            return(true);
    }
}
