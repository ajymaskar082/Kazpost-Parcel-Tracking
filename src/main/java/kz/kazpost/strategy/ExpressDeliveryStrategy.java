
package kz.kazpost.strategy;

import org.springframework.stereotype.Component;

@Component("expressDelivery")
public class ExpressDeliveryStrategy implements DeliveryStrategy {

    @Override
    public String getDeliveryType() {
        return "Жедел жеткізу";
    }

    @Override
    public int getEstimatedDays() {
        return 2;
    }

    @Override
    public double calculateCost(double weightKg) {
        if (weightKg <= 0) {
            throw new IllegalArgumentException(
                    "Сәлемдеме салмағы оң сан болуы керек");
        }

        return 2000 + weightKg * 400;
    }
}
