package com.home.amazon.serverless.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.home.amazon.serverless.DependencyFactory;
import com.home.amazon.serverless.dto.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetItemHandlerTest {
    private static final String TEST_TABLE_NAME = "TestTable";
    private static final String TEST_PARTITION_KEY_VALUE = "123";

    private static String TEST_RAW_PATH = "/1";
    @Mock
    private DynamoDbEnhancedClient client;
    @Mock
    private DynamoDbTable<Book> table;
    @Mock
    private APIGatewayV2HTTPEvent request;
    @Mock
    private Context context;

    @BeforeEach
    public void setUp() {
        when(client.table(eq(TEST_TABLE_NAME), any(TableSchema.class))).thenReturn(table);
    }

    @Test
    public void shouldReturnItemIfExists() {
        Book testBook = new Book();
        testBook.setIsbn(TEST_PARTITION_KEY_VALUE);
        when(table.getItem(any(Key.class))).thenReturn(testBook);
        when(request.getRawPath()).thenReturn(TEST_RAW_PATH);

        try (MockedStatic<DependencyFactory> dependencyFactoryMockedStatic = mockStatic(DependencyFactory.class)) {
            dependencyFactoryMockedStatic.when(DependencyFactory::dynamoDbEnhancedClient).thenReturn(client);
            dependencyFactoryMockedStatic.when(DependencyFactory::tableName).thenReturn(TEST_TABLE_NAME);
            GetItemHandler handler = new GetItemHandler();
            APIGatewayV2HTTPResponse response = handler.handleRequest(request, context);
            assertEquals(200, response.getStatusCode());
            assertTrue(response.getBody().contains(TEST_PARTITION_KEY_VALUE));
        }
    }

}