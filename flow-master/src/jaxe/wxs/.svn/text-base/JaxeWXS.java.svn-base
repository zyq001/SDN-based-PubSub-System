/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conformément aux dispositions de la Licence Publique Générale GNU, telle que publiée par la Free Software Foundation ; version 2 de la licence, ou encore (à votre choix) toute version ultérieure.

Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans même la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de détail, voir la Licence Publique Générale GNU .

Vous devez avoir reçu un exemplaire de la Licence Publique Générale GNU en même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.apache.log4j.Logger;

import jaxe.Config;
import jaxe.InterfaceSchema;
import jaxe.Jaxe;
import jaxe.JaxeException;


/**
 * Classe implémentant InterfaceSchema pour les schémas du W3C.
 */
public class JaxeWXS implements InterfaceSchema {
    
    private static final Logger LOG = Logger.getLogger(JaxeWXS.class);
    
    private final Config cfg;
    private WXSSchema schema;
    private final HashMap<Element, WXSElement> hRefElementVersWXS; // lien références éléments -> éléments WXS
    private final HashMap<Element, WXSAttribute> hRefAttributVersWXS; // lien références attributs -> attributs WXS
    private final HashMap<String, ArrayList<WXSElement>> hNomVersWXS; // lien noms -> objets WXS
    private HashMap<String, String> espaceVersPrefixe; // associations espaces de noms -> préfixes
    
    // liste de tous les éléments WXS (pas forcément directement sous xs:schema)
    // (ils peuvent avoir un attribut name ou un attribut ref)
    private final LinkedHashSet<WXSElement> lTousElements;
    private Set<WXSSchema> schemasInclu;
    
    
    public JaxeWXS(final URL schemaURL, final Config cfg) throws JaxeException {
        this.cfg = cfg;
        schemasInclu = new HashSet<WXSSchema>();
        espaceVersPrefixe = new HashMap<String, String>();
        
        schema = new WXSSchema(lireDocument(schemaURL), schemaURL, this, null);
        ajouterEspaces(schema, null, null);
        schemasInclu.add(schema);
        schema.inclusions();
        
        lTousElements = new LinkedHashSet<WXSElement>();
        for (final WXSSchema sch : schemasInclu)
            lTousElements.addAll(sch.listeTousElements());
        for (final WXSSchema sch : schemasInclu)
            sch.resoudreReferences(); // WXSAny.resoudreReferences() a besoin de cet objet JaxeWXS, c'est donc appelé plus tard
        hRefElementVersWXS = new HashMap<Element, WXSElement>();
        hRefAttributVersWXS = new HashMap<Element, WXSAttribute>();
        hNomVersWXS = new HashMap<String, ArrayList<WXSElement>>();
        for (WXSElement element : lTousElements) {
            hRefElementVersWXS.put(element.getDOMElement(), element);
            if (element.getName() != null && element.getRef() == null) {
                ArrayList<WXSElement> listeWXS = hNomVersWXS.get(element.getName());
                if (listeWXS == null) {
                    listeWXS = new ArrayList<WXSElement>();
                    hNomVersWXS.put(element.getName(), listeWXS);
                }
                listeWXS.add(element);
            }
        }
        for (WXSElement element : lTousElements) {
            final ArrayList<WXSAttribute> attributs = element.listeAttributs();
            if (attributs != null) {
                for (WXSAttribute attribut : attributs)
                    hRefAttributVersWXS.put(attribut.getDOMElement(), attribut);
            }
        }
    }
    
    /**
     * Renvoie true si la référence vient de ce schéma
     */
    public boolean elementDansSchema(final Element refElement) {
        return(hRefElementVersWXS.get(refElement) != null);
    }
    
    /**
     * Renvoie la référence du premier élément du schéma avec le nom donné.
     */
    public Element referenceElement(final String nom) {
        final ArrayList<WXSElement> listeWXS = hNomVersWXS.get(nom);
        if (listeWXS == null)
            return(null);
        return(listeWXS.get(0).getDOMElement());
    }
    
    /**
     * Renvoie la référence du premier élément du schéma avec le nom et l'espace de noms de l'élément passé en paramètre.
     */
    public Element referenceElement(final Element el) {
        final String nom;
        if (el.getPrefix() == null)
            nom = el.getNodeName();
        else
            nom = el.getLocalName();
        final String espace = el.getNamespaceURI();
        final WXSElement element = chercherPremierElement(nom, espace);
        if (element != null)
            return(element.getDOMElement());
        return(null);
    }
    
    /**
     * Renvoie la référence du premier élément du schéma avec le nom et l'espace de noms de l'élément passé en paramètre,
     * et avec le parent dont la référence est passée en paramètre.
     */
    public Element referenceElement(final Element el, final Element refParent) {
        if (refParent == null)
            return(referenceElement(el)); // pour les éléments racine
        final WXSElement wxsParent = hRefElementVersWXS.get(refParent);
        if (wxsParent == null) {
            LOG.error("JaxeWXS: referenceElement: référence élément inconnue: " + refParent);
            return(null);
        }
        final ArrayList<ToutElement> liste = wxsParent.listeSousElements();
        final String nom = el.getLocalName();
        final String espace = el.getNamespaceURI();
        for (final ToutElement element : liste) {
            if (element.getName().equals(nom)) {
                final String espaceElement = element.getNamespace();
                if ((espace == null && espaceElement == null) || (espace != null && espace.equals(espaceElement)))
                    return(element.getDOMElement());
            }
        }
        return(null);
    }
    
    /**
     * Renvoie le nom de l'élément dont la référence est donnée.
     */
    public String nomElement(final Element refElement) {
        final WXSElement element = hRefElementVersWXS.get(refElement);
        if (element == null) {
            LOG.error("JaxeWXS: nomElement: référence élément inconnue: " + refElement);
            return(null);
        }
        return(element.getName());
    }
    
    /**
     * Renvoie l'espace de nom de l'élément dont la référence est passée en paramètre,
     * ou null si l'espace de noms n'est pas défini.
     */
    public String espaceElement(final Element refElement) {
        final WXSElement element = hRefElementVersWXS.get(refElement);
        if (element == null)
            return(null);
        return(element.getNamespace());
    }
    
    /**
     * Renvoie le préfixe à utiliser pour créer un élément dont on donne la référence,
     * ou null s'il n'y en a pas.
     */
    public String prefixeElement(final Element refElement) {
        final String espace = espaceElement(refElement);
        if (espace == null)
            return(null);
        return(espaceVersPrefixe.get(espace));
    }
    
    /**
     * Renvoie la documentation d'un élément dont on donne la référence
     * (sous forme de texte simple, avec des \n pour faire des sauts de lignes)
     */
    public String documentationElement(final Element refElement) {
        final WXSElement element = hRefElementVersWXS.get(refElement);
        if (element == null)
            return(null);
        return(element.getDocumentation());
    }
    
    /**
     * Renvoie la liste des valeurs possibles pour un élément, à partir de sa référence.
     * Renvoie null s'il y a un nombre infini de valeurs possibles ou si l'élément n'a pas un type simple.
     */
    public ArrayList<String> listeValeursElement(final Element refElement) {
        final WXSElement element = hRefElementVersWXS.get(refElement);
        if (element == null)
            return(null);
        return(element.listeValeurs());
    }
    
    /**
     * Renvoie true si la valeur donnée est une valeur valide pour l'élément
     */
    public boolean valeurElementValide(final Element refElement, final String valeur) {
        final WXSElement element = hRefElementVersWXS.get(refElement);
        if (element == null)
            return(false);
        return(element.validerValeur(valeur));
    }
    
    /**
     * Renvoie la liste des espaces de noms (String) utilisés par ce schéma.
     */
    public ArrayList<String> listeEspaces() {
        final LinkedHashSet<String> liste = new LinkedHashSet<String>();
        if (schema.getTargetNamespace() != null)
            liste.add(schema.getTargetNamespace());
        for (final String s : espaceVersPrefixe.keySet())
            liste.add(s);
        return(new ArrayList<String>(liste));
    }
    
    /**
     * Renvoie true si l'espace de nom est défini dans le schéma
     */
    public boolean aEspace(final String espace) {
        final String targetNamespace = schema.getTargetNamespace();
        if (espace == null) {
            if (targetNamespace == null || targetNamespace.equals(""))
                return(true);
            if (espaceVersPrefixe.containsKey(""))
                return(true);
            // cas des éléments locaux sans espace de noms :
            final boolean qualified = "qualified".equals(schema.getElementFormDefault());
            if (!qualified)
                return(true);
        } else {
            if (espace.equals(targetNamespace))
                return(true);
            if (espaceVersPrefixe.containsKey(espace))
                return(true);
        }
        return(false);
    }
    
    /**
     * Renvoie un préfixe à utiliser pour l'espace de noms donné, ou null si aucune suggestion n'est possible
     */
    public String prefixeEspace(final String ns) {
        return(espaceVersPrefixe.get(ns));
    }
    
    /**
     * Renvoie l'espace de noms cible du schéma (attribut targetNamespace avec WXS).
     * Attention: Le concept d'un espace cible unique pour un schéma n'existe pas avec Relax NG.
     */
    public String espaceCible() {
        return(schema.getTargetNamespace());
    }
    
    /**
     * Renvoie les références des éléments qui ne sont pas dans l'espace de noms passé en paramètre
     */
    public ArrayList<Element> listeElementsHorsEspace(final String espace) {
        final ArrayList<Element> liste = new ArrayList<Element>();
        for (final WXSElement el : lTousElements) {
            if (el.getName() != null && el.getRef() == null && !el.getAbstract()) {
                final String tns = el.getNamespace();
                if (tns != null && !tns.equals(espace))
                    liste.add(el.getDOMElement());
            }
        }
        return(liste);
    }
    
    /**
     * Renvoie les références des éléments qui sont dans les espaces de noms passés en paramètre
     */
    public ArrayList<Element> listeElementsDansEspaces(final Set<String> espaces) {
        final ArrayList<Element> liste = new ArrayList<Element>();
        for (final WXSElement el : lTousElements) {
            if (el.getName() != null && el.getRef() == null && !el.getAbstract()) {
                final String tns = el.getNamespace();
                if (tns != null && espaces.contains(tns))
                    liste.add(el.getDOMElement());
            }
        }
        return(liste);
    }
    
    /**
     * Renvoie les références de tous les éléments du schéma
     */
    public ArrayList<Element> listeTousElements() {
        final ArrayList<Element> liste = new ArrayList<Element>();
        for (final WXSElement el : lTousElements) {
            if (el.getName() != null && el.getRef() == null && !el.getAbstract())
                liste.add(el.getDOMElement());
        }
        return(liste);
    }
    
    /**
     * Renvoit true si l'enfant est obligatoire sous le parent.
     */
    public boolean elementObligatoire(final Element refParent, final Element refEnfant) {
        final WXSElement parent = hRefElementVersWXS.get(refParent);
        if (parent == null) {
            LOG.error("JaxeWXS: elementObligatoire: référence élément inconnue: " + refParent);
            return(false);
        }
        final WXSElement enfant = hRefElementVersWXS.get(refEnfant);
        if (enfant == null) {
            LOG.error("JaxeWXS: elementObligatoire: référence élément inconnue: " + refEnfant);
            return(false);
        }
        Boolean bb = parent.enfantObligatoire(enfant);
        return(bb != null && bb.booleanValue());
    }
    
    /**
     * Renvoit true si le parent peut avoir des enfants multiples avec la référence refEnfant.
     */
    public boolean enfantsMultiples(final Element refParent, final Element refEnfant) {
        final WXSElement parent = hRefElementVersWXS.get(refParent);
        if (parent == null) {
            LOG.error("JaxeWXS: enfantsMultiples: référence élément inconnue: " + refParent);
            return(false);
        }
        final WXSElement enfant = hRefElementVersWXS.get(refEnfant);
        if (enfant == null) {
            LOG.error("JaxeWXS: enfantsMultiples: référence élément inconnue: " + refEnfant);
            return(false);
        }
        Boolean bb = parent.enfantsMultiples(enfant);
        return(bb != null && bb.booleanValue());
    }
    
    /**
     * Renvoie les références des éléments enfants de l'élément dont la référence est passée en paramètre
     */
    public ArrayList<Element> listeSousElements(final Element refParent) {
        // à faire: cache
        final WXSElement parent = hRefElementVersWXS.get(refParent);
        if (parent == null) {
            LOG.error("JaxeWXS: listeSousElements: référence élément inconnue: " + refParent);
            return(null);
        }
        final ArrayList<ToutElement> sousElements = parent.listeSousElements();
        final ArrayList<Element> liste = new ArrayList<Element>();
        for (ToutElement element : sousElements)
            liste.add(element.getDOMElement());
        return(liste);
    }
    
    /**
     * Expression régulière correspondant au schéma pour un élément parent donné.
     * Dans JaxeWXS, modevisu=true et modevalid=true.
     * @param modevisu  True si on cherche une expression régulière à afficher pour l'utilisateur
     * @param modevalid  Pour obtenir une validation stricte au lieu de chercher si une insertion est possible
     */
    public String expressionReguliere(final Element refParent, final boolean modevisu, final boolean modevalid) {
        final WXSElement parent = hRefElementVersWXS.get(refParent);
        if (parent == null) {
            LOG.error("JaxeWXS: expressionReguliere: référence élément inconnue: " + refParent);
            return(null);
        }
        return(parent.expressionReguliereElement()); // on utilise toujours modevisu=true et modevalid=true
    }
    
    /**
     * Renvoie la liste des références des parents possibles pour un élément dont la référence est passée en paramètre
     */
    public ArrayList<Element> listeElementsParents(final Element refElement) {
        final WXSElement element = hRefElementVersWXS.get(refElement);
        if (element == null) {
            LOG.error("JaxeWXS: listeElementsParents: référence élément inconnue: " + refElement);
            return(null);
        }
        final ArrayList<WXSElement> parents = element.listeElementsParents();
        final ArrayList<Element> liste = new ArrayList<Element>();
        for (WXSElement el : parents)
            liste.add(el.getDOMElement());
        return(liste);
    }
    
    /**
     * Renvoie la liste des références des attributs possibles pour un élément dont
     * on donne la référence en paramètre
     */
    public ArrayList<Element> listeAttributs(final Element refElement) {
        final WXSElement element = hRefElementVersWXS.get(refElement);
        if (element == null) {
            LOG.error("JaxeWXS: listeAttributs: référence élément inconnue: " + refElement);
            return(null);
        }
        final ArrayList<WXSAttribute> attributs = element.listeAttributs();
        final ArrayList<Element> liste = new ArrayList<Element>();
        for (WXSAttribute attribut : attributs)
            liste.add(attribut.getDOMElement());
        return(liste);
    }
    
    /**
     * Renvoie le nom d'un attribut à partir de sa référence
     */
    public String nomAttribut(final Element refAttribut) {
        final WXSAttribute attribut = hRefAttributVersWXS.get(refAttribut);
        if (attribut == null) {
            LOG.error("JaxeWXS: nomAttribut: référence attribut inconnue: " + refAttribut);
            return(null);
        }
        return(attribut.getName());
    }
    
    /**
     * Renvoie l'espace de noms d'un attribut à partir de sa référence, ou null si aucun n'est défini
     */
    public String espaceAttribut(final Element refAttribut) {
        final WXSAttribute attribut = hRefAttributVersWXS.get(refAttribut);
        if (attribut == null) {
            LOG.error("JaxeWXS: espaceAttribut: référence attribut inconnue: " + refAttribut);
            return(null);
        }
        return(attribut.getNamespace());
    }
    
    /**
     * Renvoie la documentation d'un attribut à partir de sa référence
     */
    public String documentationAttribut(final Element refAttribut) {
        final WXSAttribute attribut = hRefAttributVersWXS.get(refAttribut);
        if (attribut == null) {
            LOG.error("JaxeWXS: documentationAttribut: référence attribut inconnue: " + refAttribut);
            return(null);
        }
        return(attribut.getDocumentation());
    }
    
    /**
     * Renvoie l'espace de noms d'un attribut à partir de son nom complet (avec le préfixe s'il y en a un)
     */
    public String espaceAttribut(final String nomAttribut) {
        if (nomAttribut == null)
            return(null);
        final String prefixe = prefixeNom(nomAttribut);
        if (prefixe == null)
            return(null);
        if ("xml".equals(prefixe))
            return("http://www.w3.org/XML/1998/namespace");
        return(schema.espacePrefixe(prefixe));
    }
    
    /**
     * Renvoie true si un attribut est obligatoire, à partir de sa définition.
     * Attention: ce n'est pas possible à déterminer avec seulement la référence d'attribut avec Relax NG.
     * Il vaut mieux utiliser attributObligatoire.
     */
    @Deprecated
    public boolean estObligatoire(final Element refAttribut) {
        final WXSAttribute attribut = hRefAttributVersWXS.get(refAttribut);
        if (attribut == null) {
            LOG.error("JaxeWXS: estObligatoire: référence attribut inconnue: " + refAttribut);
            return(false);
        }
        return("required".equals(attribut.getUse()));
    }
    
    /**
     * Renvoit true si l'attribut est obligatoire sous le parent.
     */
    public boolean attributObligatoire(final Element refParent, final Element refAttribut) {
        return(estObligatoire(refAttribut));
    }
    
    /**
     * Renvoie la liste des valeurs possibles pour un attribut, à partir de sa référence.
     * Renvoie null s'il y a un nombre infini de valeurs possibles.
     */
    public ArrayList<String> listeValeursAttribut(final Element refAttribut) {
        final WXSAttribute attribut = hRefAttributVersWXS.get(refAttribut);
        if (attribut == null) {
            LOG.error("JaxeWXS: listeValeursAttribut: référence attribut inconnue: " + refAttribut);
            return(null);
        }
        return(attribut.listeValeurs());
    }
    
    /**
     * Renvoie la valeur par défaut d'un attribut dont la référence est donnée en paramètre
     */
    public String valeurParDefaut(final Element refAttribut) {
        final WXSAttribute attribut = hRefAttributVersWXS.get(refAttribut);
        if (attribut == null) {
            LOG.error("JaxeWXS: valeurParDefaut: référence attribut inconnue: " + refAttribut);
            return(null);
        }
        return(attribut.valeurParDefaut());
    }
    
    /**
     * Renvoie true si la valeur donnée est une valeur valide pour l'attribut
     */
    public boolean attributValide(final Element refAttribut, final String valeur) {
        // à refaire avec les classes WXS
        final WXSAttribute attribut = hRefAttributVersWXS.get(refAttribut);
        if (attribut == null) {
            LOG.error("JaxeWXS: attributValide: référence attribut inconnue: " + refAttribut);
            return(false);
        }
        return(attribut.validerValeur(valeur));
    }
    
    /**
     * Renvoie la référence du premier élément parent d'un attribut à partir de sa référence
     */
    public Element parentAttribut(final Element refAttribut) {
        final WXSAttribute attribut = hRefAttributVersWXS.get(refAttribut);
        if (attribut == null) {
            LOG.error("JaxeWXS: parentAttribut: référence attribut inconnue: " + refAttribut);
            return(null);
        }
        final ArrayList<WXSElement> parents = attribut.listeElementsParents();
        if (parents.size() > 0)
            return(parents.get(0).getDOMElement());
        return(null);
    }
    
    /**
     * Renvoie true si l'élément dont on donne la référence peut contenir du texte
     */
    public boolean contientDuTexte(final Element refElement) {
        final WXSElement element = hRefElementVersWXS.get(refElement);
        if (element == null) {
            LOG.error("JaxeWXS: contientDuTexte: référence élément inconnue: " + refElement);
            return(false);
        }
        return(element.contientDuTexte());
    }
    
    
    /**
     * Teste si un élément est valide.
     * Si insertion est true, teste juste la validité d'une insertion (tous les sous-éléments sont optionnels).
     */
    public boolean elementValide(final Element refElement, final List<Element> refSousElements, final boolean insertion) {
        final WXSElement element = hRefElementVersWXS.get(refElement);
        if (element == null) {
            LOG.error("JaxeWXS: elementValide: référence élément inconnue: " + refElement);
            return(false);
        }
        final ArrayList<WXSElement> sousElements = new ArrayList<WXSElement>();
        for (Element ref : refSousElements) {
            final WXSElement sousElement = hRefElementVersWXS.get(ref);
            if (sousElement != null)
                sousElements.add(sousElement); // sinon ref d'une autre config ?
        }
        return(element.validerSousElements(sousElements, insertion));
    }
    
    
    protected WXSSchema nouveauSchemaInclu(final URL urlSchemaParent, final String schemaLocation, final String espaceImport, final WXSSchema schemaParent) throws JaxeException {
        try {
            final URL urls = resoudreURI(getURLParent(urlSchemaParent), schemaLocation);
            if (urls == null)
                throw new JaxeException("include/import : location not found : " + schemaLocation);
            for (WXSSchema schemaInclu : schemasInclu)
                if (schemaInclu.getURL().toURI().normalize().equals(urls.toURI().normalize())) {
                    ajouterEspaces(schemaInclu, schemaParent, espaceImport); // une chance de plus de trouver un préfixe
                    return(schemaInclu);
                }
            final WXSSchema schemaInclu = new WXSSchema(lireDocument(urls), urls, this, schemaParent);
            ajouterEspaces(schemaInclu, schemaParent, espaceImport);
            schemasInclu.add(schemaInclu);
            schemaInclu.inclusions();
            return(schemaInclu);
        } catch (final MalformedURLException ex) {
            throw new JaxeException("include/import : MalformedURLException: " + ex.getMessage(), ex);
        } catch (final URISyntaxException ex) {
            throw new JaxeException("include/import : URISyntaxException: " + ex.getMessage(), ex);
        } catch (final TransformerException ex) {
            throw new JaxeException("include/import : TransformerException: " + ex.getMessage(), ex);
        }
    }
    
    private void ajouterEspaces(final WXSSchema sch, final WXSSchema schemaParent, final String espaceImport) {
        if (espaceImport != null && espaceVersPrefixe.get(espaceImport) == null) {
            String prefixe = sch.prefixeEspace(espaceImport);
            if (prefixe != null)
                espaceVersPrefixe.put(espaceImport, prefixe);
            else if (schemaParent != null) {
                prefixe = schemaParent.prefixeEspace(espaceImport);
                if (prefixe != null)
                    espaceVersPrefixe.put(espaceImport, prefixe);
            }
        }
        // toujours ajouter targetNamespace ?
        final String targetNamespace = sch.getTargetNamespace();
        if (targetNamespace != null && !"".equals(targetNamespace)) {
            final String prefixe = sch.prefixeEspace(targetNamespace);
            if (prefixe != null)
                espaceVersPrefixe.put(targetNamespace, prefixe);
        }
    }
    
    private WXSElement chercherPremierElement(final String nom, final String espace) {
        final ArrayList<WXSElement> listeWXS = hNomVersWXS.get(nom);
        if (listeWXS == null)
            return(null);
        for (WXSElement element : listeWXS)
            if ((espace == null && element.getNamespace() == null) || (espace != null && espace.equals(element.getNamespace())))
                return(element);
        return(null);
    }
    
    /**
     * Renvoie l'élément racine du document avec l'URL passée en paramètre.
     */
    protected static Element lireDocument(final URL urlSchema) throws JaxeException {
        Document schemadoc;
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            final DocumentBuilder parser = dbf.newDocumentBuilder();
            parser.setEntityResolver(Jaxe.getEntityResolver());
            schemadoc = parser.parse(urlSchema.toExternalForm());
        } catch (final Exception ex) {
            LOG.error("JaxeWXS: lecture de " + urlSchema.toExternalForm(), ex);
            ex.printStackTrace(System.err);
            throw new JaxeException("JaxeWXS: lecture de " + urlSchema.toExternalForm(), ex);
        }
        return(schemadoc.getDocumentElement());
    }
    
    /*
     * Utilise le CatalogResolver d'Apache pour résoudre des URI en URL
     */
    protected static URL resoudreURI(final URL schemadir, final String uri) throws MalformedURLException, TransformerException {
        if (uri == null)
            return(null);
        if (uri.startsWith("http://"))
            return(new URL(uri));
        else if (schemadir != null && !uri.startsWith("urn:"))
            return(new URL(schemadir.toExternalForm() + "/" + uri));
        else {
            final URIResolver resolver = Jaxe.getURIResolver();
            if (resolver != null) {
                Source src;
                if (schemadir != null)
                    src = resolver.resolve(uri, schemadir.toString());
                else
                    src = resolver.resolve(uri, null);
                final URL surl = new URL(src.getSystemId());
                try {
                    // pour éviter un bug de CatalogResolver qui n'encode pas l'URI correctement...
                    final URI suri = new URI(surl.getProtocol(), surl.getHost(), surl.getPath(), surl.getQuery(), null);
                    return(suri.toURL());
                } catch (URISyntaxException ex) {
                    LOG.error("resolveURI", ex);
                    return(surl);
                } catch (MalformedURLException ex) {
                    LOG.error("resolveURI", ex);
                    return(surl);
                }
            } else
                return(new URL(uri));
        }
    }
    
    /**
     * Renvoie l'url du répertoire parent du fichier ou répertoire correspondant à l'URL donnée,
     * ou null si l'on ne peut pas déterminer le répertoire parent.
     */
    protected static URL getURLParent(final URL u) {
        final int index = u.toExternalForm().lastIndexOf("/");
        if (index >= 0) {
            try {
                return(new URL(u.toExternalForm().substring(0, index)));
            } catch (final MalformedURLException ex) {
                LOG.error("getURLParent(" + u + ") : MalformedURLException", ex);
                return(null);
            }
        }
        return(null);
    }
    
    protected Set<ToutElement> listeAny(final String namespace, final String targetNamespace) {
        final LinkedHashSet<ToutElement> liste = new LinkedHashSet<ToutElement>();
        if (namespace == null || "".equals(namespace) || "##any".equals(namespace)) {
            for (final WXSElement el : lTousElements)
                if (el.getName() != null && el.getRef() == null && !el.getAbstract())
                    liste.add(el);
        } else if ("##local".equals(namespace)) {
            for (final WXSElement el : lTousElements) {
                if (el.getName() != null && el.getRef() == null && !el.getAbstract()) {
                    final String tns = el.getNamespace();
                    if (tns == null || tns.equals(targetNamespace))
                        liste.add(el);
                }
            }
        } else if ("##other".equals(namespace)) {
            final ArrayList<Element> references = cfg.listeElementsHorsEspace(targetNamespace);
            for (Element ref : references)
                liste.add(new ElementExterne(ref, cfg));
        } else {
            // liste d'espaces de noms séparés par des espaces
            final HashSet<String> espaces = new HashSet<String>(Arrays.asList(namespace.split("\\s")));
            if (espaces.contains("##targetNamespace")) {
                espaces.remove("##targetNamespace");
                espaces.add(targetNamespace);
            }
            if (espaces.contains("##local")) {
                espaces.remove("##local");
                espaces.add("");
            }
            final ArrayList<Element> references = cfg.listeElementsDansEspaces(espaces);
            for (Element ref : references)
                liste.add(new ElementExterne(ref, cfg));
        }
        return(liste);
    }
    
    /**
     * Renvoie la partie locale du nom d'un élément (en retirant le préfixe)
     */
    protected static String valeurLocale(final String nom) {
        if (nom == null)
            return(null);
        final int ind = nom.indexOf(':');
        if (ind == -1)
            return(nom);
        return(nom.substring(ind + 1));
    }
    
    /**
     * Renvoie le préfixe d'un nom, ou null s'il n'en a pas.
     */
    protected static String prefixeNom(final String nom) {
        if (nom == null)
            return(null);
        final int indp = nom.indexOf(':');
        if (indp == -1)
            return(null);
        else
            return(nom.substring(0, indp));
    }
    
    protected static ArrayList<String> listeValeursBooleen(final String type, final Element domElement) {
        final String tns = domElement.lookupNamespaceURI(prefixeNom(type));
        final String espaceSchema = domElement.getNamespaceURI();
        if ("boolean".equals(valeurLocale(type)) && espaceSchema.equals(tns)) {
            final String[] tvalbool = {"true", "false", "1", "0"};
            final ArrayList<String> valbool = new ArrayList<String>(Arrays.asList(tvalbool));
            return(valbool);
        }
        return(null);
    }
    
    protected String titreElement(final Element refElement) {
        return(cfg.titreElement(refElement));
    }
    
}
