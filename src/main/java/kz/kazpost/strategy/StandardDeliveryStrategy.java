
package kz.kazpost.strategy;

import org.springframework.stereotype.Component;

@Component("standardDelivery")
public class StandardDeliveryStrategy implements DeliveryStrategy {

    @Override
    public String getDeliveryType() {
        return "Стандартты жеткізу";
    }

    @Override
    public int getEstimatedDays() {
        return 5;
    }

    @Override
    public double calculateCost(double weightKg) {
        if (weightKg <= 0) {
            throw new IllegalArgumentException(
                    "Сәлемдеме салмағы оң сан болуы керек");
        }

        return 1000 + weightKg * 250;
    }
}
