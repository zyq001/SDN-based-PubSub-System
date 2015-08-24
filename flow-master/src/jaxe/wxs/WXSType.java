/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conformément aux dispositions de la Licence Publique Générale GNU, telle que publiée par la Free Software Foundation ; version 2 de la licence, ou encore (à votre choix) toute version ultérieure.

Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans même la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de détail, voir la Licence Publique Générale GNU .

Vous devez avoir reçu un exemplaire de la Licence Publique Générale GNU en même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Type simple ou complexe.
 */
public interface WXSType {
    
    public String getName();
    
    public String getNamespace();
    
    public Parent getParent();
    
    public void resoudreReferences(final WXSSchema schema, final WXSThing redefine);
    
    /**
     * Renvoie la liste des valeurs possibles pour ce type si c'est un type simple ou un type complexe avec contenu simple.
     * Renvoie null si la liste des valeurs possibles est infinie.
     */
    public ArrayList<String> listeValeurs();
    
    /**
     * Renvoie true si la valeur est valide pour le type simple ou type complexe avec contenu simple.
     */
    public boolean validerValeur(final String valeur);
    
}
