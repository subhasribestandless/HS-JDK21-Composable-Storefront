def call(branch, buildName) {
    echo "##### Initiate Build to SAP Commerce Cloud Environment #####"
    //deploy tag 
    script{
        withCredentials([
            string(credentialsId: 'commerceCloudSubscriptionCode', variable: 'subscriptionCode')
        ]) {
            def token = getCommerceCloudToken()
            build = sh (script: "curl --location --request GET 'https://portalapi.commerce.ondemand.com/v2/subscriptions/${subscriptionCode}/builds' --header 'Content-Type: application/json' --header 'x-approuter-authorization: Bearer ${token}' --data-raw '{\"branch\": \"${branch}\",\"name\": \"${buildName}\"}'",returnStdout:true)
            echo "$build"
            build_result = readJSON text: "$build"
            code_number = build_result["code"]
            return code_number
        }
    }
}  
