def call(branch, buildName) {

    echo "=============================="
    echo ">>> STEP 2: BUILD INITIATED"
    echo ">>> branch: ${branch}"
    echo ">>> buildName: ${buildName}"
    echo "=============================="

    // 🔥 HARDCODED SUBSCRIPTION CODE (no Jenkins dependency)
    def subscriptionCode = "5416ea93eb324720a548e0690064c59c"

    def token = getCommerceCloudToken()

    echo ">>> STEP 3: CALLING BUILD API"
    echo ">>> Using subscriptionCode: ${subscriptionCode}"

    def response = sh(
            script: """
            curl --compressed -sS -i \
            --location --request POST \
            "https://portalapi.commerce.ondemand.com/v2/subscriptions/${subscriptionCode}/builds" \
            --header "Content-Type: application/json" \
            --header "Accept: application/json" \
            --header "x-approuter-authorization: Bearer ${token}" \
            --data-raw '{"branch":"${branch}","name":"${buildName}"}'
        """,
            returnStdout: true
    ).trim()

    echo "=============================="
    echo ">>> RAW RESPONSE"
    echo response
    echo "=============================="

    // Extract HTTP status
    def matcher = response =~ /HTTP\\/.* (\\d{3})/
    def status = matcher ? matcher[0][1] : "UNKNOWN"

    echo "HTTP STATUS = ${status}"

    // Extract body
    def body = response.substring(response.lastIndexOf("\r\n\r\n") + 4).trim()

    echo "BODY:"
    echo body

    if (!(status in ["200", "201", "202"])) {
        error("Build API FAILED with HTTP ${status}")
    }

    if (!body.startsWith("{")) {
        error("Invalid JSON response:\n${body}")
    }

    def json = readJSON text: body

    echo "=============================="
    echo "BUILD SUCCESS"
    echo "CODE = ${json.code}"
    echo "=============================="

    return json.code
}