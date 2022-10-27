package com.example.demofrontend.controller;


import com.example.demofrontend.model.Product;
import com.google.gson.Gson;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.logging.LoggingFeature;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class ProductController {

    private static final String REST_API_BASE = "http://localhost:8082/api/products";

    private static Client createJerseyRestClient() {
        ClientConfig clientConfig = new ClientConfig();

        // Config logging for client side
        clientConfig.register(
                new LoggingFeature(
                        Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME),
                        Level.INFO,
                        LoggingFeature.Verbosity.PAYLOAD_ANY,
                        10000));

        return ClientBuilder.newClient(clientConfig);
    }

    @GetMapping
    public String index(Model model) {
        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_BASE);
        List<Product> list = target.request(MediaType.APPLICATION_JSON_TYPE).get(List.class);
        model.addAttribute("listProduct", list);
        return "index";
    }

    @GetMapping("add")
    public String formProduct() {
        return "form";
    }

    @PostMapping("add")
    public String addProduct(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "price") int price,
            @RequestParam(name = "quantity") int quantity
    ) {
        Product p = new Product();
        p.setName(name);
        p.setPrice((double) price);
        p.setQuantity(quantity);
        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_BASE);
        target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(new Gson().toJson(p), MediaType.APPLICATION_JSON_TYPE));
        return "index";
    }

    @GetMapping("sell")
    public String sellProduct() {
        return "update-form";
    }

    @PostMapping("sell")
    public String sellProduct(
            @RequestParam(name = "id") int id,
            @RequestParam(name = "quantity") int quantity
    ) {
        Client client = createJerseyRestClient();
        WebTarget target = client.target(REST_API_BASE);
        target.queryParam("id",id)
                .queryParam("quantity",quantity)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept( MediaType.APPLICATION_JSON );
        return "index";
    }
}
