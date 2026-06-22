def call() {
    echo "entered getCommerceCloudToken method"
    def tokenEndpoint = "https://ycloud.accounts.ondemand.com/oauth2/token"
    def resource = "urn:sap:identity:application:provider:name:cp-dependency"

    def tokenResponse = sh(
        script: """curl --silent --location --request POST 'https://ycloud.accounts.ondemand.com/oauth2/token' \
            -H 'Content-Type: application/x-www-form-urlencoded' \
            -H 'Accept: application/json' \
            -d 'client_id=b3f4ea79-ebc0-4a41-bf1c-93f7c588eafe' \
            -d 'client_secret=xFM/UavCi@X=i8xdGHJvPDP?3UU6j_q' \
            --data-urlencode 'grant_type=client_credentials' \
            --data-urlencode 'resource=urn:sap:identity:application:provider:name:cp-dependency'""",
        returnStdout: true
    )
    echo "$tokenResponse"
    def tokenJson = readJSON text: tokenResponse
    if (!tokenJson["access_token"]) {
        error("Failed to obtain access token from SAP Commerce Cloud OAuth2 endpoint")
    }

    return tokenJson["access_token"]
}