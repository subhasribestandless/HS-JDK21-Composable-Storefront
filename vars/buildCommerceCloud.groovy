def call(branch, buildName) {

    echo "=============================="
    echo ">>> STEP 2: BUILD INITIATED"
    echo ">>> branch: ${branch}"
    echo ">>> buildName: ${buildName}"
    echo "=============================="

    withCredentials([
            string(credentialsId: '8b6f355ccc924f1990b5d798a5633478', variable: 'subscriptionCode')
    ]) {

        def token = getCommerceCloudToken()

        echo ">>> STEP 3: CALLING BUILD API"
        echo ">>> subscriptionCode: ${subscriptionCode}"
        echo ">>> token prefix: ${token.take(20)}..."

        def buildResponse = sh(
                script: """
            curl -sS --location --request POST \
            'https://portalapi.commerce.ondemand.com/v2/subscriptions/${subscriptionCode}/builds' \
            --header 'Content-Type: application/json' \
            --header 'x-approuter-authorization: Bearer ${token}' \
            --data-raw '{"branch":"${branch}","name":"${buildName}"}'
            """,
                returnStdout: true
        ).trim()

        echo "=============================="
        echo ">>> STEP 4: RAW BUILD RESPONSE"
        echo buildResponse
        echo "=============================="

        if (!buildResponse || !buildResponse.startsWith("{")) {
            error("BUILD API FAILED - NON JSON RESPONSE: ${buildResponse}")
        }

        def json = readJSON text: buildResponse

        echo ">>> STEP 5: PARSED RESPONSE"
        echo "code = ${json.code}"
        echo "=============================="

        return json.code
    }
}