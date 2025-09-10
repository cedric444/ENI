package com.stage.pizzeria.model;

public enum ExpirationRisk {

    SAFE("Sûr", "#28a745"),
    WARNING("Attention", "#ffc107"),
    CRITICAL("Critique", "#dc3545"),
    EXPIRED("Expiré", "#6c757d");

    private final String label;
    private final String color;

    ExpirationRisk(String label, String color) {
        this.label = label;
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public String getColor() {
        return color;
    }

    /**
     * Method to get the complete css Style
     *
     * @return String
     */
    public String getCssSTyle() {
        return "backgroundcolor: " + color + "; color: white;";
    }

    @Override
    public String toString() {
        return label + "(" + color + ")" +
                '}';
    }
}
