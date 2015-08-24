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


public class WXSExtension extends WXSAnnotated implements AvecSousElements, Parent {
    
    // (group|all|choice|sequence)?, (attribute|attributeGroup)*
    protected AvecSousElements modele = null; // WXSGroup | WXSAll | WXSChoice | WXSSequence
    protected List<WXSThing> attrDecls; // attrDecls: (attribute|attributeGroup)*
    protected String base = null;
    protected WXSType wxsBase = null;
    
    protected Element domElement;
    protected WXSComplexContent parent;
    
    
    public WXSExtension(final Element el, final WXSComplexContent parent, final WXSSchema schema) {
        parseAnnotation(el);
        attrDecls = new ArrayList<WXSThing>();
        for (Node n = el.getFirstChild(); n != null; n=n.getNextSibling()) {
            if (n instanceof Element) {
                if ("group".equals(n.getLocalName()))
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
            if (wxsBase instanceof WXSComplexType)
                ((WXSComplexType)wxsBase).ajouterExtension(this);
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
        if (wxsBase instanceof WXSComplexType)
            liste.addAll(((WXSComplexType)wxsBase).listeSousElements());
        if (modele != null)
            liste.addAll(modele.listeSousElements());
        return(liste);
    }
    
    public ArrayList<WXSElement> listeElementsParents() {
        if (parent != null)
            return(parent.listeElementsParents());
        else
            return(new ArrayList<WXSElement>());
    }
    
    public String expressionReguliere() {
        final String erBase;
        if (wxsBase instanceof WXSComplexType)
            erBase = ((WXSComplexType)wxsBase).expressionReguliere();
        else
            erBase = null;
        final String erModele;
        if (modele != null)
            erModele = modele.expressionReguliere();
        else
            erModele = null;
        if (erBase == null && erModele == null)
            return("");
        else if (erBase != null && erModele == null)
            return(erBase);
        else if (erBase == null && erModele != null)
            return(erModele);
        else
            return('(' + erBase + ", " + erModele + ')');
    }
    
    public Boolean enfantObligatoire(final WXSElement enfant) {
        Boolean bb1 = null;
        if (wxsBase instanceof WXSComplexType)
            bb1 = ((WXSComplexType)wxsBase).enfantObligatoire(enfant);
        if (bb1 != null && bb1.booleanValue())
            return(bb1);
        Boolean bb2 = null;
        if (modele != null)
            bb2 = modele.enfantObligatoire(enfant);
        if (bb2 != null && bb2.booleanValue())
            return(bb2);
        return(bb1 != null ? bb1 : bb2);
    }
    
    public Boolean enfantsMultiples(final WXSElement enfant) {
        Boolean bb1 = null;
        if (wxsBase instanceof WXSComplexType)
            bb1 = ((WXSComplexType)wxsBase).enfantsMultiples(enfant);
        if (bb1 != null && bb1.booleanValue())
            return(bb1);
        Boolean bb2 = null;
        if (modele != null)
            bb2 = modele.enfantsMultiples(enfant);
        return(bb1 != null ? bb1 : bb2);
    }
    
    public ArrayList<String> listeValeurs() {
        if (wxsBase != null)
            return(wxsBase.listeValeurs());
        else if (base != null)
            return(JaxeWXS.listeValeursBooleen(base, domElement));
        return(null);
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
            final ArrayList<WXSAttribute> aAjouter = new ArrayList<WXSAttribute>();
            for (WXSAttribute attributExt : liste) {
                final String nomExt = attributExt.getName();
                boolean trouve = false;
                for (WXSAttribute attributBase : listeBase)
                    if (nomExt.equals(attributBase.getName())) {
                        trouve = true;
                        break;
                    }
                if (!trouve)
                    aAjouter.add(attributExt);
            }
            listeBase.addAll(aAjouter);
            return(listeBase);
        }
        return(liste);
    }
    
    public int valider(final List<WXSElement> sousElements, final int start, final boolean insertion) {
        int pos = start;
        if (wxsBase instanceof WXSComplexType) {
            pos = ((WXSComplexType)wxsBase).valider(sousElements, start, insertion);
            if (pos == start && !insertion && !((WXSComplexType)wxsBase).estOptionnel())
                return(start);
        }
        if (modele != null) {
            int pos2 = modele.valider(sousElements, pos, insertion);
            if (pos2 == pos && !insertion && !modele.estOptionnel())
                return(start);
            pos = pos2;
        }
        return(pos);
    }
    
    public boolean estOptionnel() {
        if (wxsBase instanceof WXSComplexType && !((WXSComplexType)wxsBase).estOptionnel())
            return(false);
        if (modele != null)
            return(modele.estOptionnel());
        return(true);
    }
    
    public boolean validerValeur(final String valeur) {
        if (wxsBase != null)
            return(wxsBase.validerValeur(valeur));
        else if (base != null)
            return(WXSSimpleType.validerValeur(JaxeWXS.valeurLocale(base), valeur));
        return(false);
    }
}
