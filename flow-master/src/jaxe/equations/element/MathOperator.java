/*
Jaxe - Editeur XML en Java

Copyright (C) 2003 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conformément aux dispositions de la Licence Publique Générale GNU, telle que publiée par la Free Software Foundation ; version 2 de la licence, ou encore (à votre choix) toute version ultérieure.

Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans même la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de détail, voir la Licence Publique Générale GNU .

Vous devez avoir reçu un exemplaire de la Licence Publique Générale GNU en même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.equations.element;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;

/**
 * This class presents a operator, like "(" or "*"
 *
 * @author <a href="mailto:stephan@vern.chem.tu-berlin.de">Stephan Michels</a>
 * @author <a href="mailto:sielaff@vern.chem.tu-berlin.de">Marco Sielaff</a>
 * @version %I%, %G%
 */
public class MathOperator extends MathElement
{

  /** The XML element from this class */
  public final static String ELEMENT = "mo";

  /** Attribute name of the stretchy property */
  public final static String ATTRIBUTE_STRETCHY = "stretchy";
  
    private boolean stretchy = true;
    private double lspace = 0; // left space, in em
    private double rspace = 0; // right space, in em

    /**
     * Enables, or disables if the operator should fit his
   * size to the size of the container
     *
     * @param stretchy True, if the operater should fit this size
     */
    public void setStretchy(final boolean stretchy)
    {
        this.stretchy = stretchy;
    }
    
    /**
     * Set left space in em
     */
    public void setLspace(final double lspace) {
        this.lspace = lspace;
    }
    
    /**
     * Set right space in em
     */
    public void setRspace(final double rspace) {
        this.rspace = rspace;
    }
    
    /**
     * Paints a delimitier
     *
     * @param g The graphics context to use for painting 
     * @param posX The first left position for painting 
     * @param posY The position of the baseline
     * @param upperSymbol The symbol for upper edge
     * @param middleSymbol The symbol for middle part
     * @param lowerSymbol The symbol for lower edge
     */
    private void paintDelimiter(final Graphics g, final int posX, final int posY,
        final char upperSymbol, final char middleSymbol, final char lowerSymbol)
    {
        final int height = getExprHeight();
        final int aheight = getExprAscentHeight();
        final Graphics2D g2d = (Graphics2D) g;
        final GlyphVector gvtop = getFont().createGlyphVector(g2d.getFontRenderContext(), new char[]{ upperSymbol });
        final GlyphVector gvmid = getFont().createGlyphVector(g2d.getFontRenderContext(), new char[]{ middleSymbol });
        final GlyphVector gvbot = getFont().createGlyphVector(g2d.getFontRenderContext(), new char[]{ lowerSymbol });
        
        final double gvtopHeight = gvtop.getVisualBounds().getHeight() - 1;
        final double gvmidHeight = gvmid.getVisualBounds().getHeight() - 1;
        final double gvbotHeight = gvbot.getVisualBounds().getHeight() - 1;
        
        if (height - gvtopHeight - gvbotHeight > 0) {
            final double goal = height - gvtopHeight - gvbotHeight + 3;
            double sy = goal / gvmidHeight;
            AffineTransform transform = AffineTransform.getScaleInstance(1, sy);
            gvmid.setGlyphTransform(0, transform);
            for (int i=0; i<2; i++)
                if (gvmid.getVisualBounds().getHeight() != goal) {
                    sy = sy * goal / gvmid.getVisualBounds().getHeight();
                    transform = AffineTransform.getScaleInstance(1, sy);
                    gvmid.setGlyphTransform(0, transform);
                } else
                    break;
            g2d.drawGlyphVector(gvmid, posX, posY - aheight + (int)Math.round(gvtopHeight) - (int)Math.round(gvmid.getVisualBounds().getY()) - 1);
        }
        g2d.drawGlyphVector(gvtop, posX, posY - aheight + (int)Math.round(gvtopHeight) - (int)Math.round(gvtopHeight + gvtop.getVisualBounds().getY()));
        g2d.drawGlyphVector(gvbot, posX, posY + height - aheight - (int)Math.round(gvbotHeight + gvbot.getVisualBounds().getY()));
    }

  /**
   * Paints a delimitier
   *
   * @param g The graphics context to use for painting 
   * @param posX The first left position for painting 
   * @param posY The position of the baseline 
   * @param upperSymbol The symbol for upper edge
   * @param middleSymbol The symbol for middle part
   * @param connectSymbol The symbol for connecting 
                          the middle part with the edges
   * @param lowerSymbol The symbol for lower edge
   */
    private void paintCurlyDelimiter(final Graphics g, final int posX, final int posY,
                                                                     final char upperSymbol, final char middleSymbol,
                                                                     final char connectSymbol, final char lowerSymbol)
    {
        final int height = getExprHeight();
        final int middle = posY - getMiddleShift();

        g.setColor(Color.black);
        g.setFont(getFont());

        final int fontascent = getFontMetrics().getAscent();
        int countparts = height / fontascent + 1;
        final int halfcount = countparts / 2;

        if (countparts % 2 == 0)
            countparts++;

        for (int i = 1; i < halfcount; i++) {
            g.drawString(""+connectSymbol, posX, posY - (fontascent - 1) * i);
            g.drawString(""+connectSymbol, posX, posY + (fontascent - 1) * i);
        }
        g.drawString(""+middleSymbol, posX, posY);
        g.drawString(""+upperSymbol, posX, posY - (fontascent - 1) * halfcount);
        g.drawString(""+lowerSymbol, posX, posY + (fontascent - 1) * halfcount);
    }

  /**
   * Paints a horizontal delimitier
   *
   * @param g The graphics context to use for painting 
   * @param posX The first left position for painting 
   * @param posY The position of the baseline 
   * @param upperSymbol The symbol for upper edge
   * @param middleSymbol The symbol for middle part
   * @param connectSymbol The symbol for connecting 
                          the middle part with the edges
   * @param lowerSymbol The symbol for lower edge
   */
    private void paintCurlyDelimiterHorizontal(final Graphics g, final int posX, final int posY,
                                                                                         final char upperSymbol,
                                                                                         final char middleSymbol,
                                                                                         final char connectSymbol,
                                                                                         final char lowerSymbol)
    {
        final int height = getParent().getWidth(true);
        final int middle = posY - getMiddleShift();
        final Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.setFont(getFont());
        final AffineTransform at = new AffineTransform();

        at.translate(posX, posY);
        at.rotate(Math.PI / 2.0);
        at.translate(-posX, -middle);
        g2d.setTransform(at);

        final int fontascent = getFontMetrics().getAscent();
        int countparts = height / fontascent + 1;
        final int halfcount = countparts / 2;

        if (countparts % 2 == 0)
            countparts++;

        for (int i = 1; i < halfcount; i++)
        {
            g2d.drawString(""+connectSymbol, posX, posY - (fontascent - 1) * i);
            g2d.drawString(""+connectSymbol, posX, posY + (fontascent - 1) * i);
        }
        g2d.drawString(""+middleSymbol, posX, posY);
        g2d.drawString(""+upperSymbol, posX, posY - (fontascent - 1) * halfcount);
        g2d.drawString(""+lowerSymbol, posX, posY + (fontascent - 1) * halfcount);

        g2d.setTransform(new AffineTransform());
    }

  /**
   * Paints this element
   *
   * @param g The graphics context to use for painting 
   * @param posX The first left position for painting 
   * @param posY The position of the baseline 
   */
    @Override
    public void paint(final Graphics g, int posX, final int posY)
    {
        if (lspace != 0) {
            final int empix = getFontMetrics().charWidth('A');
            posX += (int)Math.round(lspace*empix);
        }
        if (getText().length() == 1
                        && "[{(|)}]\u222B".indexOf(getText().charAt(0)) >= 0
                        && stretchy)
        {
            final int ascent = getParent().getAscentHeight(false);
            final int descent = getParent().getDescentHeight(false);
            final int height = ascent + descent;
            //final int width = (int) (height * 0.2);
            //final int halfwidth = width / 2;
            //final int middle = posY - getMiddleShift();
            //final int fontascent = getFontMetrics().getAscent();
            //final int countparts = height / fontascent + 1;

            if (getText().equals("("))
            {
                if (height <= getFontMetrics().getHeight())
                {
                    g.setFont(getFont());
                    g.drawString(getText(), posX, posY);
                }
                else
                    paintDelimiter(g, posX, posY, '\u239B', '\u239C', '\u239D');
            }
            else if (getText().equals(")"))
            {
                if (height <= getFontMetrics().getHeight())
                {
                    g.setFont(getFont());
                    g.drawString(getText(), posX, posY);
                }
                else
                    paintDelimiter(g, posX, posY, '\u239E', '\u239F', '\u23A0');
            }
            else if (getText().equals("["))
            {
                if (height <= getFontMetrics().getHeight())
                {
                    g.setFont(getFont());
                    g.drawString(getText(), posX, posY);
                }
                else
                    paintDelimiter(g, posX, posY, '\u23A1', '\u23A2', '\u23A3');
            }
            else if (getText().equals("]"))
            {
                if (height <= getFontMetrics().getHeight())
                {
                    g.setFont(getFont());
                    g.drawString(getText(), posX, posY);
                }
                else
                    paintDelimiter(g, posX, posY, '\u23A4', '\u23A5', '\u23A6');

            }
            else if (getText().equals("{"))
            {
                if (height <= getFontMetrics().getHeight())
                {
                    g.setFont(getFont());
                    g.drawString(getText(), posX, posY);
                }
                else
                    paintCurlyDelimiter(g, posX, posY, '\u23A7', '\u23A8', '\u23AA', '\u23A9');
            }
            else if (getText().equals("}"))
            {
                if (height <= getFontMetrics().getHeight())
                {
                    g.setFont(getFont());
                    g.drawString(getText(), posX, posY);
                }
                else
                    paintCurlyDelimiter(g, posX, posY, '\u23AB', '\u23AC', '\u23AA', '\u23AD');
            }
            else if (getText().equals("|"))
            {
                g.drawLine(posX + 2, posY - ascent, posX + 2, posY + descent);
            }
            else if (getText().equals("\u222B"))
            {
                paintDelimiter(g, posX, posY, '\u2320', '\u23AE', '\u2321');
            }
        }
        else if (getText().length() == 1
                         && "\uFE37\uFE38".indexOf(getText().charAt(0)) >= 0)
        {
            //final int width = getParent().getWidth(true);
            //final int halfwidth = width / 2;
            //final int height = Math.max((int) (width * 0.2), 2);
            //final int halfheight = height / 2;

            if (getText().equals("\uFE37"))
                paintCurlyDelimiterHorizontal(g, posX, posY, '\u23A7', '\u23A8', '\u23AA', '\u23A9');
            else if (getText().equals("\uFE38"))
                paintCurlyDelimiterHorizontal(g, posX, posY, '\u23AB', '\u23AC', '\u23AA', '\u23AD');
        }
        else if (getText().length() == 1
                         && "\u2211\u220F".indexOf(getText().charAt(0)) >= 0
                         && stretchy)
        {
            g.setColor(Color.black);
            if (getExprHeight() > getFontMetrics().getHeight()) {
                final Font bigfont = getMathBase().getFont(getFontSize() * 2);
                g.setFont(bigfont);
            } else
                g.setFont(getFont());
            if ("\u2211".indexOf(getText().charAt(0)) >= 0)
                g.drawString("\u2211", posX, posY);
            else
                g.drawString("\u220F", posX, posY);
            g.setFont(getFont());
        }
        else if (getText().equals("\u00AF") && stretchy) // over bar
        {
            final int width = getParent().getWidth(false) - 2;
            g.drawLine(posX, posY, posX + width, posY);
        }
        else if (getText().equals("^") && stretchy) // hat
        {
            final int width = getParent().getWidth(false) - 3;
            g.drawLine(posX, posY, posX + width/2, posY - 3);
            g.drawLine(posX + width/2, posY - 3, posX + width, posY);
        }
        else if (".".equals(getText()) || "..".equals(getText()) || "...".equals(getText()))
        {
            g.setFont(getFont());
            g.drawString(getText(), posX + 1, posY);
        }
        else
        {
            g.setFont(getFont());
            g.drawString(getText(), posX, posY);
        }
    }
    
    private MathElement getExpr() {
        if (getText().equals("\u222B") || "\u2211\u220F".contains(getText())) {
            MathRow row;
            if (getParent() instanceof MathRow)
                row = (MathRow)getParent();
            else
                row = (MathRow)(getParent().getParent());
            // test intégrale/sommation/produit double
            final MathElement contenu = row.getMathElement(1);
            if (!(contenu instanceof MathRow && contenu.getMathElementCount() > 0))
                return(contenu);
            final MathElement interieur1 = contenu.getMathElement(0);
            if (!(interieur1 instanceof MathRow && interieur1.getMathElementCount() > 0))
                return(contenu);
            final MathElement interieur2 = interieur1.getMathElement(0);
            if (!(interieur2 instanceof MathUnderOver || interieur2 instanceof MathUnder ||
                    interieur2 instanceof MathOver || interieur2 instanceof MathOperator))
                return(contenu);
            MathElement op;
            if (interieur2 instanceof MathUnderOver || interieur2 instanceof MathUnder || interieur2 instanceof MathOver)
                op = interieur2.getMathElement(0);
            else
                op = interieur2;
            if (!("\u222B".equals(op.getText()) || "\u2211\u220F".contains(op.getText())))
                return(contenu);
            return(interieur1.getMathElement(1));
        } else
            return(getParent());
    }
    
    private int getExprHeight() {
        return(getExpr().getHeight(false));
    }

    private int getExprAscentHeight() {
        return(getExpr().getAscentHeight(false));
    }

    private int getExprDescentHeight() {
        return(getExpr().getDescentHeight(false));
    }

  /**
   * Return the current width of this element 
   *
   * @param dynamicParts Should be true, if the calculation consider the elements,
   *                     which has not fixed sizes
   *
   * @return Width of this element 
   */
    @Override
    public int getWidth(final boolean dynamicParts)
    {
        int totalspace = 0;
        if (lspace != 0 || rspace != 0) {
            final int empix = getFontMetrics().charWidth('A');
            final int lspacepix = (int)Math.round(lspace*empix);
            final int rspacepix = (int)Math.round(rspace*empix);
            totalspace = lspacepix + rspacepix;
        }
        if (getText().length() == 1) {
            final char firstchar = getText().charAt(0);
            // if ("[{()}]".indexOf(firstchar)>=0)
            // return (int)((parent.getAscentHeight(false)+parent.getDescentHeight(false))*0.2);
            // else
            if ("|".indexOf(firstchar) >= 0)
                return 5 + totalspace;
            else if ("\uFE37\uFE38".indexOf(firstchar) >= 0)
                return 1 + totalspace;
            else if ("\u222B".indexOf(firstchar) >= 0)
                return getMathBase().getFontMetrics(getFontSize()*2).stringWidth(getText()) + totalspace;
            else if ("\u2211\u220F".indexOf(firstchar) >= 0) {
                if (getExprHeight() > getFontMetrics().getHeight())
                    return getMathBase().getFontMetrics(getFontSize()*2).stringWidth(getText()) + totalspace;
                else
                    return getFontMetrics().stringWidth(getText()) + totalspace;
            } else if ("^\u00AF".indexOf(firstchar) >= 0 && stretchy && dynamicParts)
                return getParent().getWidth(false) - 2;
        }
        return getFontMetrics().stringWidth(getText()) + totalspace;
    }

  /**
   * Return the current height of this element 
   *
   * @param dynamicParts Should be true, if the calculation consider the elements,
   *                     which has not fixed sizes
   *
   * @return Height of this element
   */
    @Override
    public int getHeight(final boolean dynamicParts)
    {
        return getAscentHeight(dynamicParts) + getDescentHeight(dynamicParts);
    }

  /**
   * Return the current height of the upper part
   * of this component from the baseline
   *
   * @param dynamicParts Should be true, if the calculation consider the elements,
   *                     which has not fixed sizes
   *
   * @return Height of the upper part 
   */ 
    @Override
    public int getAscentHeight(final boolean dynamicParts)
    {
        if (getText().length() == 1 && "[()]\u222B".indexOf(getText().charAt(0)) >= 0) {
            if (!dynamicParts || !stretchy)
                return getFontMetrics().getAscent();
            return(getExprAscentHeight() + 1);
        } else if (getText().length() == 1 && "{}".indexOf(getText().charAt(0)) >= 0) {
            if (!dynamicParts || !stretchy)
                return getFontMetrics().getAscent();
            final int ascent = getFontMetrics().getAscent();
            final int countparts = getExprHeight() / ascent + 1;

            if ("{}".indexOf(getText().charAt(0)) >= 0)
                if (countparts % 2 == 0)
                    return (int) ((countparts + 1) * ascent * 0.5 + getMiddleShift());
            return (int) (countparts * ascent * 0.5 + getMiddleShift());
        }
        else if (getText().length() == 1
                         && "\u2211\u220F".indexOf(getText().charAt(0)) >= 0
                         && stretchy) {
            if (getExprHeight() > getFontMetrics().getHeight())
                return getMathBase().getFontMetrics(getFontSize() * 2).getAscent();
            else
                return getFontMetrics().getAscent();
        } else if (getText().length() == 1
                         && "\uFE37\uFE38".indexOf(getText().charAt(0)) >= 0)
            return 0;
        else if ("\u00AF".equals(getText()) && stretchy)
            return(3);
        else if ("\u223C".equals(getText()) && stretchy)
            return(getFontMetrics().getAscent()/2);
        else if (".".equals(getText()) || "..".equals(getText()) || "...".equals(getText()))
            return(getFontMetrics().getAscent()/2);
        else
            return getFontMetrics().getAscent();
    }

  /**
   * Return the current height of the lower part
   * of this component from the baseline
   *
   * @param dynamicParts Should be true, if the calculation consider the elements,
   *                     which has not fixed sizes
   *
   * @return Height of the lower part 
   */ 
    @Override
    public int getDescentHeight(final boolean dynamicParts)
    {
        if (getText().length() == 1 && "[()]\u222B".indexOf(getText().charAt(0)) >= 0) {
            if (!dynamicParts || !stretchy)
                return getFontMetrics().getDescent();
            return(getExprDescentHeight() + 1);
        } else if (getText().length() == 1 && "{}".indexOf(getText().charAt(0)) >= 0) {
            if (!dynamicParts || !stretchy)
                return getFontMetrics().getDescent();
            final int ascent = getFontMetrics().getAscent();
            final int countparts = getExprHeight() / ascent + 1;

            if ("{}".indexOf(getText().charAt(0)) >= 0)
                if (countparts % 2 == 0)
                    return (int) ((countparts + 1) * ascent * 0.5 - getMiddleShift());
            return (int) (countparts * ascent * 0.5 - getMiddleShift());
        }
        else if (getText().length() == 1
                         && "\u2211\u220F".indexOf(getText().charAt(0)) >= 0
                         && stretchy) {
            if (getExprHeight() > getFontMetrics().getHeight())
                return getMathBase().getFontMetrics(getFontSize() * 2).getDescent();
            else
                return getFontMetrics().getDescent();
        } else if (getText().length() == 1
                         && "\uFE37\uFE38".indexOf(getText().charAt(0)) >= 0)
            return getFontMetrics().stringWidth("}");
        else
            return getFontMetrics().getDescent();
    }
}
