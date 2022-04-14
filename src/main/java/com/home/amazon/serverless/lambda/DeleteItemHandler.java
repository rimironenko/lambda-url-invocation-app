package com.home.amazon.serverless.lambda;

import com.amazonaws.services.lambda.runtime.Context;
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

public class DeleteItemHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private final DynamoDbEnhancedClient dbClient;
    private final String tableName;
    private final TableSchema<Book> bookTableSchema;

    public DeleteItemHandler() {
        dbClient = DependencyFactory.dynamoDbEnhancedClient();
        tableName = DependencyFactory.tableName();
        bookTableSchema = TableSchema.fromBean(Book.class);
    }

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent request, Context context) {
        String responseBody = "";
        DynamoDbTable<Book> booksTable = dbClient.table(tableName, bookTableSchema);
        String itemPartitionKey = getPartitionKey(request.getRawPath());
        if (itemPartitionKey != null) {
            Book deletedBook = booksTable.deleteItem(Key.builder().partitionValue(itemPartitionKey).build());
            responseBody = new Gson().toJson(deletedBook);
        }
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(200)
                .withBody(responseBody)
                .build();
    }

    private String getPartitionKey(String rawPath) {
        if (rawPath == null) return null;
        return rawPath.substring(rawPath.indexOf('/') + 1);
    }

}
