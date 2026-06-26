def call() {

    echo "Getting SAP Commerce Cloud OAuth Token"

    def tokenResponse = sh(
            script: """
        curl -sS --location --request POST 'https://ycloud.accounts.ondemand.com/oauth2/token' \
        -H 'Content-Type: application/x-www-form-urlencoded' \
        -H 'Accept: application/json' \
        --data-urlencode 'client_id=b3f4ea79-ebc0-4a41-bf1c-93f7c588eafe' \
        --data-urlencode 'client_secret=xFM/UavCi@X=i8xdGHJvPDP?3UU6j_q' \
        --data-urlencode 'grant_type=client_credentials' \
        --data-urlencode 'resource=urn:sap:identity:application:provider:name:cp-dependency'
        """,
            returnStdout: true
    ).trim()

    echo "Token response: ${tokenResponse}"

    if (!tokenResponse.startsWith("{")) {
        error("Token API did not return JSON: ${tokenResponse}")
    }

    def json = readJSON text: tokenResponse

    if (!json.access_token) {
        error("No access_token found in response")
    }

    return json.access_token
}