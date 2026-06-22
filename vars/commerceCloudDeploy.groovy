def call(buildName, dbUpdateMode, environmentId, strategy) {
    echo "##### Initiate Deployment to SAP Commerce Cloud Environment #####"
    //deploy tag 
    script{
        withCredentials([
            string(credentialsId: 'commerceCloudSubscriptionCode', variable: 'subscriptionCode')
        ]) {
            def token = getCommerceCloudToken()
            deploy = sh (script: "curl --location --request POST 'https://portalapi.commerce.ondemand.com/v2/subscriptions/${subscriptionCode}/deployments' --header 'Content-Type: application/json' --header 'x-approuter-authorization: Bearer ${token}' --data-raw '{\"buildCode\": \"${buildName}\",\"databaseUpdateMode\": \"${dbUpdateMode}\",\"environmentCode\": \"${environmentId}\",\"strategy\": \"${strategy}\"}'",returnStdout:true)
            echo "$deploy"
            deploy_result = readJSON text: "$deploy"
            deploy_code = deploy_result["code"]
            return deploy_code
        }
    }
}
