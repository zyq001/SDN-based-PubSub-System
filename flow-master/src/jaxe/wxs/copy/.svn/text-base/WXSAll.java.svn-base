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
import org.w3c.dom.Node;


public class WXSAll extends WXSAnnotated implements AvecSousElements, Parent {
    
    protected ArrayList<WXSElement> elements; // (element)*
    protected int minOccurs = 1; // 0 | 1
    protected int maxOccurs = 1; // 1
    
    protected Parent parent; // WXSComplexType | WXSGroup | WXSRestriction | WXSExtension
    
    
    public WXSAll(final Element el, final Parent parent, final WXSSchema schema) {
        parseAnnotation(el);
        elements = new ArrayList<WXSElement>();
        for (Node n = el.getFirstChild(); n != null; n=n.getNextSibling()) {
            if (n instanceof Element && "element".equals(n.getLocalName()))
                elements.add(new WXSElement((Element)n, this, schema));
        }
        try {
            if (el.getAttributeNode("minOccurs") != null)
                minOccurs = Integer.parseInt(el.getAttribute("minOccurs"));
        } catch (NumberFormatException ex) {
        }
        this.parent = parent;
    }
    
    public void resoudreReferences(final WXSSchema schema, final WXSThing redefine) {
        for (WXSElement element: elements)
            element.resoudreReferences(schema, redefine);
    }
    
    public Set<WXSElement> listeTousElements() {
        final LinkedHashSet<WXSElement> liste = new LinkedHashSet<WXSElement>();
        for (WXSElement element: elements)
            liste.addAll(element.listeTousElements());
        return(liste);
    }
    
    public ArrayList<ToutElement> listeSousElements() {
        final ArrayList<ToutElement> liste = new ArrayList<ToutElement>();
        for (WXSElement element: elements)
            liste.addAll(element.listeElementsCorrespondant());
        return(liste);
    }
    
    public ArrayList<WXSElement> listeElementsParents() {
        if (parent != null)
            return(parent.listeElementsParents());
        return(new ArrayList<WXSElement>());
    }
    
    public String expressionReguliere() {
        final StringBuilder sb = new StringBuilder();
        sb.append('(');
        boolean premier = true;
        for (Iterator<WXSElement> iter = elements.iterator(); iter.hasNext(); ) {
            final String er = iter.next().expressionReguliere();
            if (er != null) {
                if (!premier)
                    sb.append(" & ");
                premier = false;
                sb.append(er);
            }
        }
        sb.append(')');
        if (minOccurs == 0)
            sb.append('?');
        return(sb.toString());
    }
    
    public Boolean enfantObligatoire(final WXSElement enfant) {
        // renvoie null si l'enfant n'en est pas un
        for (WXSElement element : elements)
            for (WXSElement elc : element.listeElementsCorrespondant())
                if (elc == enfant)
                    return(new Boolean(minOccurs > 0 && element.getMinOccurs() > 0));
        return(null);
    }
    
    public Boolean enfantsMultiples(final WXSElement enfant) {
        // renvoie null si l'enfant n'en est pas un
        for (WXSElement element : elements)
            for (WXSElement elc : element.listeElementsCorrespondant())
                if (elc == enfant)
                    return(Boolean.FALSE);
        return(null);
    }
    
    public int valider(final List<WXSElement> sousElements, final int start, final boolean insertion) {
        if (elements.size() == 0)
            return(start);
        int[] occurences = new int[elements.size()];
        for (int i=0; i<elements.size(); i++)
            occurences[i] = 0;
        int nb = 0;
        for (int i=start; i<sousElements.size(); i++) {
            final WXSElement sousElement = sousElements.get(i);
            boolean trouve = false;
            for (int j=0; j<elements.size(); j++) {
                if (sousElement == elements.get(j)) {
                    trouve = true;
                    occurences[j]++;
                    break;
                }
            }
            if (!trouve)
                break;
            nb++;
        }
        for (int i=0; i<elements.size(); i++)
            if (occurences[i] > 1)
                return(start);
        if (!insertion) {
            for (int i=0; i<elements.size(); i++)
                if (occurences[i] == 0 && !elements.get(i).estOptionnel())
                    return(start);
        }
        return(start + nb);
    }
    
    public boolean estOptionnel() {
        if (elements.size() == 0)
            return(true);
        if (minOccurs == 0)
            return(true);
        for (WXSElement element : elements) {
            if (!element.estOptionnel())
                return(false);
        }
        return(true);
    }
}
