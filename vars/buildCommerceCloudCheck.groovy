def call(codeNumber) {

    echo "=============================="
    echo ">>> STEP 6: BUILD STATUS CHECK STARTED"
    echo ">>> codeNumber: ${codeNumber}"
    echo "=============================="

    def subscriptionCode = "8b6f355ccc924f1990b5d798a5633478"

    while (true) {

        def token = getCommerceCloudToken()

        echo ">>> CALLING STATUS API FOR CODE: ${codeNumber}"

        def result = sh(
                script: """
            curl -sS --location \
            'https://portalapi.commerce.ondemand.com/v2/subscriptions/${subscriptionCode}/builds/${codeNumber}' \
            --header 'x-approuter-authorization: Bearer ${token}'
            """,
                returnStdout: true
        ).trim()

        echo ">>> RAW STATUS RESPONSE:"
        echo result

        if (!result.startsWith("{")) {
            error("STATUS API FAILED - NOT JSON: ${result}")
        }

        def json = readJSON text: result

        echo ">>> STATUS = ${json.status}"

        if (json.status == "SUCCESS") {
            echo ">>> BUILD SUCCESS ✅"
            return
        }

        if (json.status == "FAIL") {
            error("BUILD FAILED IN SAP COMMERCE CLOUD")
        }

        echo ">>> WAITING 120 SECONDS BEFORE NEXT POLL"
        sleep 120
    }
}