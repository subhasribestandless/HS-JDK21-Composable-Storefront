def call(branch, buildName) {
    echo "##### Initiate Build to SAP Commerce Cloud Environment #####"

    withCredentials([
            string(credentialsId: 'commerceCloudSubscriptionCode', variable: 'subscriptionCode')
    ]) {

        def token = getCommerceCloudToken()

        def build = sh(
                script: """
            curl -sS --location --request POST \
            'https://portalapi.commerce.ondemand.com/v2/subscriptions/${subscriptionCode}/builds' \
            --header 'Content-Type: application/json' \
            --header 'x-approuter-authorization: Bearer ${token}' \
            --data-raw '{\"branch\":\"${branch}\",\"name\":\"${buildName}\"}'
            """,
                returnStdout: true
        ).trim()

        echo "Build response: ${build}"

        if (!build || !build.startsWith("{")) {
            error("Build API did not return JSON. Response: ${build}")
        }

        def build_result = readJSON text: build
        return build_result.code
    }
}