package com.home.amazon.serverless.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.google.gson.Gson;
import com.home.amazon.serverless.DependencyFactory;
import com.home.amazon.serverless.dto.Book;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.HashMap;
import java.util.Map;

public class GetItemHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private final DynamoDbEnhancedClient dbClient;
    private final String tableName;
    private final TableSchema<Book> bookTableSchema;
    Map<String, String> jsonHeaders;

    public GetItemHandler() {
        dbClient = DependencyFactory.dynamoDbEnhancedClient();
        tableName = DependencyFactory.tableName();
        bookTableSchema = TableSchema.fromBean(Book.class);
        jsonHeaders = new HashMap<>();
        jsonHeaders.put("Content-Type", "application/json");
    }

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Inside lambda");
        logger.log("Raw path: " + input.getRawPath());
        String response = "";
        DynamoDbTable<Book> booksTable = dbClient.table(tableName, bookTableSchema);
        Map<String, String> pathParameters = input.getPathParameters();
        logger.log("Path parameters: " + pathParameters);
        if (pathParameters != null) {
            String itemPartitionKey = pathParameters.get(Book.PARTITION_KEY);
            logger.log("Ley: " + itemPartitionKey);
            Book item = booksTable.getItem(Key.builder().partitionValue(itemPartitionKey).build());
            if (item != null) {
                response = new Gson().toJson(item);
            }
        }

        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(200)
                .withBody(response)
                .withHeaders(jsonHeaders)
                .build();
    }

}
