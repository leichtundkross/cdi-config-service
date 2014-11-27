package com.exxeta.configservice;

/**
 * Beschreibt die Funktionalitaet, die eine Datenbank zum Lesen und Schreiben von Konfigurationswerten bieten muss
 * 
 */
public interface ConfigDatabase {

    /**
     * Schreibt einen Konfigurationswert unter dem angegebenen Schluessel
     * 
     * @param propertyName
     *            der Schluessel, unter welchem eingetragen wird
     * @param entry
     *            die einzutragenden Werte
     */
    void save(String propertyName, ConfigurationEntry entry);

    /**
     * Laedt einen Konfigurationswert unter dem angegebenen Schluessel
     * 
     * @param propertyName
     *            der Schluessel der Property, unter welchem gesucht werden soll
     * @return der gefundene Wert als String
     */
    String load(String propertyName);
}
