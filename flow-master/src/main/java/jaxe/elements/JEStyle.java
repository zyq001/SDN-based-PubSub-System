/*
Jaxe - Editeur XML en Java

Copyright (C) 2002 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conformément aux dispositions de la Licence Publique Générale GNU, telle que publiée par la Free Software Foundation ; version 2 de la licence, ou encore (à votre choix) toute version ultérieure.

Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans même la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de détail, voir la Licence Publique Générale GNU .

Vous devez avoir reçu un exemplaire de la Licence Publique Générale GNU en même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.elements;

import org.apache.log4j.Logger;

import java.awt.Toolkit;
import java.util.Arrays;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import jaxe.FonctionAjStyle;
import jaxe.JaxeDocument;
import jaxe.JaxeElement;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Elément de style (B ou I ou SUB ou SUP). Modifie l'aspect du texte en conséquence.
 * Type d'élément Jaxe: 'style'
 * paramètre: style: GRAS | ITALIQUE | EXPOSANT | INDICE | SOULIGNE | BARRE |
 *                   PCOULEUR[###,###,###] | FCOULEUR[###,###,###]
 *            (plusieurs styles peuvent être combinés avec un caractère ';')
 *
 * NORMAL ne doit plus être utilisé (il faut utiliser FONCTION à la place, avec
 * classe="jaxe.FonctionNormal")
 */
public class JEStyle extends JaxeElement {
    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(JEStyle.class);

    public String ceStyle;
    
    public JEStyle(final JaxeDocument doc) {
        this.doc = doc;
    }
    
    @Override
    public void init(final Position pos, final Node noeud) {
        final String valeurStyle = doc.cfg.valeurParametreElement(refElement, "style", null);
        if (valeurStyle == null)
            return;
        final StringBuilder styleBuilder = new StringBuilder();
        styleBuilder.append(valeurStyle);

        ceStyle = styleBuilder.toString();
            
        final int offsetdebut = pos.getOffset();
        try {
            debut = doc.createPosition(offsetdebut);
        } catch (final BadLocationException ex) {
            LOG.error("JEStyle.init", ex);
        }
        Position newpos = pos;
        
        creerEnfants(newpos);
        try {
            debut = doc.createPosition(offsetdebut);
        } catch (final BadLocationException ex) {
            LOG.error("JEStyle.init", ex);
        }
        
        if (newpos.getOffset() != offsetdebut)
            changerStyle(ceStyle, offsetdebut, newpos.getOffset() - offsetdebut);
    }
    
    public String getText() {
        return(getText((Element)noeud));
    }
    
    private static String getText(final Element elem) {
        final StringBuilder sb = new StringBuilder();
        for (Node n = elem.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.TEXT_NODE)
                sb.append(n.getNodeValue());
            else if (n.getNodeType() == Node.ELEMENT_NODE)
                sb.append(getText((Element)n));
        }
        return(sb.toString());
    }
    
    @Override
    public Node nouvelElement(final Element refElement) {
        return null;
    }
    
    public static void appliquer(final JaxeDocument doc, final int start, final int end, final Element refElement) {
        final String ceStyle = doc.cfg.valeurParametreElement(refElement, "style", null);
        if (ceStyle == null || ceStyle.equals("")) {
            LOG.error("appliquer(JaxeDocument, int, int, Element) - Pas d'attribut param pour le style");
            return;
        }
        
        if (start >= end)
            return;
        
        final FonctionAjStyle fct = new FonctionAjStyle(refElement);
        if (!fct.appliquer(doc, start, end))
            Toolkit.getDefaultToolkit().beep();
    }
    
    /**
     * Coupe l'élément du style en 2, retourne le nouvel élément JEStyle créé après celui-ci.
     */
    @Override
    public JaxeElement couper(final Position pos) {
        final int offsetpos = pos.getOffset();
        if (debut.getOffset() >= offsetpos || fin.getOffset() < offsetpos) {
            LOG.error("JEStyle: coupure impossible en " + offsetpos + " pour " + noeud.getNodeName() + " debut=" + debut.getOffset() + " fin=" + fin.getOffset());
            return(null);
        }
        final JEStyle jst = new JEStyle(doc);
        jst.noeud = noeud.cloneNode(false);
        jst.refElement = refElement;
        jst.ceStyle = new String(ceStyle);
        try {
            jst.debut = pos;
            jst.fin = fin;
            fin = doc.createPosition(offsetpos - 1);
        } catch (final BadLocationException ex) {
            LOG.error("couper() - BadLocationException", ex);
        }
        doc.dom2JaxeElement.put(jst.noeud, jst);
        Node suivant;
        for (Node n = noeud.getFirstChild(); n != null; n = suivant) {
            suivant = n.getNextSibling();
            final JaxeElement je = doc.getElementForNode(n);
            if (je.debut.getOffset() < offsetpos && je.fin.getOffset() >= offsetpos) {
                final JaxeElement je2 = je.couper(pos); // coupure d'un texte ou d'un style
                noeud.removeChild(je2.noeud);
                jst.noeud.appendChild(je2.noeud);
            } else if (je.debut.getOffset() >= offsetpos) {
                noeud.removeChild(je.noeud);
                jst.noeud.appendChild(je.noeud);
            }
        }
        if (noeud.getNextSibling() != null)
            noeud.getParentNode().insertBefore(jst.noeud, noeud.getNextSibling());
        else
            noeud.getParentNode().appendChild(jst.noeud);
        return(jst);
        
    }

    /**
     * fusionne cet élément avec celui donné, dans le DOM (aucun changement du
     * texte)
     */
    @Override
    public void fusionner(final JaxeElement el) {
        if (!((noeud.getNextSibling() == el.noeud) || (el.noeud.getNextSibling() == noeud))) {
            LOG.error("fusion impossible entre 2 styles (ils ne sont pas adjacents)");
            return;
        }
        if (!(el instanceof JEStyle))
            return;
        final JEStyle jes = (JEStyle) el;
        if (memeStyle((JEStyle)el)) {
            if (noeud.getNextSibling() == el.noeud) {
                Node suivant;
                for (Node n = el.noeud.getFirstChild(); n != null; n = suivant) {
                    suivant = n.getNextSibling();
                    noeud.appendChild(n);
                    final JaxeElement jen = doc.getElementForNode(n);
                    final JaxeElement jep = doc.getElementForNode(n.getPreviousSibling());
                    if ((jep instanceof JETexte && jen instanceof JETexte) ||
                            (jep instanceof JEStyle && jen instanceof JEStyle && ((JEStyle)jep).memeStyle((JEStyle)jen)))
                        jep.fusionner(jen);
                }
                fin = el.fin;
            } else {
                Node precedent;
                for (Node n = el.noeud.getLastChild(); n != null; n = precedent) {
                    precedent = n.getPreviousSibling();
                    noeud.insertBefore(n, noeud.getFirstChild());
                    final JaxeElement jen = doc.getElementForNode(n);
                    final JaxeElement je2 = doc.getElementForNode(n.getNextSibling());
                    if ((jen instanceof JETexte && je2 instanceof JETexte) ||
                            (jen instanceof JEStyle && je2 instanceof JEStyle && ((JEStyle)jen).memeStyle((JEStyle)je2)))
                        jen.fusionner(je2);
                }
                debut = el.debut;
            }
            el.getParent().supprimerEnfantDOM(el);
        }
    }
    
    public boolean memeStyle(final JEStyle targetJE) {
        boolean result = (refElement == targetJE.refElement);
        /*if (result) {
            final String[] sourceStyles = ceStyle.split(";");
            final String[] targetStyles = targetJE.ceStyle.split(";");
            Arrays.sort(sourceStyles);
            Arrays.sort(targetStyles);
            final List<String> soStList = Arrays.asList(sourceStyles);
            final List<String> taStList = Arrays.asList(targetStyles);
            result = soStList.containsAll(taStList) && taStList.containsAll(soStList); 
        }*/
        return result;
    }
    
}
