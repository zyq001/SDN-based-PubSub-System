

package jaxe;


import java.util.*;

import org.w3c.dom.*;


/**
 * Sch?ma simplifi? pour Jaxe (inclut dans les fichiers de config)
 */
public class SchemaSimple implements InterfaceSchema {
    
    private Config cfg;
    
    private final Element racine_schema;
    private HashMap<String, Element> cacheDefElement; // cache des associations nom ?l?ment -> d?finition
    private HashMap<Element, String> cacheNomsElements; // cache des associations d?finition -> nom ?l?ment
    
    
    public SchemaSimple(final Element racine_schema, final Config cfg) {
        this.cfg = cfg;
        this.racine_schema = racine_schema;
        construireCacheDefElement();
    }
    
    /**
     * Renvoie true si la r?f?rence vient de ce sch?ma
     */
    public boolean elementDansSchema(final Element refElement) {
        final Document domdoc = refElement.getOwnerDocument();
        return(domdoc == racine_schema.getOwnerDocument());
    }
    
    /**
     * Renvoie la r?f?rence du premier ?l?ment du sch?ma avec le nom donn?.
     */
    public Element referenceElement(final String nom) {
        return(cacheDefElement.get(nom));
    }
    
    /**
     * Renvoie la r?f?rence du premier ?l?ment du sch?ma avec le nom et l'espace de noms de l'?l?ment pass? en param?tre.
     */
    public Element referenceElement(final Element el) {
        final String nom;
        if (el.getPrefix() == null)
            nom = el.getNodeName();
        else
            nom = el.getLocalName();
        return(referenceElement(nom));
    }
    
    /**
     * Renvoie la r?f?rence du premier ?l?ment du sch?ma avec le nom et l'espace de noms de l'?l?ment pass? en param?tre,
     * et avec le parent dont la r?f?rence est pass?e en param?tre.
     */
    public Element referenceElement(final Element el, final Element refParent) {
        return(referenceElement(el));
    }
    
    /**
     * Renvoie le nom de l'?l?ment dont la r?f?rence est donn?e.
     */
    public String nomElement(final Element refElement) {
        return(cacheNomsElements.get(refElement));
    }
    
    /**
     * Renvoie l'espace de nom de l'?l?ment dont la r?f?rence est pass?e en param?tre,
     * ou null si l'espace de noms n'est pas d?fini.
     */
    public String espaceElement(final Element refElement) {
        return(null);
    }
    
    /**
     * Renvoie la documentation d'un ?l?ment dont on donne la r?f?rence
     * (sous forme de texte simple, avec des \n pour faire des sauts de lignes)
     */
    public String documentationElement(final Element refElement) {
        return(null);
    }
    
    /**
     * Renvoie la liste des valeurs possibles pour un ?l?ment, ? partir de sa r?f?rence.
     * Renvoie null s'il y a un nombre infini de valeurs possibles ou si l'?l?ment n'a pas un type simple.
     */
    public ArrayList<String> listeValeursElement(final Element refElement) {
        return(null);
    }
    
    /**
     * Renvoie true si la valeur donn?e est une valeur valide pour l'?l?ment
     */
    public boolean valeurElementValide(final Element refElement, final String valeur) {
        return(true);
    }
    
    /**
     * Renvoie le pr?fixe ? utiliser pour cr?er un ?l?ment dont on donne la r?f?rence,
     * ou null s'il n'y en a pas.
     */
    public String prefixeElement(final Element refElement) {
        return(null);
    }
    
    /**
     * Renvoie la liste des espaces de noms (String) utilis?s par ce sch?ma.
     */
    public ArrayList<String> listeEspaces() {
        return(null);
    }
    
    /**
     * Renvoie true si l'espace de nom est d?fini dans le sch?ma
     */
    public boolean aEspace(final String espace) {
        return(espace == null);
    }
    
    /**
     * Renvoie un pr?fixe ? utiliser pour l'espace de noms donn?, ou null si aucune suggestion n'est possible
     */
    public String prefixeEspace(final String espace) {
        return(null);
    }
    
    /**
     * Renvoie l'espace de noms cible du sch?ma (attribut targetNamespace avec WXS)
     */
    public String espaceCible() {
        return(null);
    }
    
    /**
     * Renvoie les r?f?rences des ?l?ments qui ne sont pas dans l'espace de noms pass? en param?tre
     */
    public ArrayList<Element> listeElementsHorsEspace(final String espace) {
        if (espace == null)
            return(new ArrayList<Element>());
        else
            return(listeTousElements());
    }
    
    /**
     * Renvoie les r?f?rences des ?l?ments qui sont dans les espaces de noms pass?s en param?tre
     */
    public ArrayList<Element> listeElementsDansEspaces(final Set<String> espaces) {
        return(new ArrayList<Element>());
    }
    
    /**
     * Renvoie les r?f?rences de tous les ?l?ments du sch?ma
     */
    public ArrayList<Element> listeTousElements() {
        return(new ArrayList<Element>(cacheNomsElements.keySet()));
    }
    
    /**
     * Renvoit true si l'enfant est obligatoire sous le parent.
     */
    public boolean elementObligatoire(final Element refParent, final Element refEnfant) {
        return(false);
    }
    
    /**
     * Renvoit true si le parent peut avoir des enfants multiples avec la r?f?rence refEnfant.
     */
    public boolean enfantsMultiples(final Element refParent, final Element refEnfant) {
        return(true);
    }
    
    /**
     * Renvoie les r?f?rences des ?l?ments enfants de l'?l?ment dont la r?f?rence est pass?e en param?tre
     */
    public ArrayList<Element> listeSousElements(final Element refParent) {
        final ArrayList<Element> liste = new ArrayList<Element>();
        final NodeList lsousel = refParent.getElementsByTagName("SOUS-ELEMENT");
        for (int i=0; i<lsousel.getLength(); i++) {
            final Element sousel = (Element)lsousel.item(i);
            liste.add(cacheDefElement.get(sousel.getAttribute("element")));
        }
        final NodeList lsousens = refParent.getElementsByTagName("SOUS-ENSEMBLE");
        for (int i=0; i<lsousens.getLength(); i++) {
            final Element sousens = (Element)lsousens.item(i);
            final String nomens = sousens.getAttribute("ensemble");
            final NodeList lens = racine_schema.getElementsByTagName("ENSEMBLE");
            for (int j=0; j<lens.getLength(); j++) {
                final Element ensemble = (Element)lens.item(j);
                if (nomens.equals(ensemble.getAttribute("nom")))
                    liste.addAll(listeSousElements(ensemble));
            }
        }
        return(liste);
    }
    
    /**
     * Expression r?guli?re correspondant au sch?ma pour un ?l?ment parent donn?
     * @param modevisu  True si on cherche une expression r?guli?re ? afficher pour l'utilisateur
     * @param modevalid  Pour obtenir une validation stricte au lieu de chercher si une insertion est possible
     */
    public String expressionReguliere(final Element refParent, final boolean modevisu, final boolean modevalid) {
        final ArrayList<Element> lsousb = listeSousElements(refParent);
        final StringBuilder expr = new StringBuilder();
        final int s = lsousb.size();
        for (int i=0; i < s; i++) {
            if (i != 0)
                expr.append("|");
            if (modevisu)
                expr.append(cfg.titreElement(lsousb.get(i)));
            else
                expr.append(nomElement(lsousb.get(i)));
            if (!modevisu)
                expr.append(",");
        }
        if (s != 0) {
            expr.insert(0, "(");
            expr.append(")*");
        }
        return(expr.toString());
    }
    
    /**
     * Renvoie la liste des r?f?rences des parents possibles pour un ?l?ment dont la r?f?rence est pass?e en param?tre
     */
    public ArrayList<Element> listeElementsParents(final Element refElement) {
        final ArrayList<Element> liste = new ArrayList<Element>();
        if ("ELEMENT".equals(refElement.getNodeName())) {
            final NodeList lsousel = racine_schema.getElementsByTagName("SOUS-ELEMENT");
            for (int i=0; i<lsousel.getLength(); i++) {
                final Element sousel = (Element)lsousel.item(i);
                if (refElement.getAttribute("nom").equals(sousel.getAttribute("element"))) {
                    final Element parent = (Element)sousel.getParentNode();
                    if ("ELEMENT".equals(parent.getNodeName()))
                        liste.add(parent);
                    else if ("ENSEMBLE".equals(parent.getNodeName()))
                        liste.addAll(listeElementsParents(parent));
                }
            }
        } else if ("ENSEMBLE".equals(refElement.getNodeName())) {
            final String nomens = refElement.getAttribute("nom");
            final NodeList lsousens = racine_schema.getElementsByTagName("SOUS-ENSEMBLE");
            for (int i=0; i<lsousens.getLength(); i++) {
                final Element sousens = (Element)lsousens.item(i);
                if (nomens.equals(sousens.getAttribute("ensemble"))) {
                    final Element parent = (Element)sousens.getParentNode();
                    if ("ELEMENT".equals(parent.getNodeName()))
                        liste.add(parent);
                    else if ("ENSEMBLE".equals(parent.getNodeName()))
                        liste.addAll(listeElementsParents(parent));
                }
            }
        }
        return(liste);
    }
    
    /**
     * Renvoie la liste des r?f?rences des attributs possibles pour un ?l?ment dont
     * on donne la r?f?rence en param?tre
     */
    public ArrayList<Element> listeAttributs(final Element refElement) {
        final NodeList latt = refElement.getElementsByTagName("ATTRIBUT");
        final ArrayList<Element> l = new ArrayList<Element>();
        addNodeList(l, latt);
        return(l);
    }
    
    /**
     * Renvoie le nom d'un attribut ? partir de sa r?f?rence
     */
    public String nomAttribut(final Element refAttribut) {
        return(refAttribut.getAttribute("nom"));
    }
    
    /**
     * Renvoie l'espace de noms d'un attribut ? partir de sa r?f?rence, ou null si aucun n'est d?fini
     */
    public String espaceAttribut(final Element refAttribut) {
        return(null);
    }
    
    /**
     * Renvoie la documentation d'un attribut ? partir de sa r?f?rence
     */
    public String documentationAttribut(final Element refAttribut) {
        return(null);
    }
    
    /**
     * Renvoie l'espace de noms d'un attribut ? partir de son nom complet (avec le pr?fixe s'il y en a un)
     */
    public String espaceAttribut(final String nomAttribut) {
        return(null);
    }
    
    /**
     * Renvoie true si un attribut est obligatoire, ? partir de sa d?finition
     */
    @Deprecated
    public boolean estObligatoire(final Element refAttribut) {
        final String presence = refAttribut.getAttribute("presence");
        return("obligatoire".equals(presence));
    }
    
    /**
     * Renvoit true si l'attribut est obligatoire sous le parent.
     */
    public boolean attributObligatoire(final Element refParent, final Element refAttribut) {
        return(estObligatoire(refAttribut));
    }
    
    /**
     * Renvoie la liste des valeurs possibles pour un attribut, ? partir de sa r?f?rence.
     * Renvoie null s'il y a un nombre infini de valeurs possibles.
     */
    public ArrayList<String> listeValeursAttribut(final Element refAttribut) {
        final NodeList lval = refAttribut.getElementsByTagName("VALEUR");
        if (lval.getLength() == 0)
            return(null);
        final ArrayList<String> liste = new ArrayList<String>();
        for (int i=0; i<lval.getLength(); i++) {
            final Element val = (Element)lval.item(i);
            final String sval = val.getFirstChild().getNodeValue().trim();
            liste.add(sval);
        }
        return(liste);
    }
    
    /**
     * Renvoie la valeur par d?faut d'un attribut dont la r?f?rence est donn?e en param?tre
     */
    public String valeurParDefaut(final Element refAttribut) {
        return(null);
    }
    
    /**
     * Renvoie true si la valeur donn?e est une valeur valide pour l'attribut
     */
    public boolean attributValide(final Element refAttribut, final String valeur) {
        if ((valeur == null || "".equals(valeur)) && estObligatoire(refAttribut))
            return(false);
        final ArrayList<String> valeurs = listeValeursAttribut(refAttribut);
        if (valeurs != null)
            return(valeurs.contains(valeur));
        return(true);
    }
    
    /**
     * Renvoie la r?f?rence du premier ?l?ment parent d'un attribut ? partir de sa r?f?rence
     */
    public Element parentAttribut(final Element refAttribut) {
        return((Element)refAttribut.getParentNode());
    }
    
    /**
     * Renvoie true si l'?l?ment dont on donne la r?f?rence peut contenir du texte
     */
    public boolean contientDuTexte(final Element refElement) {
        final String texte  = refElement.getAttribute("texte");
        return("autorise".equals(texte));
    }
    
    /**
     * Renvoie la table hash par nom des d?finitions des ?l?ments dans le fichier de config
     * (?l?ments ELEMENT)
     */
    private HashMap<String, Element> construireCacheDefElement() {
        cacheDefElement = new HashMap<String, Element>();
        cacheNomsElements = new HashMap<Element, String>();
        final NodeList lelements = racine_schema.getElementsByTagName("ELEMENT");
        for (int i=0; i<lelements.getLength(); i++) {
            final Element el = (Element)lelements.item(i);
            final String nom = el.getAttribute("nom");
            cacheDefElement.put(nom, el);
            cacheNomsElements.put(el, nom);
        }
        return(cacheDefElement);
    }
    
    /**
     * Ajoute tous les ?l?ments d'une NodeList ? une ArrayList de Element, en supposant que
     * tous les ?l?ments de la NodeList sont des org.w3c.dom.Element.
     */
    private static void addNodeList(final ArrayList<Element> l, final NodeList nl) {
        for (int i=0; i<nl.getLength(); i++)
            l.add((Element)nl.item(i)); // attention au cast
    }
    
}
