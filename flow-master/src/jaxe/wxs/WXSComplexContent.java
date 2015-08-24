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


public class WXSComplexContent extends WXSAnnotated implements AvecSousElements, Parent {
    
    // (restriction|extension)
    protected AvecSousElements modele = null; // WXSRestriction | WXSExtension
    protected Boolean mixed = null;
    
    protected WXSComplexType parent;
    
    
    public WXSComplexContent(final Element el, final WXSComplexType parent, final WXSSchema schema) {
        parseAnnotation(el);
        for (Node n = el.getFirstChild(); n != null; n=n.getNextSibling()) {
            if (n instanceof Element) {
                if ("restriction".equals(n.getLocalName()))
                    modele = new WXSRestriction((Element)n, this, schema);
                else if ("extension".equals(n.getLocalName()))
                    modele = new WXSExtension((Element)n, this, schema);
            }
        }
        if (el.getAttributeNode("mixed") != null)
            mixed = new Boolean("true".equals(el.getAttribute("mixed")) || "1".equals(el.getAttribute("mixed")));
        
        this.parent = parent;
    }
    
    public void resoudreReferences(final WXSSchema schema, final WXSThing redefine) {
        if (modele != null)
            modele.resoudreReferences(schema, redefine);
    }
    
    public Set<WXSElement> listeTousElements() {
        if (modele != null)
            return(modele.listeTousElements());
        return(new LinkedHashSet<WXSElement>());
    }
    
    public ArrayList<ToutElement> listeSousElements() {
        if (modele != null)
            return(modele.listeSousElements());
        return(new ArrayList<ToutElement>());
    }
    
    public String expressionReguliere() {
        if (modele != null)
            return(modele.expressionReguliere());
        return(null);
    }
    
    public Boolean enfantObligatoire(final WXSElement enfant) {
        if (modele != null)
            return(modele.enfantObligatoire(enfant));
        return(null);
    }
    
    public Boolean enfantsMultiples(final WXSElement enfant) {
        if (modele != null)
            return(modele.enfantsMultiples(enfant));
        return(null);
    }
    
    public ArrayList<WXSAttribute> listeAttributs() {
        if (modele instanceof WXSRestriction)
            return(((WXSRestriction)modele).listeAttributs());
        else if (modele instanceof WXSExtension)
            return(((WXSExtension)modele).listeAttributs());
        return(new ArrayList<WXSAttribute>());
    }
    
    public ArrayList<WXSElement> listeElementsParents() {
        return(parent.listeElementsParents());
    }
    
    public int valider(final List<WXSElement> sousElements, final int start, final boolean insertion) {
        if (modele != null)
            return(modele.valider(sousElements, start, insertion));
        return(start);
    }
    
    public boolean estOptionnel() {
        if (modele != null)
            return(modele.estOptionnel());
        return(true);
    }
}
