/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conform�ment aux dispositions de la Licence Publique G�n�rale GNU, telle que publi�e par la Free Software Foundation ; version 2 de la licence, ou encore (� votre choix) toute version ult�rieure.

Ce programme est distribu� dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans m�me la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de d�tail, voir la Licence Publique G�n�rale GNU .

Vous devez avoir re�u un exemplaire de la Licence Publique G�n�rale GNU en m�me temps que ce programme ; si ce n'est pas le cas, �crivez � la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs.copy;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class WXSComplexType extends WXSAnnotated implements WXSType, AvecSousElements, Parent {
    
    // (simpleContent | complexContent | ((group|all|choice|sequence)?, (attribute|attributeGroup)*))
    protected WXSSimpleContent simpleContent = null;
    protected AvecSousElements modele = null; // WXSComplexContent | WXSGroup | WXSAll | WXSChoice | WXSSequence
    protected List<WXSThing> attrDecls; // attrDecls: (attribute|attributeGroup)*
    protected String name = null;
    protected boolean mixed = false;
    protected boolean abstractAtt = false;
    
    protected Parent parent; // WXSElement | WXSRedefine
    protected WXSSchema schema;
    protected List<WXSElement> references;
    protected List<WXSExtension> extensions;
    
    
    public WXSComplexType(final Element el, final Parent parent, final WXSSchema schema) {
        parseAnnotation(el);
        attrDecls = new ArrayList<WXSThing>();
        for (Node n = el.getFirstChild(); n != null; n=n.getNextSibling()) {
            if (n instanceof Element) {
                if ("simpleContent".equals(n.getLocalName()))
                    simpleContent = new WXSSimpleContent((Element)n, schema);
                else if ("complexContent".equals(n.getLocalName()))
                    modele = new WXSComplexContent((Element)n, this, schema);
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
        if (el.getAttributeNode("name") != null)
            name = el.getAttribute("name");
        if (el.getAttributeNode("mixed") != null)
            mixed = "true".equals(el.getAttribute("mixed")) || "1".equals(el.getAttribute("mixed"));
        if (el.getAttributeNode("abstract") != null)
            abstractAtt = "true".equals(el.getAttribute("abstract")) || "1".equals(el.getAttribute("abstract"));
        
        this.parent = parent;
        this.schema = schema;
        references = null;
        extensions = null;
    }
    
    public WXSSimpleContent getSimpleContent() {
        return(simpleContent);
    }
    
    public String getName() {
        return(name);
    }
    
    public boolean getMixed() {
        return(mixed);
    }
    
    public String getNamespace() {
        return(schema.getTargetNamespace());
    }
    
    public Parent getParent() {
        return(parent);
    }
    
    public void resoudreReferences(final WXSSchema schema, final WXSThing redefine) {
        if (simpleContent != null)
            simpleContent.resoudreReferences(schema, redefine);
        if (modele != null)
            modele.resoudreReferences(schema, redefine);
        for (WXSThing attrDecl: attrDecls) {
            if (attrDecl instanceof WXSAttribute)
                ((WXSAttribute)attrDecl).resoudreReferences(schema);
            else if (attrDecl instanceof WXSAttributeGroup)
                ((WXSAttributeGroup)attrDecl).resoudreReferences(schema, redefine);
        }
    }
    
    public void ajouterReference(final WXSElement element) {
        if (references == null)
            references = new ArrayList<WXSElement>();
        references.add(element);
    }
    
    public void ajouterExtension(final WXSExtension ext) {
        if (extensions == null)
            extensions = new ArrayList<WXSExtension>();
        extensions.add(ext);
    }
    
    public Set<WXSElement> listeTousElements() {
        if (modele != null)
            return(modele.listeTousElements());
        return(new LinkedHashSet<WXSElement>());
    }
    
    public ArrayList<ToutElement> listeSousElements() {
        final ArrayList<ToutElement> liste = new ArrayList<ToutElement>();
        if (modele != null)
            liste.addAll(modele.listeSousElements());
        return(liste);
    }
    
    public ArrayList<WXSElement> listeElementsParents() {
        final ArrayList<WXSElement> liste = new ArrayList<WXSElement>();
        if (parent instanceof WXSElement) {
            if (!((WXSElement)parent).getAbstract())
                liste.add((WXSElement)parent);
            final List<WXSElement> substitutions = ((WXSElement)parent).getSubstitutions();
            if (substitutions != null)
                liste.addAll(substitutions);
        }
        if (references != null) {
            for (WXSElement el : references) {
                if (!el.getAbstract())
                    liste.add(el);
                final List<WXSElement> substitutions = el.getSubstitutions();
                if (substitutions != null)
                    liste.addAll(substitutions);
            }
        }
        if (extensions != null) {
            for (WXSExtension ext : extensions)
                liste.addAll(ext.listeElementsParents());
        }
        return(liste);
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
    
    public ArrayList<String> listeValeurs() {
        if (simpleContent != null)
            return(simpleContent.listeValeurs());
        return(null);
    }
    
    public ArrayList<WXSAttribute> listeAttributs() {
        if (simpleContent != null)
            return(simpleContent.listeAttributs());
        else if (modele instanceof WXSComplexContent)
            return(((WXSComplexContent)modele).listeAttributs());
        final ArrayList<WXSAttribute> liste = new ArrayList<WXSAttribute>();
        for (WXSThing attrDecl: attrDecls) {
            if (attrDecl instanceof WXSAttribute)
                liste.add((WXSAttribute)attrDecl);
            else if (attrDecl instanceof WXSAttributeGroup)
                liste.addAll(((WXSAttributeGroup)attrDecl).listeAttributs());
        }
        return(liste);
    }
    
    public int valider(final List<WXSElement> sousElements, final int start, final boolean insertion) {
        if (simpleContent != null)
            return(start);
        else if (modele != null)
            return(modele.valider(sousElements, start, insertion));
        return(start);
    }
    
    public boolean estOptionnel() {
        if (simpleContent != null)
            return(true);
        else if (modele != null)
            return(modele.estOptionnel());
        return(true);
    }
    
    public boolean validerValeur(final String valeur) {
        if (simpleContent != null)
            return(simpleContent.validerValeur(valeur));
        return(false);
    }
}
