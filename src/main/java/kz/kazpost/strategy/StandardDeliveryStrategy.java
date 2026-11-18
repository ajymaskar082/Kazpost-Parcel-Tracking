package kz.kazpost.strategy;

import kz.kazpost.model.Parcel;

public final class StandardDeliveryStrategy implements DeliveryStrategy {
    private static final double BASE_COST = 800.0;
    private static final double COST_PER_KG = 250.0;

    @Override
    public double calculateDeliveryCost(Parcel parcel) {
        return BASE_COST + parcel.getWeight() * COST_PER_KG;
    }

    @Override
    public String getName() {
        return "Стандартты жеткізу";
    }
}