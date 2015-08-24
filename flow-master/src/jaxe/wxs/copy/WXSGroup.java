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

import org.apache.log4j.Logger;


public class WXSGroup extends WXSAnnotated implements AvecSousElements, Parent {
    
    private static final Logger LOG = Logger.getLogger(WXSGroup.class);
    
    // si pas ref: (all|choice|sequence)
    protected AvecSousElements modele = null; // (WXSAll | WXSChoice | WXSSequence)
    protected String name = null;
    protected String ref = null;
    protected WXSGroup wxsRef = null;
    protected int minOccurs = 1;
    protected int maxOccurs = 1;
    
    protected Element domElement;
    protected Parent parent; // WXSComplexType | WXSRestriction | WXSExtension | WXSExplicitGroup | WXSRedefine
    protected WXSSchema schema;
    protected List<WXSGroup> references;
    
    
    public WXSGroup(final Element el, final Parent parent, final WXSSchema schema) {
        parseAnnotation(el);
        for (Node n = el.getFirstChild(); n != null; n=n.getNextSibling()) {
            if (n instanceof Element) {
                if ("all".equals(n.getLocalName()))
                    modele = new WXSAll((Element)n, this, schema);
                else if ("choice".equals(n.getLocalName()))
                    modele = new WXSChoice((Element)n, this, schema);
                else if ("sequence".equals(n.getLocalName()))
                    modele = new WXSSequence((Element)n, this, schema);
            }
        }
        if (el.getAttributeNode("name") != null)
            name = el.getAttribute("name");
        if (el.getAttributeNode("ref") != null)
            ref = el.getAttribute("ref");
        try {
            if (el.getAttributeNode("minOccurs") != null)
                minOccurs = Integer.parseInt(el.getAttribute("minOccurs"));
            if (el.getAttributeNode("maxOccurs") != null) {
                if ("unbounded".equals(el.getAttribute("maxOccurs")))
                    maxOccurs = Integer.MAX_VALUE;
                else
                    maxOccurs = Integer.parseInt(el.getAttribute("maxOccurs"));
            }
        } catch (NumberFormatException ex) {
        }
        
        domElement = el;
        this.parent = parent;
        this.schema = schema;
        references = null;
    }
    
    public String getName() {
        if (name == null && wxsRef != null)
            return(wxsRef.getName());
        return(name);
    }
    
    public String getNamespace() {
        return(schema.getTargetNamespace());
    }
    
    public Parent getParent() {
        return(parent);
    }
    
    public void resoudreReferences(final WXSSchema schema, final WXSThing redefine) {
        if (modele != null)
            modele.resoudreReferences(schema, redefine);
        if (ref != null) {
            final String tns = domElement.lookupNamespaceURI(JaxeWXS.prefixeNom(ref));
            wxsRef = schema.resoudreReferenceGroupe(JaxeWXS.valeurLocale(ref), tns, redefine);
            if (wxsRef != null)
                wxsRef.ajouterReference(this);
            else
                LOG.error("R�f�rence de groupe introuvable : " + ref);
        }
    }
    
    public void ajouterReference(final WXSGroup groupe) {
        if (references == null)
            references = new ArrayList<WXSGroup>();
        references.add(groupe);
    }
    
    public Set<WXSElement> listeTousElements() {
        if (modele != null)
            return(modele.listeTousElements());
        return(new LinkedHashSet<WXSElement>());
    }
    
    public ArrayList<ToutElement> listeSousElements() {
        if (wxsRef != null)
            return(wxsRef.listeSousElements());
        if (modele != null)
            return(modele.listeSousElements());
        return(new ArrayList<ToutElement>());
    }
    
    public ArrayList<WXSElement> listeElementsParents() {
        final ArrayList<WXSElement> liste = new ArrayList<WXSElement>();
        if (parent != null)
            liste.addAll(parent.listeElementsParents());
        if (references != null) {
            for (WXSGroup groupe : references)
                liste.addAll(groupe.listeElementsParents());
        }
        return(liste);
    }
    
    public String expressionReguliere() {
        final String er;
        if (wxsRef != null)
            er = wxsRef.expressionReguliere();
        else if (modele != null)
            er = modele.expressionReguliere();
        else
            er = "()";
        if (minOccurs == 0 && maxOccurs == 1)
            return(er + '?');
        else if (minOccurs == 0 && maxOccurs > 1)
            return(er + '*');
        else if (minOccurs > 0 && maxOccurs > 1)
            return(er + '+');
        else
            return(er);
    }
    
    public Boolean enfantObligatoire(final WXSElement enfant) {
        if (wxsRef != null)
            return(wxsRef.enfantObligatoire(enfant));
        // renvoie null si l'enfant n'en est pas un
        Boolean bb = null;
        if (modele != null)
            bb = modele.enfantObligatoire(enfant);
        return(bb);
    }
    
    public Boolean enfantsMultiples(final WXSElement enfant) {
        if (wxsRef != null)
            return(wxsRef.enfantsMultiples(enfant));
        // renvoie null si l'enfant n'en est pas un
        Boolean bb = null;
        if (modele != null)
            bb = modele.enfantsMultiples(enfant);
        return(bb);
    }
    
    public int valider(final List<WXSElement> sousElements, final int start, final boolean insertion) {
        if (!insertion && sousElements.size() < minOccurs)
            return(start);
        int nb = 0;
        for (int i=start; i<sousElements.size(); ) {
            if (nb >= maxOccurs)
                return(i);
            int pos = i;
            if (wxsRef != null)
                pos = wxsRef.valider(sousElements, i, insertion);
            else if (modele != null)
                pos = modele.valider(sousElements, i, insertion);
            if (pos == i)
                return(i);
            i = pos;
            nb++;
        }
        return(sousElements.size());
    }
    
    public boolean estOptionnel() {
        if (minOccurs == 0)
            return(true);
        if (wxsRef != null)
            return(wxsRef.estOptionnel());
        if (modele != null)
            return(modele.estOptionnel());
        return(true);
    }
}
