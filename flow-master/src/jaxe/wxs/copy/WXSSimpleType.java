/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conform�ment aux dispositions de la Licence Publique G�n�rale GNU, telle que publi�e par la Free Software Foundation ; version 2 de la licence, ou encore (� votre choix) toute version ult�rieure.

Ce programme est distribu� dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans m�me la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de d�tail, voir la Licence Publique G�n�rale GNU .

Vous devez avoir re�u un exemplaire de la Licence Publique G�n�rale GNU en m�me temps que ce programme ; si ce n'est pas le cas, �crivez � la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs.copy;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.apache.log4j.Logger;


public class WXSSimpleType extends WXSAnnotated implements WXSType {
    
    private static final Logger LOG = Logger.getLogger(WXSSimpleType.class);
    
    // (restriction|list|union)
    protected WXSRestriction restriction = null;
    protected WXSList list = null;
    protected WXSUnion union = null;
    protected String name = null;
    
    protected Parent parent; // WXSRedefine | WXSElement | WXSRestriction
    protected WXSSchema schema;
    
    
    public WXSSimpleType(final Element el, final Parent parent, final WXSSchema schema) {
        parseAnnotation(el);
        for (Node n = el.getFirstChild(); n != null; n=n.getNextSibling()) {
            if (n instanceof Element) {
                if ("restriction".equals(n.getLocalName()))
                    restriction = new WXSRestriction((Element)n, null, schema);
                else if ("list".equals(n.getLocalName()))
                    list = new WXSList((Element)n, schema);
                else if ("union".equals(n.getLocalName()))
                    union = new WXSUnion((Element)n, schema);
            }
        }
        if (el.getAttributeNode("name") != null)
            name = el.getAttribute("name");
        
        this.parent = parent;
        this.schema = schema;
    }
    
    public String getName() {
        return(name);
    }
    
    public String getNamespace() {
        return(schema.getTargetNamespace());
    }
    
    public Parent getParent() {
        return(parent);
    }
    
    public void resoudreReferences(final WXSSchema schema, final WXSThing redefine) {
        if (restriction != null)
            restriction.resoudreReferences(schema, redefine);
        if (list != null)
            list.resoudreReferences(schema, redefine);
        if (union != null)
            union.resoudreReferences(schema, redefine);
    }
    
    public ArrayList<String> listeValeurs() {
        if (restriction != null)
            return(restriction.listeValeurs());
        if (union != null)
            return(union.listeValeurs());
        return(null);
    }
    
    public boolean validerValeur(final String valeur) {
        if (restriction != null)
            return(restriction.validerValeur(valeur));
        if (list != null)
            return(list.validerValeur(valeur));
        if (union != null)
            return(union.validerValeur(valeur));
        return(false);
    }
    
    /**
     * Validation d'une valeur par rapport � un type simple des sch�mas (le type ne doit pas avoir de pr�fixe).
     */
    public static boolean validerValeur(final String type, final String valeur) {
        if ("string".equals(type))
            return(true);
        else if ("normalizedString".equals(type))
            return(verifExpr(valeur, "[^\\t\\r\\n]*"));
        else if ("token".equals(type)) {
            if (valeur.indexOf('\n') != -1 || valeur.indexOf('\r') != -1 ||
                    valeur.indexOf('\t') != -1 || valeur.indexOf("  ") != -1)
                return(false);
            return(!valeur.startsWith(" ") && !valeur.endsWith(" "));
        } else if ("base64Binary".equals(type))
            return(verifExpr(valeur, "(([a-zA-Z0-9+/=]\\s?){4})*"));
        else if ("hexBinary".equals(type))
            return(verifExpr(valeur, "(([0-9a-fA-F]){2})*"));
        else if ("integer".equals(type))
            return(verifExpr(valeur, "[+\\-]?\\d+"));
        else if ("positiveInteger".equals(type))
            return(verifExpr(valeur, "\\+?0*[1-9]\\d*"));
        else if ("negativeInteger".equals(type))
            return(verifExpr(valeur, "-0*[1-9]\\d*"));
        else if ("nonNegativeInteger".equals(type))
            return(verifExpr(valeur, "(-0+)|(\\+?\\d+)"));
        else if ("nonPositiveInteger".equals(type))
            return(verifExpr(valeur, "(\\+?0+)|(-\\d+)"));
        else if ("long".equals(type)) {
            if (!verifExpr(valeur, "[+\\-]?\\d+"))
                return(false);
            try {
                final BigInteger big = new BigInteger(valeur);
                final BigInteger max = new BigInteger("9223372036854775807");
                final BigInteger min = new BigInteger("-9223372036854775808");
                if (big.compareTo(max) > 0)
                    return(false);
                if (big.compareTo(min) < 0)
                    return(false);
                return(true);
            } catch (final NumberFormatException ex) {
                LOG.error("validerValeur(String, String) - NumberFormatException", ex);
                return(false);
            }
        } else if ("unsignedLong".equals(type)) {
            if (!verifExpr(valeur, "\\d+"))
                return(false);
            try {
                final BigInteger big = new BigInteger(valeur);
                final BigInteger max = new BigInteger("18446744073709551615");
                return(big.compareTo(max) <= 0);
            } catch (final NumberFormatException ex) {
                LOG.error("validerValeur(String, String) - NumberFormatException", ex);
                return(false);
            }
        } else if ("int".equals(type)) {
            if (!verifExpr(valeur, "[+\\-]?\\d+"))
                return(false);
            String v2 = valeur;
            if (v2.startsWith("+"))
                v2 = v2.substring(1);
            try {
                final long val = Long.parseLong(v2);
                return(val <= 2147483647l && val >= -2147483648l);
            } catch (final NumberFormatException ex) {
                LOG.error("validerValeur(String, String) - NumberFormatException", ex);
                return(false);
            }
        } else if ("unsignedInt".equals(type)) {
            if (!verifExpr(valeur, "\\d+"))
                return(false);
            try {
                final long val = Long.parseLong(valeur);
                return(val <= 4294967295l && val >= 0);
            } catch (final NumberFormatException ex) {
                LOG.error("validerValeur(String, String) - NumberFormatException", ex);
                return(false);
            }
        } else if ("short".equals(type)) {
            if (!verifExpr(valeur, "[+\\-]?\\d+"))
                return(false);
            String v2 = valeur;
            if (v2.startsWith("+"))
                v2 = v2.substring(1);
            try {
                final int val = Integer.parseInt(v2);
                return(val <= 32767 && val >= -32768);
            } catch (final NumberFormatException ex) {
                LOG.error("validerValeur(String, String) - NumberFormatException", ex);
                return(false);
            }
        } else if ("unsignedShort".equals(type)) {
            if (!verifExpr(valeur, "\\d+"))
                return(false);
            try {
                final int val = Integer.parseInt(valeur);
                return(val <= 65535 && val >= 0);
            } catch (final NumberFormatException ex) {
                LOG.error("validerValeur(String, String) - NumberFormatException", ex);
                return(false);
            }
        } else if ("byte".equals(type)) {
            if (!verifExpr(valeur, "[+\\-]?\\d+"))
                return(false);
            String v2 = valeur;
            if (v2.startsWith("+"))
                v2 = v2.substring(1);
            try {
                final int val = Integer.parseInt(v2);
                return(val <= 127 && val >= -128);
            } catch (final NumberFormatException ex) {
                LOG.error("validerValeur(String, String) - NumberFormatException", ex);
                return(false);
            }
        } else if ("unsignedByte".equals(type)) {
            if (!verifExpr(valeur, "\\d+"))
                return(false);
            try {
                final int val = Integer.parseInt(valeur);
                return(val <= 255 && val >= 0);
            } catch (final NumberFormatException ex) {
                LOG.error("validerValeur(String, String) - NumberFormatException", ex);
                return(false);
            }
        } else if ("decimal".equals(type)) {
            return(verifExpr(valeur, "[+\\-]?\\d+\\.?\\d*"));
        } else if ("float".equals(type)) {
            if (!verifExpr(valeur, "(-?INF)|(NaN)|([+\\-]?\\d+\\.?\\d*([eE][+\\-]?\\d{1,3})?)"))
                return(false);
            if ("INF".equals(valeur) || "-INF".equals(valeur)) // "Infinity" en Java
                return(true);
            try {
                Float.parseFloat(valeur);
                return(true);
            } catch (final NumberFormatException ex) {
                return(false);
            }
        } else if ("double".equals(type)) {
            if (!verifExpr(valeur, "(-?INF)|(NaN)|([+\\-]?\\d+\\.?\\d*([eE][+\\-]?\\d{1,3})?)"))
                return(false);
            if ("INF".equals(valeur) || "-INF".equals(valeur))
                return(true);
            try {
                Double.parseDouble(valeur);
                return(true);
            } catch (final NumberFormatException ex) {
                return(false);
            }
        } else if ("boolean".equals(type))
            return(verifExpr(valeur, "(true)|(false)|1|0"));
        else if ("duration".equals(type))
            return(verifExpr(valeur, "-?P(\\d{1,4}Y)?(\\d{1,2}M)?(\\d{1,2}D)?(T(\\d{1,2}H)?(\\d{1,2}M)?(\\d{1,2}(\\.\\d+)?S)?)?")); // en fait plus restrictif ("P" invalide par ex.)
        else if ("dateTime".equals(type))
            return(verifExpr(valeur, "-?\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d(\\.\\d+)?(([+\\-][01]\\d:\\d{2})|Z)?"));
        else if ("date".equals(type))
            return(verifExpr(valeur, "-?\\d{4}-[01]\\d-[0-3]\\d(([+\\-][01]\\d:\\d{2})|Z)?"));
        else if ("time".equals(type))
            return(verifExpr(valeur, "[0-2]\\d:[0-5]\\d:[0-5]\\d(\\.\\d+)?(([+\\-][01]\\d:\\d{2})|Z)?"));
        else if ("gYear".equals(type))
            return(verifExpr(valeur, "-?\\d{4}(([+\\-][01]\\d:\\d{2})|Z)?"));
        else if ("gYearMonth".equals(type))
            return(verifExpr(valeur, "-?\\d{4}-[01]\\d(([+\\-][01]\\d:\\d{2})|Z)?"));
        else if ("gMonth".equals(type))
            return(verifExpr(valeur, "--[01]\\d(([+\\-][01]\\d:\\d{2})|Z)?"));
        else if ("gMonthDay".equals(type))
            return(verifExpr(valeur, "--[01]\\d-[0-3]\\d(([+\\-][01]\\d:\\d{2})|Z)?"));
        else if ("gDay".equals(type))
            return(verifExpr(valeur, "---[0-3]\\d(([+\\-][01]\\d:\\d{2})|Z)?"));
        else if ("Name".equals(type))
            return(verifExpr(valeur, "[^<>&#!/?'\",0-9.\\-\\s][^<>&#!/?'\",\\s]*")); // en fait plus restrictif: \i\c*
        else if ("QName".equals(type))
            return(verifExpr(valeur, "[^<>&#!/?'\",0-9.\\-\\s][^<>&#!/?'\",\\s]*")); // en fait plus restrictif
        else if ("NCName".equals(type))
            return(verifExpr(valeur, "[^<>&#!/?'\",0-9.\\-\\s:][^<>&#!/?'\",:\\s]*")); // en fait plus restrictif: [\i-[:]][\c-[:]]*
        else if ("anyURI".equals(type))
            return(true);
            //return(verifExpr(valeur, "([^:/?#]+:)?(//[^/?#]*)?[^?#]*(\\?[^#]*)?(#.*)?"));
            // pb: cette expression autorise tout!
            // (mais les RFC 2396 et 2732 ne restreignent rien)
        else if ("language".equals(type))
            return(verifExpr(valeur, "[a-zA-Z]{1,8}(-[a-zA-Z0-9]{1,8})*"));
        else if ("ID".equals(type))
            return(verifExpr(valeur, "[^<>&#!/?'\",0-9.\\-\\s:][^<>&#!/?'\",:\\s]*")); // comme NCName
        else if ("IDREF".equals(type))
            return(verifExpr(valeur, "[^<>&#!/?'\",0-9.\\-\\s:][^<>&#!/?'\",:\\s]*")); // comme NCName
        else if ("IDREFS".equals(type))
            return(verifExpr(valeur, "[^<>&#!/?'\",0-9.\\-\\s:][^<>&#!/?'\",:]*"));
        else if ("ENTITY".equals(type))
            return(verifExpr(valeur, "[^<>&#!/?'\",0-9.\\-\\s:][^<>&#!/?'\",:\\s]*")); // comme NCName
        else if ("ENTITIES".equals(type))
            return(verifExpr(valeur, "[^<>&#!/?'\",0-9.\\-\\s:][^<>&#!/?'\",:]*")); // comme IDREFS
        else if ("NOTATION".equals(type))
            return(verifExpr(valeur, "[^0-9.\\-\\s][^\\s]*(\\s[^0-9.\\-\\s][^\\s]*)*"));
            // la facette enumeration est obligatoire -> contrainte suppl�mentaire
        else if ("NMTOKEN".equals(type))
            return(verifExpr(valeur, "[^<>&#!/?'\",\\s]+")); // en fait plus restrictif: \c+
        else if ("NMTOKENS".equals(type))
            return(verifExpr(valeur, "[^<>&#!/?'\",]+")); // en fait plus restrictif
        else
            return(true);
    }
    
    protected static boolean verifExpr(final String valeur, final String regexp) {
        try {
            // un cache serait-il utile ici ? (attention aux fuites de m�moire si c'est static)
            final Pattern r = Pattern.compile("^" + regexp + "$");
            return(r.matcher(valeur).matches());
        } catch (final PatternSyntaxException ex) {
            LOG.error("verifExpr(String, String): " + regexp, ex);
            return(true);
        }
    }
    
}
