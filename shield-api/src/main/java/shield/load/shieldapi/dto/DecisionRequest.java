package shield.load.shieldapi.dto;

public record DecisionRequest(
    String clientId,
    String endpoint,
    long timestamp
) {

}

