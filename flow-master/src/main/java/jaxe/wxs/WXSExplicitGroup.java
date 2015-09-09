/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conformément aux dispositions de la Licence Publique Générale GNU, telle que publiée par la Free Software Foundation ; version 2 de la licence, ou encore (à votre choix) toute version ultérieure.

Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans même la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de détail, voir la Licence Publique Générale GNU .

Vous devez avoir reçu un exemplaire de la Licence Publique Générale GNU en même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * WXSChoice et WXSSequence dérivent de cette classe
 */
public abstract class WXSExplicitGroup extends WXSAnnotated implements AvecSousElements, Parent {
    
    protected List<AvecSousElements> nestedParticles; // (element, group, choice, sequence, any)*
    protected int minOccurs = 1;
    protected int maxOccurs = 1;
    
    protected Parent parent; // WXSComplexType | WXSRestriction | WXSExtension | WXSGroup | WXSExplicitGroup
    
    
    protected void parse(final Element el, final Parent parent, final WXSSchema schema) {
        parseAnnotation(el);
        nestedParticles = new ArrayList<AvecSousElements>();
        for (Node n = el.getFirstChild(); n != null; n=n.getNextSibling()) {
            if (n instanceof Element) {
                if ("element".equals(n.getLocalName()))
                    nestedParticles.add(new WXSElement((Element)n, this, schema));
                else if ("group".equals(n.getLocalName()))
                    nestedParticles.add(new WXSGroup((Element)n, this, schema));
                else if ("choice".equals(n.getLocalName()))
                    nestedParticles.add(new WXSChoice((Element)n, this, schema));
                else if ("sequence".equals(n.getLocalName()))
                    nestedParticles.add(new WXSSequence((Element)n, this, schema));
                else if ("any".equals(n.getLocalName()))
                    nestedParticles.add(new WXSAny((Element)n, this, schema));
            }
        }
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
    }
    
    public void resoudreReferences(final WXSSchema schema, final WXSThing redefine) {
        for (AvecSousElements nestedParticle: nestedParticles)
            if (!(nestedParticle instanceof WXSAny))
                nestedParticle.resoudreReferences(schema, redefine);
    }
    
    public Set<WXSElement> listeTousElements() {
        final LinkedHashSet<WXSElement> liste = new LinkedHashSet<WXSElement>();
        for (AvecSousElements nestedParticle: nestedParticles)
            liste.addAll(nestedParticle.listeTousElements());
        return(liste);
    }
    
    public ArrayList<ToutElement> listeSousElements() {
        final ArrayList<ToutElement> liste = new ArrayList<ToutElement>();
        for (AvecSousElements nestedParticle: nestedParticles) {
            if (nestedParticle instanceof WXSElement)
                liste.addAll(((WXSElement)nestedParticle).listeElementsCorrespondant());
            else
                liste.addAll(nestedParticle.listeSousElements());
        }
        return(liste);
    }
    
    public ArrayList<WXSElement> listeElementsParents() {
        if (parent != null)
            return(parent.listeElementsParents());
        return(new ArrayList<WXSElement>());
    }
    
    public String expressionReguliere() {
        if (nestedParticles.size() == 0)
            return(null);
        final String separateur = (this instanceof WXSChoice) ? "|" : ", ";
        final StringBuilder sb = new StringBuilder();
        if (nestedParticles.size() > 1 || minOccurs != 1 || maxOccurs != 1)
            sb.append('(');
        boolean premier = true;
        for (Iterator<AvecSousElements> iter = nestedParticles.iterator(); iter.hasNext(); ) {
            final String er = iter.next().expressionReguliere();
            if (er != null) {
                if (!premier)
                    sb.append(separateur);
                premier = false;
                sb.append(er);
            }
        }
        if (nestedParticles.size() > 1 || minOccurs != 1 || maxOccurs != 1)
            sb.append(')');
        if (nestedParticles.size() == 1 && sb.length() > 2 && "((".equals(sb.substring(0, 2)) && "))".equals(sb.substring(sb.length() - 2, sb.length()))) {
            sb.deleteCharAt(0);
            sb.deleteCharAt(sb.length() - 1);
        }
        if (minOccurs == 0 && maxOccurs == 1)
            sb.append('?');
        else if (minOccurs == 0 && maxOccurs > 1)
            sb.append('*');
        else if (minOccurs > 0 && maxOccurs > 1)
            sb.append('+');
        return(sb.toString());
    }
    
    public Boolean enfantObligatoire(final WXSElement enfant) {
        for (AvecSousElements nestedParticle: nestedParticles) {
            if (nestedParticle instanceof WXSElement) {
                for (WXSElement el : ((WXSElement)nestedParticle).listeElementsCorrespondant())
                    if (el == enfant)
                        return(new Boolean((this instanceof WXSSequence || nestedParticles.size() == 1) && minOccurs != 0 && ((WXSElement)nestedParticle).getMinOccurs() != 0));
            } else  {
                Boolean bb = nestedParticle.enfantObligatoire(enfant);
                if (bb != null)
                    return(bb);
            }
        }
        return(null);
    }
    
    public Boolean enfantsMultiples(final WXSElement enfant) {
        for (AvecSousElements nestedParticle: nestedParticles) {
            if (nestedParticle instanceof WXSElement) {
                for (WXSElement el : ((WXSElement)nestedParticle).listeElementsCorrespondant())
                    if (el == enfant)
                        return(new Boolean(((WXSElement)nestedParticle).getMaxOccurs() > 1 || maxOccurs > 1));
            } else  {
                Boolean bb = nestedParticle.enfantsMultiples(enfant);
                if (bb != null && !bb.booleanValue() && maxOccurs > 1)
                    bb = Boolean.TRUE;
                if (bb != null)
                    return(bb);
            }
        }
        return(null);
    }
    
}
