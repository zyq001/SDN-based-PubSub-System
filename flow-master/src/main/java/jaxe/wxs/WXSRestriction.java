/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conformément aux dispositions de la Licence Publique Générale GNU, telle que publiée par la Free Software Foundation ; version 2 de la licence, ou encore (à votre choix) toute version ultérieure.

Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans même la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de détail, voir la Licence Publique Générale GNU .

Vous devez avoir reçu un exemplaire de la Licence Publique Générale GNU en même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class WXSRestriction extends WXSAnnotated implements AvecSousElements, Parent {
    
    // simpleType?, (minExclusive|minInclusive|maxExclusive|maxInclusive|totalDigits|fractionDigits|length|minLength|maxLength|enumeration|pattern)*
    // ou: (group|all|choice|sequence)?, (attribute|attributeGroup)*
    protected WXSSimpleType simpleType = null;
    protected List<WXSFacet> facets;
    protected AvecSousElements modele = null; // WXSGroup | WXSAll | WXSChoice | WXSSequence
    protected List<WXSThing> attrDecls; // attrDecls: (attribute|attributeGroup)*
    protected String base = null;
    
    protected WXSType wxsBase = null;
    
    protected Element domElement;
    protected WXSComplexContent parent;
    
    
    public WXSRestriction(final Element el, final WXSComplexContent parent, final WXSSchema schema) {
        parseAnnotation(el);
        facets = new ArrayList<WXSFacet>();
        attrDecls = new ArrayList<WXSThing>();
        for (Node n = el.getFirstChild(); n != null; n=n.getNextSibling()) {
            if (n instanceof Element) {
                if ("simpleType".equals(n.getLocalName()))
                    simpleType = new WXSSimpleType((Element)n, this, schema);
                else if ("minExclusive".equals(n.getLocalName()))
                    facets.add(new WXSFacet((Element)n));
                else if ("minInclusive".equals(n.getLocalName()))
                    facets.add(new WXSFacet((Element)n));
                else if ("maxExclusive".equals(n.getLocalName()))
                    facets.add(new WXSFacet((Element)n));
                else if ("maxInclusive".equals(n.getLocalName()))
                    facets.add(new WXSFacet((Element)n));
                else if ("totalDigits".equals(n.getLocalName()))
                    facets.add(new WXSFacet((Element)n));
                else if ("fractionDigits".equals(n.getLocalName()))
                    facets.add(new WXSFacet((Element)n));
                else if ("length".equals(n.getLocalName()))
                    facets.add(new WXSFacet((Element)n));
                else if ("minLength".equals(n.getLocalName()))
                    facets.add(new WXSFacet((Element)n));
                else if ("maxLength".equals(n.getLocalName()))
                    facets.add(new WXSFacet((Element)n));
                else if ("enumeration".equals(n.getLocalName()))
                    facets.add(new WXSFacet((Element)n));
                else if ("pattern".equals(n.getLocalName()))
                    facets.add(new WXSFacet((Element)n));
                else if ("group".equals(n.getLocalName()))
                    modele = new WXSGroup((Element)n, this, schema);
                else if ("all".equals(n.getLocalName()))
                    modele = new WXSAll((Element)n, this, schema);
                else if ("choice".equals(n.getLocalName()))
                    modele = new WXSChoice((Element)n, this, schema);
                else if ("sequence".equals(n.getLocalName()))
                    modele = new WXSSequence((Element)n, this, schema);
                else if ("attribute".equals(n.getLocalName()))
                    attrDecls.add(new WXSAttribute((Element)n, this, schema));
                else if ("attributeGroup".equals(n.getLocalName()))
                    attrDecls.add(new WXSAttributeGroup((Element)n, this, schema));
            }
        }
        if (el.getAttributeNode("base") != null)
            base = el.getAttribute("base");
        
        domElement = el;
        this.parent = parent;
    }
    
    public void resoudreReferences(final WXSSchema schema, final WXSThing redefine) {
        if (simpleType != null)
            simpleType.resoudreReferences(schema, redefine);
        if (modele != null)
            modele.resoudreReferences(schema, redefine);
        for (WXSThing attrDecl: attrDecls) {
            if (attrDecl instanceof WXSAttribute)
                ((WXSAttribute)attrDecl).resoudreReferences(schema);
            else if (attrDecl instanceof WXSAttributeGroup)
                ((WXSAttributeGroup)attrDecl).resoudreReferences(schema, redefine);
        }
        if (base != null) {
            final String tns = domElement.lookupNamespaceURI(JaxeWXS.prefixeNom(base));
            wxsBase = schema.resoudreReferenceType(JaxeWXS.valeurLocale(base), tns, redefine);
        }
    }
    
    public Set<WXSElement> listeTousElements() {
        final LinkedHashSet<WXSElement> liste = new LinkedHashSet<WXSElement>();
        if (modele != null)
            liste.addAll(modele.listeTousElements());
        return(liste);
    }
    
    public ArrayList<ToutElement> listeSousElements() {
        final ArrayList<ToutElement> liste = new ArrayList<ToutElement>();
        if (modele != null)
            liste.addAll(modele.listeSousElements());
        return(liste);
    }
    
    public ArrayList<WXSElement> listeElementsParents() {
        if (parent instanceof WXSComplexContent)
            return(((WXSComplexContent)parent).listeElementsParents());
        else
            return(new ArrayList<WXSElement>());
    }
    
    public String expressionReguliere() {
        if (modele != null)
            return(modele.expressionReguliere());
        return(null);
    }
    
    public Boolean enfantObligatoire(final WXSElement enfant) {
        // renvoie null si l'enfant n'en est pas un
        if (modele != null)
            return(modele.enfantObligatoire(enfant));
        return(null);
    }
    
    public Boolean enfantsMultiples(final WXSElement enfant) {
        // renvoie null si l'enfant n'en est pas un
        if (modele != null)
            return(modele.enfantsMultiples(enfant));
        return(null);
    }
    
    public ArrayList<String> listeValeurs() {
        ArrayList<String> liste = null;
        for (WXSFacet facet : facets) {
            if ("enumeration".equals(facet.getFacet())) {
                if (liste == null)
                    liste = new ArrayList<String>();
                liste.add(facet.getValue());
            }
        }
        return(liste);
    }
    
    public ArrayList<WXSAttribute> listeAttributs() {
        final ArrayList<WXSAttribute> liste = new ArrayList<WXSAttribute>();
        for (WXSThing attrDecl: attrDecls) {
            if (attrDecl instanceof WXSAttribute)
                liste.add((WXSAttribute)attrDecl);
            else if (attrDecl instanceof WXSAttributeGroup)
                liste.addAll(((WXSAttributeGroup)attrDecl).listeAttributs());
        }
        if (wxsBase instanceof WXSComplexType) {
            final ArrayList<WXSAttribute> listeBase = ((WXSComplexType)wxsBase).listeAttributs();
            final ArrayList<WXSAttribute> aRetirer = new ArrayList<WXSAttribute>();
            for (WXSAttribute attributRest : liste) {
                final String nomExt = attributRest.getName();
                final boolean prohibited = "prohibited".equals(attributRest.getUse());
                for (WXSAttribute attributBase : listeBase)
                    if (nomExt.equals(attributBase.getName())) {
                        if (prohibited)
                            aRetirer.add(attributBase);
                        else
                            listeBase.set(listeBase.indexOf(attributBase), attributRest);
                        break;
                    }
            }
            for (WXSAttribute attribut : aRetirer)
                listeBase.remove(attribut);
            return(listeBase);
        }
        return(liste);
    }
    
    public int valider(final List<WXSElement> sousElements, final int start, final boolean insertion) {
        if (modele == null)
            return(start);
        return(modele.valider(sousElements, start, insertion));
    }
    
    public boolean estOptionnel() {
        if (modele != null)
            return(modele.estOptionnel());
        return(true);
    }
    
    public boolean validerValeur(final String valeur) {
        if (wxsBase != null) {
            if (!wxsBase.validerValeur(valeur))
                return(false);
        }
        boolean enumerationOrPattern = false;
        for (final WXSFacet facet : facets) {
            if ("enumeration".equals(facet.getFacet())) {
                if (facet.validerValeur(valeur))
                    return(true);
                enumerationOrPattern = true;
            } else if ("pattern".equals(facet.getFacet())) {
                if (facet.validerValeur(valeur))
                    return(true);
                enumerationOrPattern = true;
            } else if (!facet.validerValeur(valeur))
                return(false);
        }
        if (enumerationOrPattern)
            return(false);
        return(true);
    }
}
