
package kz.kazpost.controller;

import kz.kazpost.model.Parcel;
import kz.kazpost.service.ParcelService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ParcelController {

    private final ParcelService parcelService;

    public ParcelController(ParcelService parcelService) {
        this.parcelService = parcelService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("parcels", parcelService.getAllParcels());
        model.addAttribute("total", parcelService.getParcelCount());
        return "index";
    }

    @GetMapping("/track")
    public String track(@RequestParam(required = false) String number,
                        Model model) {
        if (number != null && !number.isBlank()) {
            parcelService.findByTrackingNumber(number.trim())
                    .ifPresentOrElse(
                            parcel -> model.addAttribute("parcel", parcel),
                            () -> model.addAttribute(
                                    "error", "Сәлемдеме табылмады")
                    );
        }
        model.addAttribute("searchNumber", number);
        return "index";
    }

    @GetMapping("/parcel/{trackingNumber}")
    public String parcelDetails(@PathVariable String trackingNumber,
                                Model model) {
        Parcel parcel = parcelService
                .findByTrackingNumber(trackingNumber)
                .orElse(null);

        if (parcel == null) {
            return "redirect:/";
        }

        model.addAttribute("parcel", parcel);
        model.addAttribute("history", parcel.getTrackingHistory());
        return "parcel-details";
    }

    @PostMapping("/parcel/status")
    public String updateStatus(
            @RequestParam String trackingNumber,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {

        boolean updated = parcelService.updateStatus(
                trackingNumber, status);

        if (updated) {
            redirectAttributes.addFlashAttribute(
                    "message", "Сәлемдеме мәртебесі жаңартылды");
        } else {
            redirectAttributes.addFlashAttribute(
                    "error", "Сәлемдеме табылмады");
        }

        return "redirect:/";
    }
}
