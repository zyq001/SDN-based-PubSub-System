/*
Jaxe - Editeur XML en Java

Copyright (C) 2003 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conformément aux dispositions de la Licence Publique Générale GNU, telle que publiée par la Free Software Foundation ; version 2 de la licence, ou encore (à votre choix) toute version ultérieure.

Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans même la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de détail, voir la Licence Publique Générale GNU .

Vous devez avoir reçu un exemplaire de la Licence Publique Générale GNU en même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.equations.element;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

/**
 * This class presents a mathematical idenifier, like "x"
 *
 * @author <a href="mailto:stephan@vern.chem.tu-berlin.de">Stephan Michels</a>
 * @author <a href="mailto:sielaff@vern.chem.tu-berlin.de">Marco Sielaff</a>
 * @version %I%, %G%
 */
public class MathIdentifier extends MathText
{
    /** The XML element from this class */
    public final static String ELEMENT = "mi";
    
    /** Attribute name of the mathvariant property */
    public final static String ATTRIBUTE_MATHVARIANT = "mathvariant"; 
    
    private String mathvariant = "italic";
    
    
    /**
     * Sets the mathvariant attribute
     *
     * @param mathvariant mathvariant (normal | bold | italic | bold-italic)
     */
    public void setMathvariant(final String mathvariant)
    {
        this.mathvariant = mathvariant;
    }
    
    /**
     * Paints this element
     *
     * @param g The graphics context to use for painting 
     * @param posX The first left position for painting 
     * @param posY The position of the baseline 
     */ 
    @Override
    public void paint(final Graphics g, final int posX, final int posY)
    {
        final String s = getText();
        final Font f;
        if ("italic".equals(mathvariant))
            f = getItalicFont();
        else if ("bold".equals(mathvariant))
            f = getBoldFont();
        else if ("bold-italic".equals(mathvariant))
            f = getBoldItalicFont();
        else
            f = getFont();
        //if (f.canDisplayUpTo(s) == -1)
        // bug on MacOS X JVM -> workaround...
        boolean canDisplay;
        if (System.getProperty("os.name").startsWith("Mac OS")) {
            canDisplay = true;
            final GlyphVector gv  = f.createGlyphVector(((Graphics2D)g).getFontRenderContext(), s);
            for (int i=0; i<gv.getNumGlyphs(); i++) {
                if (gv.getGlyphCode(i) <= 0) { // < or <= ?
                    canDisplay = false;
                    break;
                }
            }
        } else
            canDisplay = (f.canDisplayUpTo(s) == -1);
        if (canDisplay)
            g.setFont(f);
        else
            g.setFont(getFont());
        g.drawString(s, posX, posY);
    }
    
    public int getRealAscentHeight(final Graphics g)
    {
        final Graphics2D g2d = (Graphics2D) g;
        final GlyphVector gv = getItalicFont().createGlyphVector(g2d.getFontRenderContext(), getText().toCharArray());
        final Rectangle2D r = gv.getVisualBounds();
        double miny = r.getMinY();
        if (miny > 0)
            miny = 0;
        double maxy = r.getMaxY();
        if (maxy > 0)
            maxy = 0;
        return((int)Math.round(maxy - miny));
    }
    
}
