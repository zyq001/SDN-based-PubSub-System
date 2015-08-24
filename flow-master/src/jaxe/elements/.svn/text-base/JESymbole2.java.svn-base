/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conformément aux dispositions de la Licence Publique Générale GNU, telle que publiée par la Free Software Foundation ; version 2 de la licence, ou encore (à votre choix) toute version ultérieure.

Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans même la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de détail, voir la Licence Publique Générale GNU .

Vous devez avoir reçu un exemplaire de la Licence Publique Générale GNU en même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.elements;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.text.Position;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.log4j.Logger;

import jaxe.JaxeDocument;
import jaxe.JaxeElement;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Elément représentant des symboles avec des caractères UNICODE nécessitant une police UNICODE comme STIX pour l'affichage.
 * Type d'élément Jaxe: 'symbole2'
 */
public class JESymbole2 extends JaxeElement {
    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(JESymbole.class);
    
    public final static String defaultSrcAttr = "nom";
    public String srcAttr = defaultSrcAttr; // pour la compatibilité avec l'ancien symbole
    
    // équivalences entre les vieilles images des symboles et les caractères unicodes
    protected static final String[][] vieux_symboles = {
        {"symboles/grec-minuscules/alpha.png", "\u03B1"},
        {"symboles/grec-minuscules/beta.png", "\u03B2"},
        {"symboles/grec-minuscules/gamma.png", "\u03B3"},
        {"symboles/grec-minuscules/delta.png", "\u03B4"},
        {"symboles/grec-minuscules/epsilon.png", "\u03B5"},
        {"symboles/grec-minuscules/zeta.png", "\u03B6"},
        {"symboles/grec-minuscules/eta.png", "\u03B7"},
        {"symboles/grec-minuscules/theta.png", "\u03B8"},
        {"symboles/grec-minuscules/iota.png", "\u03B9"},
        {"symboles/grec-minuscules/kappa.png", "\u03BA"},
        {"symboles/grec-minuscules/lambda.png", "\u03BB"},
        {"symboles/grec-minuscules/mu.png", "\u03BC"},
        {"symboles/grec-minuscules/nu.png", "\u03BD"},
        {"symboles/grec-minuscules/xi.png", "\u03BE"},
        {"symboles/grec-minuscules/omicron.png", "\u03BF"},
        {"symboles/grec-minuscules/pi.png", "\u03C0"},
        {"symboles/grec-minuscules/rho.png", "\u03C1"},
        {"symboles/grec-majuscules/sigmaf.png", "\u03C2"},
        {"symboles/grec-minuscules/sigma.png", "\u03C3"},
        {"symboles/grec-minuscules/tau.png", "\u03C4"},
        {"symboles/grec-minuscules/upsilon.png", "\u03C5"},
        {"symboles/grec-minuscules/phi2.png", "\u03C6"},
        {"symboles/grec-minuscules/chi.png", "\u03C7"},
        {"symboles/grec-minuscules/psi.png", "\u03C8"},
        {"symboles/grec-minuscules/omega.png", "\u03C9"},
        
        {"symboles/grec-majuscules/Gamma.png", "\u0393"},
        {"symboles/grec-majuscules/Delta.png", "\u0394"},
        {"symboles/grec-majuscules/Theta.png", "\u0398"},
        {"symboles/grec-majuscules/Lambda.png", "\u039B"},
        {"symboles/grec-majuscules/Xi.png", "\u039E"},
        {"symboles/grec-majuscules/Pi.png", "\u03A0"},
        {"symboles/grec-majuscules/Sigma.png", "\u03A3"},
        {"symboles/grec-majuscules/Phi.png", "\u03A6"},
        {"symboles/grec-majuscules/Psi.png", "\u03A8"},
        {"symboles/grec-majuscules/Omega.png", "\u03A9"},
        
        {"symboles/grec-majuscules/thetasym.png", "\u03D1"},
        {"symboles/grec-minuscules/phi.png", "\u03D5"},
        {"symboles/grec-minuscules/piv.png", "\u03D6"},
        
        {"symboles/maths/not.png", "\u00AC"},
        {"symboles/maths/plusmn.png", "\u00B1"},
        {"symboles/maths/larr.png", "\u2190"},
        {"symboles/maths/rarr.png", "\u2192"},
        {"symboles/maths/harr.png", "\u2194"},
        {"symboles/maths/ldarr.png", "\u21D0"},
        {"symboles/maths/rdarr.png", "\u21D2"},
        {"symboles/maths/hdarr.png", "\u21D4"},
        {"symboles/maths/forall.png", "\u2200"},
        {"symboles/maths/part.png", "\u2202"},
        {"symboles/maths/exist.png", "\u2203"},
        {"symboles/maths/nabla.png", "\u2207"},
        {"symboles/maths/isin.png", "\u2208"},
        {"symboles/maths/infin.png", "\u221E"},
        {"symboles/maths/cap.png", "\u2229"},
        {"symboles/maths/cup.png", "\u222A"},
        {"symboles/maths/sim.png", "\u223C"},
        {"symboles/maths/asymp.png", "\u2248"},
        {"symboles/maths/ne.png", "\u2260"},
        {"symboles/maths/le.png", "\u2264"},
        {"symboles/maths/ge.png", "\u2265"},
        {"symboles/maths/sub.png", "\u2282"}
    };
    
    private static Font STIXFontRegular = null;
    private JLabel label = null;
    private JESymbole2MouseListener listener;
    public float alignementY = (float)0.7;
    
    
    public JESymbole2(final JaxeDocument doc) {
        this.doc = doc;
    }
    
    @Override
    public void init(final Position pos, final Node noeud) {
        srcAttr = doc.cfg.valeurParametreElement(refElement, "srcAtt", defaultSrcAttr);
        
        if (noeud.getFirstChild() == null) {
            // conversion de symbole vers symbole2
            final Element el = (Element)noeud;
            final String nomf = el.getAttribute(srcAttr);
            if (!"".equals(nomf)) {
                String texte = null;
                for (int i=0; i<vieux_symboles.length; i++)
                    if (nomf.equals(vieux_symboles[i][0])) {
                        texte = vieux_symboles[i][1];
                        break;
                    }
                if (texte != null) {
                    el.removeAttribute(srcAttr);
                    final Node ntexte = doc.DOMdoc.createTextNode(texte);
                    el.appendChild(ntexte);
                }
            }
        }
        affichageLabel();
        label.setAlignmentY(alignementY);
        
        listener = new JESymbole2MouseListener(this, doc.jframe);
        label.addMouseListener(listener);
        final Position newpos = insertComponent(pos, label);
    }
    
    public static Font getSTIXFont() {
        if (STIXFontRegular != null)
            return(STIXFontRegular);
        try {
            STIXFontRegular = Font.createFont(Font.TRUETYPE_FONT, JESymbole2.class.getResourceAsStream("/jaxe/polices/STIXSubset-Regular.ttf"));
            STIXFontRegular = STIXFontRegular.deriveFont(Font.PLAIN, 15);
        } catch (FontFormatException ex) {
            LOG.error("JESymbole2 Font.createFont STIXSubset-Regular.ttf", ex);
            return(null);
        } catch (IOException ex) {
            LOG.error("JESymbole2 Font.createFont STIXSubset-Regular.ttf", ex);
            return(null);
        }
        return(STIXFontRegular);
    }
    
    @Override
    public Node nouvelElement(final Element refElement) {
        final Element newel = nouvelElementDOM(doc, refElement);
        if (newel == null)
            return null;
        
        final DialogueSymbole2 dlg = new DialogueSymbole2(doc.jframe, newel);
        if (!dlg.afficher())
            return null;
        final String texte = dlg.getCaracteres();
        final Node ntexte = doc.DOMdoc.createTextNode(texte);
        newel.appendChild(ntexte);
        
        return newel;
    }
    
    @Override
    public void afficherDialogue(final JFrame jframe) {
        final Element el = (Element)noeud;

        final DialogueSymbole2 dlg = new DialogueSymbole2(doc.jframe, el);
        if (!dlg.afficher())
            return;
        final String texte = dlg.getCaracteres();
        Node ntexte;
        if (noeud.getFirstChild() != null) {
            ntexte = noeud.getFirstChild();
            ntexte.setNodeValue(texte);
        } else {
            ntexte = doc.DOMdoc.createTextNode(texte);
            noeud.appendChild(ntexte);
        }
        
        majAffichage();
    }
    
    @Override
    public void majAffichage() {
        affichageLabel();
        doc.imageChanged(label);
    }
    
    protected void affichageLabel() {
        if (label == null)
            label = new JLabel();
        else
            label.setEnabled(true);
        String texte;
        if (noeud.getFirstChild() != null)
            texte = noeud.getFirstChild().getNodeValue();
        else
            texte = "?";
        label.setText(texte);
        label.setFont(getSTIXFont());
    }
    
    @Override
    public void selection(final boolean select) {
        super.selection(select);
        label.setEnabled(!select);
    }
    
    @Override
    public void effacer() {
        super.effacer();
        if (listener != null) {
            label.removeMouseListener(listener);
            listener = null;
        }
    }
    
    class JESymbole2MouseListener extends MouseAdapter {
        JESymbole2 jei;
        JFrame jframe;
        public JESymbole2MouseListener(final JESymbole2 obj, final JFrame jframe) {
            super();
            jei = obj;
            this.jframe = jframe;
        }
        @Override
        public void mouseClicked(final MouseEvent e) {
            jei.afficherDialogue(jframe);
        }
    }
}
