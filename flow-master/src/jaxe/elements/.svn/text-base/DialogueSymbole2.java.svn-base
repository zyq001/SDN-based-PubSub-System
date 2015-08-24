/*
Jaxe - Editeur XML en Java

Copyright (C) 2002 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conformément aux dispositions de la Licence Publique Générale GNU, telle que publiée par la Free Software Foundation ; version 2 de la licence, ou encore (à votre choix) toute version ultérieure.

Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans même la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de détail, voir la Licence Publique Générale GNU .

Vous devez avoir reçu un exemplaire de la Licence Publique Générale GNU en même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.elements;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import jaxe.JaxeResourceBundle;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Affiche un dialogue permettant de choisir un symbole parmis des caractères UNICODE.
 */
public class DialogueSymbole2 extends JDialog implements ActionListener {
    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(DialogueSymbole.class);
    
    final static String[][] symboles = {
        {"\u0393", "\u0394", "\u0398", "\u039B", "\u039E", "\u03A0", "\u03A3", "\u03A5", "\u03A6", "\u03A7",
            "\u03A8", "\u03A9"}, //grec_majuscules
        {"\u03B1", "\u03B2", "\u03B3", "\u03B4", "\u03B5", "\u03B6", "\u03B7", "\u03B8",
            "\u03B9", "\u03BA", "\u03BB", "\u03BC", "\u03BD", "\u03BE", "\u03BF", "\u03C0", "\u03C1", "\u03C2", "\u03C3",
            "\u03C4", "\u03C5", "\u03C6", "\u03C7", "\u03C8", "\u03C9"}, // grec_minuscules
        {"\u03D1", "\u03D5", "\u03D6"}, // grec_symboles
        {"\u00AC", "\u00B1", "\u00D7", "\u2113", "\u2102", "\u2115", "\u211A", "\u211D", "\u2124", "\u212B",
            "\u2190", "\u2192", "\u2194", "\u21D0", "\u21D2", "\u21D4",
            "\u2200", "\u2202", "\u2203", "\u2205", "\u2207", "\u2208", "\u2209", "\u2211",
            "\u221D", "\u221E",
            "\u2227", "\u2228", "\u2229", "\u222A", "\u222B", "\u223C", "\u2248", "\u2260", "\u2261", "\u2264", "\u2265", "\u2282"}, // maths
        {"\uD835\uDC9C", "\u212C", "\uD835\uDC9E", "\uD835\uDC9F", "\u2130", "\u2131", "\uD835\uDCA2", "\u210B",
            "\u2110", "\uD835\uDCA5", "\uD835\uDCA6", "\u2112", "\u2133", "\uD835\uDCA9", "\uD835\uDCAA",
            "\uD835\uDCAB", "\uD835\uDCAC", "\u211B", "\uD835\uDCAE", "\uD835\uDCAF", "\uD835\uDCB0",
            "\uD835\uDCB1", "\uD835\uDCB2", "\uD835\uDCB3", "\uD835\uDCB4", "\uD835\uDCB5"} // lettres maj. cursives
    };
    final static int nbcol = 13; // nombre de symboles par ligne
    
    Element el;
    JFrame jframe;
    boolean valide = false;
    JLabel[] labels;
    int ichoix = -1;

    public DialogueSymbole2(final JFrame jframe, final Element el) {
        super(jframe, JaxeResourceBundle.getRB().getString("symbole.Insertion"), true);
        this.jframe = jframe;
        this.el = el;
        
        int nbsymboles = 0;
        for (int i=0; i<symboles.length; i++)
            nbsymboles += symboles[i].length;
        
        Node ntexte = el.getFirstChild();
        String texte;
        if (ntexte != null)
            texte = ntexte.getNodeValue();
        else
            texte = null;
        final JPanel cpane = new JPanel(new BorderLayout());
        setContentPane(cpane);
        int nblignes = 0;
        for (int i=0; i<symboles.length; i++)
            nblignes += Math.ceil((double)symboles[i].length / nbcol);
        final GridLayout grille = new GridLayout(nblignes, nbcol, 10, 10);
        final JPanel spane = new JPanel(grille);
        cpane.add(spane, BorderLayout.CENTER);
        
        final Font STIXFontRegular = JESymbole2.getSTIXFont();
        
        ichoix = 0;
        final MyMouseListener ecouteur = new MyMouseListener();
        labels = new JLabel[nbsymboles];
        int num = 0;
        int x = 0;
        for (int i=0; i<symboles.length; i++) {
            for (int j=0; j<symboles[i].length; j++) {
                if (texte != null && texte.equals(symboles[i][j]))
                    ichoix = num;
                final JLabel label = new JLabel(symboles[i][j], SwingConstants.CENTER);
                label.setFont(STIXFontRegular);
                label.addMouseListener(ecouteur);
                labels[num] = label;
                spane.add(label);
                num++;
                x++;
                if (x >= nbcol)
                    x = 0;
            }
            if (x != 0) {
                for (int k=x; k<nbcol; k++)
                    spane.add(new JLabel());
                x = 0;
            }
        }

        final JPanel bpane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        final JButton boutonAnnuler = new JButton(JaxeResourceBundle.getRB().getString("bouton.Annuler"));
        boutonAnnuler.addActionListener(this);
        boutonAnnuler.setActionCommand("Annuler");
        bpane.add(boutonAnnuler);
        final JButton boutonOK = new JButton(JaxeResourceBundle.getRB().getString("bouton.OK"));
        boutonOK.addActionListener(this);
        boutonOK.setActionCommand("OK");
        bpane.add(boutonOK);
        cpane.add(bpane, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(boutonOK);
        choix(ichoix);
        //pack(); pb avec les sélections: la taille calculée est trop petite !
        setSize(400, 400);
        if (jframe != null) {
            final Rectangle r = jframe.getBounds();
            setLocation(r.x + r.width/4, r.y + r.height/4);
        } else {
            final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            setLocation((screen.width - getSize().width)/3, (screen.height - getSize().height)/3);
        }
    }

    public boolean afficher() {
        if (ichoix == -1)
            return false;
        setVisible(true);
        return valide;
    }

    public String getCaracteres() {
        if (ichoix == -1)
            return(null);
        final JLabel label = labels[ichoix];
        return(label.getText());
    }
    
    public void actionPerformed(final ActionEvent e) {
        final String cmd = e.getActionCommand();
        if ("OK".equals(cmd)) {
            valide = true;
            setVisible(false);
        } else if ("Annuler".equals(cmd)) {
            valide = false;
            setVisible(false);
        }
    }

    protected void choix(final int ich) {
        if (ichoix != -1) {
            final JLabel label = labels[ichoix];
            label.setBorder(null);
        }
        ichoix = ich;
        final JLabel label = labels[ichoix];
        label.setBorder(BorderFactory.createLineBorder(Color.darkGray));
    }
    
    class MyMouseListener extends MouseAdapter {
        public MyMouseListener() {
            super();
        }
        @Override
        public void mouseClicked(final MouseEvent e) {
            final Component c = e.getComponent();
            for (int i=0; i<labels.length; i++)
                if (labels[i] == c) {
                    choix(i);
                    if (e.getClickCount() == 2) {
                        valide = true;
                        setVisible(false);
                    }
                }
        }
    }
}
