package kz.kazpost.strategy;

import kz.kazpost.model.Parcel;

public final class ExpressDeliveryStrategy implements DeliveryStrategy {
    private static final double BASE_COST = 1_500.0;
    private static final double COST_PER_KG = 450.0;

    @Override
    public double calculateDeliveryCost(Parcel parcel) {
        return BASE_COST + parcel.getWeight() * COST_PER_KG;
    }

    @Override
    public String getName() {
        return "Жедел жеткізу";
    }
}