package co.edu.escuelaing.handler.normalizer;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleMappingService {
    private final DynamoDbClient dynamoDbClient;
    private final String tableName = "RoleMappings";

    public RoleMappingService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    /**
     * Traduce roles corporativos → roles por app
     */
    public List<String> translateRoles(List<String> corporateRoles, String appId) {

        List<String> finalRoles = new ArrayList<>();

        for (String corpRole : corporateRoles) {

            Map<String, AttributeValue> key = new HashMap<>();
            key.put("corporateRole", AttributeValue.builder().s(corpRole).build());
            key.put("appId", AttributeValue.builder().s(appId).build());

            try {
                GetItemResponse response = dynamoDbClient.getItem(
                        GetItemRequest.builder()
                                .tableName(tableName)
                                .key(key)
                                .build()
                );

                if (response.hasItem()) {
                    List<String> mapped = response.item()
                            .get("mappedRoles")
                            .ss()
                            .stream()
                            .toList();

                    finalRoles.addAll(mapped);
                }

            } catch (Exception e) {
                System.out.println("Error loading mapping for role: " + corpRole);
            }
        }

        return finalRoles;
    }
}
