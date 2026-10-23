package com.netco.microservices.shipping.service;

import com.netco.microservices.shipping.event.OrderPlacedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;

@Component
public class ShippoClient {
    private final WebClient client = WebClient.builder().baseUrl("https://api.goshippo.com").build();
    @Value("${shippo.test-token:}") private String token;
    @Value("${shippo.from.name:Netco Practice Store}") private String fromName;
    @Value("${shippo.from.street1:}") private String fromStreet;
    @Value("${shippo.from.city:}") private String fromCity;
    @Value("${shippo.from.state:}") private String fromState;
    @Value("${shippo.from.zip:}") private String fromZip;
    @Value("${shippo.from.country:US}") private String fromCountry;
    @Value("${shippo.from.phone:}") private String fromPhone;
    @Value("${shippo.parcel.weight:1}") private String weight;

    public String createTestShipment(OrderPlacedEvent event) {
        if (token.isBlank() || !token.startsWith("shippo_test_"))
            throw new IllegalStateException("SHIPPO_TEST_TOKEN must be a Shippo test token; live tokens are deliberately rejected");
        if (event.shippingAddress() == null) throw new IllegalArgumentException("Order has no shipping address");
        var address = event.shippingAddress();
        Map<String, Object> from = Map.of("name", fromName, "street1", fromStreet, "city", fromCity,
                "state", fromState, "zip", fromZip, "country", fromCountry, "phone", fromPhone);
        Map<String, Object> to = Map.of("name", value(address.name()), "street1", value(address.street1()),
                "city", value(address.city()), "state", value(address.state()), "zip", value(address.zip()),
                "country", value(address.country()), "phone", value(address.phone()));
        Map<String, Object> parcel = Map.of("length", "10", "width", "8", "height", "4",
                "distance_unit", "in", "weight", weight, "mass_unit", "lb");
        Map<String, Object> request = Map.of("address_from", from, "address_to", to,
                "parcels", List.of(parcel), "async", false, "metadata", "order:" + event.orderNumber());
        Map<?, ?> response = client.post().uri("/shipments/")
                .header("Authorization", "ShippoToken " + token)
                .bodyValue(request).retrieve().bodyToMono(Map.class).block();
        return response == null ? "unknown" : String.valueOf(response.get("object_id"));
    }
    private String value(String value) { if (value == null || value.isBlank()) throw new IllegalArgumentException("Incomplete destination address"); return value; }
}
