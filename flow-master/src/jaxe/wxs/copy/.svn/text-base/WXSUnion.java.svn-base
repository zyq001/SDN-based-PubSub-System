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


public class WXSUnion extends WXSAnnotated {
    
    // (simpleType)*
    protected List<WXSSimpleType> simpleTypes;
    protected String[] memberTypes = null;
    
    protected Element domElement;
    protected WXSSimpleType[] wxsMemberTypes;
    
    
    public WXSUnion(final Element el, final WXSSchema schema) {
        parseAnnotation(el);
        simpleTypes = new ArrayList<WXSSimpleType>();
        for (Node n = el.getFirstChild(); n != null; n=n.getNextSibling()) {
            if (n instanceof Element && "simpleType".equals(n.getLocalName()))
                simpleTypes.add(new WXSSimpleType((Element)n, null, schema));
        }
        if (el.getAttributeNode("memberTypes") != null)
            memberTypes = (el.getAttribute("memberTypes")).split("\\s");
        
        domElement = el;
        wxsMemberTypes = null;
    }
    
    public void resoudreReferences(final WXSSchema schema, final WXSThing redefine) {
        for (WXSSimpleType simpleType : simpleTypes)
            simpleType.resoudreReferences(schema, redefine);
        if (memberTypes != null) {
            wxsMemberTypes = new WXSSimpleType[memberTypes.length];
            for (int i=0; i<memberTypes.length; i++) {
                final String type = memberTypes[i];
                final String tns = domElement.lookupNamespaceURI(JaxeWXS.prefixeNom(type));
                final WXSType wxsType = schema.resoudreReferenceType(JaxeWXS.valeurLocale(type), tns, redefine);
                if (wxsType instanceof WXSSimpleType)
                    wxsMemberTypes[i] = (WXSSimpleType)wxsType;
                else {
                    wxsMemberTypes[i] = null;
                    final String espaceSchema = domElement.getNamespaceURI();
                    if (!espaceSchema.equals(tns))
                        memberTypes[i] = null; // si le type n'a pas �t� r�solu il doit �tre un type des sch�mas XML
                }
            }
        }
    }
    
    public ArrayList<String> listeValeurs() {
        final ArrayList<String> liste = new ArrayList<String>();
        if (memberTypes != null) {
            for (int i=0; i<memberTypes.length; i++) {
                if (wxsMemberTypes[i] != null) {
                    final ArrayList<String> lv = wxsMemberTypes[i].listeValeurs();
                    if (lv == null)
                        return(null);
                    liste.addAll(lv);
                } else {
                    final String type = memberTypes[i];
                    final String tns = domElement.lookupNamespaceURI(JaxeWXS.prefixeNom(type));
                    final String espaceSchema = domElement.getNamespaceURI();
                    if (espaceSchema.equals(tns)) {
                        final ArrayList<String> lv = JaxeWXS.listeValeursBooleen(type, domElement);
                        if (lv == null)
                            return(null);
                        liste.addAll(lv);
                    }
                }
            }
        }
        for (WXSSimpleType st : simpleTypes) {
            final ArrayList<String> listest = st.listeValeurs();
            if (listest == null)
                return(null);
            liste.addAll(listest);
        }
        if (liste.size() == 0)
            return(null);
        return(liste);
    }
    
    public boolean validerValeur(final String valeur) {
        if (memberTypes != null) {
            for (int i=0; i<memberTypes.length; i++) {
                if (wxsMemberTypes[i] != null) {
                    if (wxsMemberTypes[i].validerValeur(valeur))
                        return(true);
                } else if (memberTypes[i] != null) {
                    if (WXSSimpleType.validerValeur(JaxeWXS.valeurLocale(memberTypes[i]), valeur))
                        return(true);
                }
            }
        }
        for (final WXSSimpleType st : simpleTypes) {
            if (st.validerValeur(valeur))
                return(true);
        }
        return(false);
    }
}
