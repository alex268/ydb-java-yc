package tech.ydb.auth.iam;

import java.security.PrivateKey;

import tech.ydb.core.auth.AuthProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;


/**
 * @author Sergey Polovko
 */
public class IamAuthContextTest {

    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);
    private IamAuthContext iamAuthContext;

    @Before
    public void setUp() {
        mockServerRule.getClient()
            .when(HttpRequest.request()
                .withMethod("POST")
                .withPath("/iam/v1/tokens")
                .withHeader("Content-Type", "application/json")
                .withHeader("Accept", "application/json"))
            .respond(HttpResponse.response("{\"iamToken\":\"my-awesome-token\"}"));

        iamAuthContext = new IamAuthContext("http://localhost:" + mockServerRule.getPort());
    }

    @After
    public void tearDown() throws Exception {
        iamAuthContext.close();
    }

    @Test
    public void getToken() {
        PrivateKey privateKey = Keys.privateKey(getClass().getResourceAsStream("private.txt"));
        AuthProvider provider = iamAuthContext.authProvider("my-account", "my-key", privateKey).join();
        Assert.assertEquals("my-awesome-token", provider.getToken());
    }
}
