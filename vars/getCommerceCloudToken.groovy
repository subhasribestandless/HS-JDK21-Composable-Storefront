def call() {

    echo "=============================="
    echo ">>> STEP 1: GETTING OAUTH TOKEN"
    echo "=============================="

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

    echo ">>> TOKEN RAW RESPONSE:"
    echo tokenResponse

    if (!tokenResponse.startsWith("{")) {
        error("TOKEN API FAILED - NOT JSON RESPONSE: ${tokenResponse}")
    }

    def json = readJSON text: tokenResponse

    if (!json.access_token) {
        error("TOKEN API FAILED - NO access_token FOUND")
    }

    echo ">>> TOKEN GENERATED SUCCESSFULLY"
    return json.access_token
}