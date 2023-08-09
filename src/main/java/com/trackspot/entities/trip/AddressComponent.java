package com.trackspot.entities.trip;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.maps.model.AddressComponentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressComponent implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("long_name")
    public String longName;

    @JsonProperty("short_name")
    public String shortName;

    public AddressComponentType[] types;

    public AddressComponent(com.google.maps.model.AddressComponent addressComponent) {
        this.longName = addressComponent.longName;
        this.shortName = addressComponent.shortName;
        this.types = addressComponent.types;
    }
}