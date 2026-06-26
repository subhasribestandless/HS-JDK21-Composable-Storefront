def call(branch, buildName) {

    echo "=============================="
    echo ">>> STEP 2: BUILD INITIATED"
    echo ">>> branch: ${branch}"
    echo ">>> buildName: ${buildName}"
    echo "=============================="

    withCredentials([
            usernamePassword(
                    credentialsId: 'commerceCloudCredentials',
                    usernameVariable: 'subscriptionCode',
                    passwordVariable: 'unusedPassword'
            )
    ]) {

        def token = getCommerceCloudToken()

        echo ">>> STEP 3: CALLING BUILD API"
        echo ">>> Token Generated Successfully"

        def response = sh(
                script: """
                curl --compressed -sS -i \\
                --location --request POST \\
                "https://portalapi.commerce.ondemand.com/v2/subscriptions/${subscriptionCode}/builds" \\
                --header "Content-Type: application/json" \\
                --header "Accept: application/json" \\
                --header "x-approuter-authorization: Bearer ${token}" \\
                --data-raw '{"branch":"${branch}","name":"${buildName}"}'
            """,
                returnStdout: true
        ).trim()

        echo "=============================="
        echo ">>> BUILD API RESPONSE"
        echo response
        echo "=============================="

        def statusMatcher = response =~ /HTTP\\/.* (\\d{3})/
        def status = statusMatcher ? statusMatcher[0][1] : "UNKNOWN"

        echo "HTTP Status = ${status}"

        def body = response.substring(response.lastIndexOf("\r\n\r\n") + 4).trim()

        echo "Response Body:"
        echo body

        if (!(status in ["200", "201", "202"])) {
            error("Build API failed with HTTP ${status}")
        }

        if (!body.startsWith("{")) {
            error("Expected JSON but received:\n${body}")
        }

        def json = readJSON text: body

        echo "=============================="
        echo "Build Code = ${json.code}"
        echo "=============================="

        return json.code
    }
}