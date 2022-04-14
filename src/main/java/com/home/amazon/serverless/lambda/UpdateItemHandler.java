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
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.utils.StringUtils;

public class UpdateItemHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private final DynamoDbEnhancedClient dbClient;
    private final String tableName;
    private final TableSchema<Book> bookTableSchema;

    public UpdateItemHandler() {
        dbClient = DependencyFactory.dynamoDbEnhancedClient();
        tableName = DependencyFactory.tableName();
        bookTableSchema = TableSchema.fromBean(Book.class);
    }

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent request, Context context) {
        String body = request.getBody();
        String responseBody = "";
        if (StringUtils.isNotBlank(body)) {
            Gson gson = new Gson();
            Book item = gson.fromJson(body, Book.class);
            if (item != null) {
                DynamoDbTable<Book> booksTable = dbClient.table(tableName, bookTableSchema);
                Book updateResult = booksTable.updateItem(item);
                responseBody = gson.toJson(updateResult);
            }
        }
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(200)
                .withBody(responseBody)
                .build();
    }
}
