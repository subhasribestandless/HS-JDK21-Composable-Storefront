def call(codeNumber) {
    script {

        while (true) {

            withCredentials([
                    string(credentialsId: 'commerceCloudSubscriptionCode', variable: 'subscriptionCode')
            ]) {

                def token = getCommerceCloudToken()

                result = sh(
                        script: """
                    curl -sS --location \
                    'https://portalapi.commerce.ondemand.com/v2/subscriptions/${subscriptionCode}/builds/${codeNumber}' \
                    --header 'x-approuter-authorization: Bearer ${token}'
                    """,
                        returnStdout: true
                ).trim()
            }

            echo "Build status response: ${result}"

            if (!result.startsWith("{")) {
                error("Invalid response from build status API: ${result}")
            }

            def statusResult = readJSON text: result

            if (statusResult.status == "SUCCESS") {
                break
            }

            if (statusResult.status == "FAIL") {
                error("Build failed in SAP Commerce Cloud")
            }

            sleep 120
        }

        echo "Commerce Cloud Build Complete"
    }
}