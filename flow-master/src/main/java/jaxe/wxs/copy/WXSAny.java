/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conform�ment aux dispositions de la Licence Publique G�n�rale GNU, telle que publi�e par la Free Software Foundation ; version 2 de la licence, ou encore (� votre choix) toute version ult�rieure.

Ce programme est distribu� dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans m�me la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de d�tail, voir la Licence Publique G�n�rale GNU .

Vous devez avoir re�u un exemplaire de la Licence Publique G�n�rale GNU en m�me temps que ce programme ; si ce n'est pas le cas, �crivez � la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs.copy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;


public class WXSAny extends WXSAnnotated implements AvecSousElements {
    
    protected String namespace = "##any"; // ( (##any | ##other) | List of (anyURI | (##targetNamespace | ##local)) )
    protected String processContents = "strict"; // (skip|lax|strict)
    protected int minOccurs = 1;
    protected int maxOccurs = 1;
    
    protected WXSExplicitGroup parent;
    protected WXSSchema schema;
    protected ArrayList<ToutElement> elements;
    
    
    public WXSAny(final Element el, final WXSExplicitGroup parent, final WXSSchema schema) {
        if (el.getAttributeNode("namespace") != null)
            namespace = el.getAttribute("namespace");
        if (el.getAttributeNode("processContents") != null)
            processContents = el.getAttribute("processContents");
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
        
        this.parent = parent;
        this.schema = schema;
        elements = null;
    }
    
    public void resoudreReferences(final WXSSchema schema, final WXSThing redefine) {
        // la r�solution n�cessite que le sch�ma soit d�j� construit, on doit donc la faire plus tard
        elements = new ArrayList<ToutElement>();
        elements.addAll(schema.listeAny(namespace));
        for (ToutElement element : elements)
            if (element instanceof WXSElement)
                ((WXSElement)element).ajouterReference(this);
    }
    
    public Set<WXSElement> listeTousElements() {
        return(new LinkedHashSet<WXSElement>());
    }
    
    public ArrayList<ToutElement> listeSousElements() {
        if (elements == null)
            resoudreReferences(schema, null);
        return(elements);
    }
    
    public ArrayList<WXSElement> listeElementsParents() {
        return(parent.listeElementsParents());
    }
    
    public String expressionReguliere() {
        if (elements == null)
            resoudreReferences(schema, null);
        final StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (Iterator<ToutElement> iter = elements.iterator(); iter.hasNext(); ) {
            sb.append(schema.titreElement(iter.next().getDOMElement()));
            if (iter.hasNext())
                sb.append('|');
        }
        sb.append(')');
        if (minOccurs == 0 && maxOccurs == 1)
            sb.append('?');
        else if (minOccurs == 0 && maxOccurs > 1)
            sb.append('*');
        else if (minOccurs > 0 && maxOccurs > 1)
            sb.append('+');
        return(sb.toString());
    }
    
    public Boolean enfantObligatoire(final WXSElement enfant) {
        if (elements == null)
            resoudreReferences(schema, null);
        // renvoie null si l'enfant n'en est pas un
        if (elements.contains(enfant))
            return(new Boolean(minOccurs > 0 && elements.size() == 1));
        else
            return(null);
    }
    
    public Boolean enfantsMultiples(final WXSElement enfant) {
        if (elements == null)
            resoudreReferences(schema, null);
        // renvoie null si l'enfant n'en est pas un
        if (elements.contains(enfant))
            return(new Boolean(maxOccurs > 1));
        else
            return(null);
    }
    
    public int valider(final List<WXSElement> sousElements, final int start, final boolean insertion) {
        if (elements == null)
            resoudreReferences(schema, null);
        if (!insertion && sousElements.size() < minOccurs)
            return(start);
        for (int i=start; i<sousElements.size(); i++) {
            if (i-start >= maxOccurs)
                return(i);
            if (!elements.contains(sousElements.get(i))) {
                if (!insertion && i-start < minOccurs)
                    return(start);
                return(i);
            }
        }
        return(sousElements.size());
    }
    
    public boolean estOptionnel() {
        return(minOccurs == 0);
    }
}
