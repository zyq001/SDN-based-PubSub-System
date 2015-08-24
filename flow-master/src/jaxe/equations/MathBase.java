/*
Jaxe - Editeur XML en Java

Copyright (C) 2003 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conformément aux dispositions de la Licence Publique Générale GNU, telle que publiée par la Free Software Foundation ; version 2 de la licence, ou encore (à votre choix) toute version ultérieure.

Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans même la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de détail, voir la Licence Publique Générale GNU .

Vous devez avoir reçu un exemplaire de la Licence Publique Générale GNU en même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.equations;

import org.apache.log4j.Logger;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
//import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.io.InputStream;
import java.io.IOException;
//import java.util.Vector;

import jaxe.equations.element.MathRootElement;

/**
 * The base for creating a MathElement tree. Now based on STIX fonts.
 *
 * @author <a href="mailto:stephan@vern.chem.tu-berlin.de">Stephan Michels</a>
 * @author <a href="mailto:sielaff@vern.chem.tu-berlin.de">Marco Sielaff</a>
 * @author Damien Guillaume
 * @version %I%, %G%
 */
public class MathBase
{
    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(MathBase.class);
    
    // static pour contourner un bug de la JVM d'Apple, se produisant aléatoirement au chargement des polices:
    // http://lists.apple.com/archives/java-dev/2009/Dec/msg00190.html
    // (il y a moins de chances que cela se produise si on ne charge les polices qu'une fois)
    // et il y a aussi ce bug avec Tomcat:
    // http://stackoverflow.com/questions/1751673/font-createfont-leaves-files-in-temp-directory
    private static Font STIXFontRegular = null;
    private static Font STIXFontItalic = null;
    private static Font STIXFontBold = null;
    
    private int inlinefontsize = 15;
    private int displayfontsize = 16;

    private final int minfontsize = 8;
    private final int maxfontsize = 60;

    private final Font[] fonts = new Font[maxfontsize];
    private final Font[] italicFonts = new Font[maxfontsize];
    private final Font[] boldFonts = new Font[maxfontsize];

    private FontMetrics[] fontmetrics = null;

    private boolean debug = false;

    /** Inline mathematical expression */
    public final static int INLINE = 0;

    /** Non inline mathematical expression */
    public final static int DISPLAY = 1;

    private final int mode = INLINE;

    private MathRootElement rootElement;
    
    //private final Vector<Font> goodFonts; // cache for findfont
    
    /**
     * Creates a MathBase
     *
     * @param element Root element of a math tree
     * @param inlinefontsize Size of the preferred font used by inline equations
     * @param displayfontsize Size of the preferred font used by non inline equations
     * @param gcalc Graphics object to use to calculate character sizes (nothing will be painted on it)
     */
    public MathBase(final MathRootElement element, final int inlinefontsize, final int displayfontsize, final Graphics gcalc)
    {
        this(inlinefontsize, displayfontsize, gcalc);
        setRootElement(element);
    }

    /**
     * Creates a MathBase
     *
     * @param element Root element of a math tree
     * @param gcalc Graphics object to use to calculate character sizes (nothing will be painted on it)
     */
    public MathBase(final MathRootElement element, final Graphics gcalc)
    {
        this(element, 15, 16, gcalc);
    }

    /**
     * Creates a MathBase
     *
     * @param inlinefontsize Size of the preferred font used by inline equations
     * @param displayfontsize Size of the preferred font used by non inline equations
     * @param gcalc Graphics object to use to calculate character sizes (nothing will be painted on it)
     */
    public MathBase(final int inlinefontsize, final int displayfontsize, final Graphics gcalc)
    {
        this.inlinefontsize = inlinefontsize;
        this.displayfontsize = displayfontsize;
        
        try {
            InputStream is;
            if (STIXFontRegular == null) {
                is = this.getClass().getResourceAsStream("/jaxe/polices/STIXSubset-Regular.ttf");
                STIXFontRegular = Font.createFont(Font.TRUETYPE_FONT, is);
                is.close();
            }
            if (STIXFontItalic == null) {
                is = this.getClass().getResourceAsStream("/jaxe/polices/STIXSubset-Italic.ttf");
                STIXFontItalic = Font.createFont(Font.TRUETYPE_FONT, is);
                is.close();
            }
            if (STIXFontBold == null) {
                is = this.getClass().getResourceAsStream("/jaxe/polices/STIXSubset-Bold.ttf");
                STIXFontBold = Font.createFont(Font.TRUETYPE_FONT, is);
                is.close();
            }
        } catch (FontFormatException ex) {
            LOG.error("MathBase Font.createFont", ex);
            return;
        } catch (IOException ex) {
            LOG.error("MathBase Font.createFont", ex);
            return;
        }
        for (int i = 0; i < maxfontsize; i++) {
            fonts[i] = STIXFontRegular.deriveFont(Font.PLAIN, i);
            italicFonts[i] = STIXFontItalic.deriveFont(Font.ITALIC, i);
            boldFonts[i] = STIXFontBold.deriveFont(Font.BOLD, i);
        }
        
        if (gcalc != null)
            setupFontMetrics(gcalc);
    }
    
    private void setupFontMetrics(final Graphics gcalc) {
        fontmetrics = new FontMetrics[maxfontsize];
        for (int i = 0; i < maxfontsize; i++)
            fontmetrics[i] = gcalc.getFontMetrics(fonts[i]);
    }
    
    /**
     * Set the root element of a math tree
     *
     * @param element Root element of a math tree
     */
    public void setRootElement(final MathRootElement element)
    {
        if (element == null)
            return;
        
        rootElement = element;
        
        rootElement.setMathBase(this);
        
        if (element.getMode() == MathRootElement.DISPLAY)
            rootElement.setFontSize(displayfontsize);
        else
            rootElement.setFontSize(inlinefontsize);
        
        rootElement.setDebug(isDebug());
    }

    /**
     * Enables, or disables the debug mode
     *
     * @param debug Debug mode
     */
    public void setDebug(final boolean debug)
    {
        this.debug = debug;
        if (rootElement != null)
            rootElement.setDebug(debug);
    }

    /**
     * Indicates, if the debug mode is enabled
     *
     * @return True, if the debug mode is enabled
     */
    public boolean isDebug()
    {
        return debug;
    }

    /**
     * Sets the default font size, which used for the root element
     *
     * @param fontsize Font size
     */
    public void setDefaultFontSize(final int fontsize)
    {
        if (fontsize >= minfontsize || fontsize < maxfontsize)
            this.inlinefontsize = fontsize;
    }

    /**
     * Get the default font size
     *
     * @return Default font size
     */
    public int getDefaultInlineFontSize()
    {
        return inlinefontsize;
    }

    /**
     * Sets the default font size for non inline equations
     *
     * @param fontsize Default font size
     */
    public void setDefaultDisplayFontSize(final int fontsize)
    {
        if (fontsize >= minfontsize || fontsize < maxfontsize)
            this.displayfontsize = fontsize;
    }

    /**
     * Get the default font size for non inline equations
     *
     * @return Default display font size
     */
    public int getDefaultDisplayFontSize()
    {
        return displayfontsize;
    }

    /**
     * Get a font specified by the font size
     *
     * @param fontsize Font size
     *
     * @return Font
     */
    public Font getFont(final int fontsize)
    {
        if (fontsize < minfontsize)
            return fonts[minfontsize];
        if (fontsize > maxfontsize)
            return fonts[maxfontsize - 1];
        return fonts[fontsize];
    }

    public Font getItalicFont(final int fontsize)
    {
        if (fontsize < minfontsize)
            return italicFonts[minfontsize];
        if (fontsize > maxfontsize)
            return italicFonts[maxfontsize - 1];
        return italicFonts[fontsize];
    }
    
    public Font getBoldFont(final int fontsize)
    {
        if (fontsize < minfontsize)
            return boldFonts[minfontsize];
        if (fontsize > maxfontsize)
            return boldFonts[maxfontsize - 1];
        return boldFonts[fontsize];
    }
    
    public Font getBoldItalicFont(final int fontsize)
    {
        // unused experimental on-the-fly transform
        return(getBoldFont(fontsize).deriveFont(java.awt.geom.AffineTransform.getShearInstance(-0.5, 0)));
    }
    
    /*
    public String findFont(final String s, final Font defaultFont) {
        if (goodFonts != null) {
            for (final Font f : goodFonts) {
                final int upto = f.canDisplayUpTo(s);
                if (upto == -1 || upto == s.length())
                    return f.getName();
            }
        }
        final Font[] allfonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for (final Font f : allfonts) {
            final int upto = f.canDisplayUpTo(s);
            if (upto == -1 || upto == s.length()) {
                goodFonts.add(f);
                return f.getName();
            }
        }
        return defaultFont.getName();
    }
    */
    
    /**
     * Get the font metrics specified by the font size
     *
     * @param fontsize Font size
     *
     * @return Font metrics
     */
    public FontMetrics getFontMetrics(final int fontsize)
    {
        if (fontsize < minfontsize)
            return fontmetrics[minfontsize];
        if (fontsize > maxfontsize)
            return fontmetrics[maxfontsize - 1];
        return fontmetrics[fontsize];
    }

    /**
     * Paints this component and all of its elements
     *
     * @param g The graphics context to use for painting
     */
    public void paint(final Graphics g) {
        if (fontmetrics == null)
            setupFontMetrics(g);
        
        final Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        if (rootElement != null)
            rootElement.paint(g);
    }

    /**
     * Return the current width of this component
     *
     * @return Width
     */
    public int getWidth()
    {
        if (rootElement != null)
          return rootElement.getWidth();
        return 0;
    }

    /**
     * Return the current height of this component
     *
     * @return Height
     */
    public int getHeight()
    {
        if (rootElement != null)
            return rootElement.getHeight();
        return 0;
    }
}
