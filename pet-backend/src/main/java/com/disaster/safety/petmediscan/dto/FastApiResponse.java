package com.disaster.safety.petmediscan.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FastApiResponse {
    private Predicted predicted;
    private List<TopItem> top5;

    @Getter @Setter
    public static class Predicted {
        private String label;
        private double score;
    }

    @Getter @Setter
    public static class TopItem {
        private String label;
        private double score;
    }
}
