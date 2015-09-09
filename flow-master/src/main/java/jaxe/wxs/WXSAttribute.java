/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conformément aux dispositions de la Licence Publique Générale GNU, telle que publiée par la Free Software Foundation ; version 2 de la licence, ou encore (à votre choix) toute version ultérieure.

Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans même la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de détail, voir la Licence Publique Générale GNU .

Vous devez avoir reçu un exemplaire de la Licence Publique Générale GNU en même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.apache.log4j.Logger;


public class WXSAttribute extends WXSAnnotated {
    
    private static final Logger LOG = Logger.getLogger(WXSAttribute.class);
    
    protected WXSSimpleType simpleType = null;
    protected String name = null;
    protected String ref = null;
    protected String type = null;
    protected String use = null; // (prohibited|optional|required)
    protected String defaultAtt = null;
    protected String fixed = null;
    protected String form = null; // (qualified|unqualified)
    
    protected WXSAttribute wxsRef = null;
    protected Element domElement;
    protected Parent parent; // WXSComplexType | WXSRestriction | WXSExtension | WXSAttributeGroup
    protected WXSSchema schema;
    
    
    public WXSAttribute(final Element el, final Parent parent, final WXSSchema schema) {
        parseAnnotation(el);
        for (Node n = el.getFirstChild(); n != null; n=n.getNextSibling()) {
            if (n instanceof Element && "simpleType".equals(n.getLocalName())) {
                simpleType = new WXSSimpleType((Element)n, null, schema);
                break;
            }
        }
        if (el.getAttributeNode("name") != null)
            name = el.getAttribute("name");
        if (el.getAttributeNode("ref") != null)
            ref = el.getAttribute("ref");
        if (el.getAttributeNode("type") != null)
            type = el.getAttribute("type");
        if (el.getAttributeNode("use") != null)
            use = el.getAttribute("use");
        if (el.getAttributeNode("default") != null)
            defaultAtt = el.getAttribute("default");
        if (el.getAttributeNode("fixed") != null)
            fixed = el.getAttribute("fixed");
        if (el.getAttributeNode("form") != null)
            form = el.getAttribute("form");
        
        domElement = el;
        this.parent = parent;
        this.schema = schema;
    }
    
    public void resoudreReferences(final WXSSchema schema) {
        if (simpleType != null)
            simpleType.resoudreReferences(schema, null);
        if (ref != null) {
            final String prefixe = JaxeWXS.prefixeNom(ref);
            final String tns;
            if ("xml".equals(prefixe))
                tns = "http://www.w3.org/XML/1998/namespace";
            else
                tns = domElement.lookupNamespaceURI(prefixe);
            wxsRef = schema.resoudreReferenceAttribut(JaxeWXS.valeurLocale(ref), tns);
            if (wxsRef == null)
                LOG.error("Référence d'attribut introuvable : " + ref);
        }
        if (simpleType == null && type != null) {
            final String tns = domElement.lookupNamespaceURI(JaxeWXS.prefixeNom(type));
            // pas de résolution pour les types du superschéma, sauf pour le superschéma
            if (tns == null || !tns.equals(domElement.getNamespaceURI()) ||
                    schema.getTargetNamespace() == null || schema.getTargetNamespace().equals(domElement.getNamespaceURI())) {
                final WXSType wxsType = schema.resoudreReferenceType(JaxeWXS.valeurLocale(type), tns, null);
                if (wxsType instanceof WXSSimpleType)
                    simpleType = (WXSSimpleType)wxsType;
            }
        }
        if (simpleType == null && wxsRef != null)
            simpleType = wxsRef.simpleType;
    }
    
    public String getName() {
        if (name == null && wxsRef != null)
            return(wxsRef.getName());
        return(name);
    }
    
    public String getUse() {
        return(use);
    }
    
    public String getForm() {
        return(form);
    }
    
    public Element getDOMElement() {
        return(domElement);
    }
    
    public String getNamespace() {
        if (ref != null) {
            final String prefixe = JaxeWXS.prefixeNom(ref);
            if (prefixe != null) {
                final String ns = domElement.lookupNamespaceURI(prefixe);
                if (ns != null)
                    return(ns);
                if ("xml".equals(prefixe))
                    return("http://www.w3.org/XML/1998/namespace");
                return(null);
            }
        }
        boolean qualified;
        if (schema.getTopAttributes().contains(this))
            qualified = true;
        else if (form != null)
            qualified = "qualified".equals(form);
        else
            qualified = "qualified".equals(schema.getAttributeFormDefault());
        if (qualified) {
            final String tn = schema.getTargetNamespace();
            if ("".equals(tn))
                return(null);
            else
                return(tn);
        } else
            return(null);
    }
    
    public WXSAttribute getWXSRef() {
        return(wxsRef);
    }
    
    public ArrayList<WXSElement> listeElementsParents() {
        if (parent != null)
            return(parent.listeElementsParents());
        return(new ArrayList<WXSElement>());
    }
    
    public ArrayList<String> listeValeurs() {
        if (fixed != null) {
            final ArrayList<String> fixedval = new ArrayList<String>();
            fixedval.add(fixed);
            return(fixedval);
        }
        if (schema.getTargetNamespace() != null && schema.getTargetNamespace().equals(domElement.getNamespaceURI()) && "boolean".equals(JaxeWXS.valeurLocale(type)))
            return(JaxeWXS.listeValeursBooleen(type, domElement)); // cas du superschéma
        if (simpleType != null)
            return(simpleType.listeValeurs());
        else if (type != null)
            return(JaxeWXS.listeValeursBooleen(type, domElement));
        return(null);
    }
    
    public String valeurParDefaut() {
        if (defaultAtt != null)
            return(defaultAtt);
        else if (fixed != null)
            return(fixed);
        else if (wxsRef != null)
            return(wxsRef.valeurParDefaut());
        return(null);
    }
    
    public boolean validerValeur(final String valeur) {
        if (fixed != null)
            return(fixed.equals(valeur));
        if  ((valeur == null || "".equals(valeur)) && "required".equals(use))
            return(false);
        if (simpleType != null)
            return(simpleType.validerValeur(valeur));
        if (type != null) {
            final String tns = domElement.lookupNamespaceURI(JaxeWXS.prefixeNom(type));
            if (tns != null && tns.equals(domElement.getNamespaceURI()))
                return(WXSSimpleType.validerValeur(JaxeWXS.valeurLocale(type), valeur));
        }
        if (wxsRef != null)
            return(wxsRef.validerValeur(valeur));
        if (type == null)
            return(true);
        return(false);
    }
}
