
package kz.kazpost.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ParcelController {

    @GetMapping("/")
    public String home(Model model) {
        List<Map<String, String>> parcels = new ArrayList<>();

        parcels.add(createParcel(
                "KZ100001",
                "Алматы",
                "Астана",
                "Жолда"
        ));

        parcels.add(createParcel(
                "KZ100002",
                "Шымкент",
                "Алматы",
                "Бөлімшеде"
        ));

        parcels.add(createParcel(
                "KZ100003",
                "Астана",
                "Қарағанды",
                "Қабылданды"
        ));

        model.addAttribute("parcels", parcels);
        model.addAttribute("total", parcels.size());

        return "index";
    }

    private Map<String, String> createParcel(
            String trackingNumber,
            String senderCity,
            String recipientCity,
            String status) {

        Map<String, String> parcel = new LinkedHashMap<>();
        parcel.put("trackingNumber", trackingNumber);
        parcel.put("senderCity", senderCity);
        parcel.put("recipientCity", recipientCity);
        parcel.put("status", status);

        return parcel;
    }
}
