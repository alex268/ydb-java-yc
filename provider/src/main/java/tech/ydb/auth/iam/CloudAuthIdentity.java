package tech.ydb.auth.iam;

import java.nio.file.Path;

import yandex.cloud.sdk.auth.provider.ApiKeyCredentialProvider;
import yandex.cloud.sdk.auth.provider.ComputeEngineCredentialProvider;
import yandex.cloud.sdk.auth.provider.CredentialProvider;
import yandex.cloud.sdk.auth.provider.IamTokenCredentialProvider;

import tech.ydb.auth.AuthIdentity;

public class CloudAuthIdentity implements AuthIdentity {
    private final CredentialProvider credentialProvider;

    CloudAuthIdentity(CredentialProvider credentialProvider) {
        this.credentialProvider = credentialProvider;
    }

    @Override
    public String getToken() {
        return credentialProvider.get().getToken();
    }

    @Override
    public void close() {
        credentialProvider.close();
    }

    public static AuthIdentity metadataIdentity() {
        return new CloudAuthIdentity(
                ComputeEngineCredentialProvider.builder()
                        .enableCache()
                        .build()
        );
    }

    public static AuthIdentity iamTokenIdentity(String accessToken) {
        return new CloudAuthIdentity(
                IamTokenCredentialProvider.builder()
                        .token(accessToken)
                        .build()
        );
    }

    public static AuthIdentity serviceAccountIdentity(Path serviceAccountFile) {
        return new CloudAuthIdentity(
                ApiKeyCredentialProvider.builder()
                        .fromFile(serviceAccountFile)
                        .enableCache()
                        .build()
        );
    }

    public static AuthIdentity serviceAccountIdentity(String serviceAccountJson) {
        return new CloudAuthIdentity(
                ApiKeyCredentialProvider.builder()
                        .fromJson(serviceAccountJson)
                        .enableCache()
                        .build()
        );
    }
}
