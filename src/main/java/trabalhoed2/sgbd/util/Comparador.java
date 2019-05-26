/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalhoed2.sgbd.util;

import java.util.Comparator;

/**
 *
 *
 */
public class Comparador{
    
  
    public static boolean compareInt(int v1, int v2, String condicao) {
        
        switch (condicao.trim()){
            case "=":
                if ((int)v1 == (int)v2){
                    return true;
                }
                return false;
            case "<":
                if (v1 < v2){
                    return true;
                }
                return false;                          
            case ">":
                if (v1 > v2){
                    return true;
                }
                return false;                          
            case "<=":
                if (v1 <= v2){
                    return true;
                }
                return false;                          
            case ">=":
                if (v1 >= v2){
                    return true;
                }
                return false;                          
            case "<>":
                if (v1 != v2){
                    return true;
                }
                return false;                          
        }        
        
        return false;
    }
    public static boolean compareString(String v1, String v2, String condicao) {
        
        switch (condicao.trim()){
            case "=":
                if (v1.equalsIgnoreCase(v2)){
                    return true;
                }
                return false;
            case "<>":
                if (!v1.equalsIgnoreCase(v2)){
                    return true;
                }
                return false;                          
        }        
        
        return false;
    }

    public static boolean compareChar(char v1, char v2, String condicao) {
        
        switch (condicao.trim()){
            case "=":
                if (v1 == v2){
                    return true;
                }
                return false;
            case "<>":
                if (v1 != v2){
                    return true;
                }
                return false;                          
        }        
        
        return false;
    }
    
    public static boolean compareBoolean(boolean v1, boolean v2, String condicao) {
        
        if (v1 == v2) 
            return true;
        return false;
        
    }
    
    public static boolean compareFloat(float v1, float v2, String condicao) {
        
        switch (condicao.trim()){
            case "=":
                if (v1 == v2){
                    return true;
                }
                return false;
            case "<":
                if (v1 < v2){
                    return true;
                }
                return false;                          
            case ">":
                if (v1 > v2){
                    return true;
                }
                return false;                          
            case "<=":
                if (v1 <= v2){
                    return true;
                }
                return false;                          
            case ">=":
                if (v1 >= v2){
                    return true;
                }
                return false;                          
            case "<>":
                if (v1 != v2){
                    return true;
                }
                return false;                          
        }        
        
        return false;
        
    }
    
    public static boolean compareDouble(double v1, double v2, String condicao) {
        
        switch (condicao.trim()){
            case "=":
                if (v1 == v2){
                    return true;
                }
                return false;
            case "<":
                if (v1 < v2){
                    return true;
                }
                return false;                          
            case ">":
                if (v1 > v2){
                    return true;
                }
                return false;                          
            case "<=":
                if (v1 <= v2){
                    return true;
                }
                return false;                          
            case ">=":
                if (v1 >= v2){
                    return true;
                }
                return false;                          
            case "<>":
                if (v1 != v2){
                    return true;
                }
                return false;                          
        }        
        
        return false;
        
    }
}
