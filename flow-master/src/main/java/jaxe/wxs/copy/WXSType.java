/*
Jaxe - Editeur XML en Java

Copyright (C) 2010 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conform�ment aux dispositions de la Licence Publique G�n�rale GNU, telle que publi�e par la Free Software Foundation ; version 2 de la licence, ou encore (� votre choix) toute version ult�rieure.

Ce programme est distribu� dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans m�me la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de d�tail, voir la Licence Publique G�n�rale GNU .

Vous devez avoir re�u un exemplaire de la Licence Publique G�n�rale GNU en m�me temps que ce programme ; si ce n'est pas le cas, �crivez � la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.wxs.copy;

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
