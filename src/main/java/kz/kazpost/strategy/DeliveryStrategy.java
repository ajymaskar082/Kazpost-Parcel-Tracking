
package kz.kazpost.strategy;

public interface DeliveryStrategy {

    String getDeliveryType();

    int getEstimatedDays();

    double calculateCost(double weightKg);
}
