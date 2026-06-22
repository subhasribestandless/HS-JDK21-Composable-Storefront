def call(codeNumber) {
    script {
        while (true) {
          withCredentials([
              string(credentialsId: 'commerceCloudSubscriptionCode', variable: 'subscriptionCode')
          ]) {
              def token = getCommerceCloudToken()
              result = sh (script: "curl --location --request GET 'https://portalapi.commerce.ondemand.com/v2/subscriptions/${subscriptionCode}/builds/$codeNumber' --header 'x-approuter-authorization: Bearer ${token}'",returnStdout:true)
          }
          echo "$result"
          statusResult = readJSON text: "$result"

          if("SUCCESS".equals(statusResult["status"])) {
            break;
          }

          if("FAIL".equals(statusResult["status"])) {
            error("Build was not completed successfully on SAP Commerce Cloud")
          }

          sh('sleep 120s')

        }

        echo "Commerce Cloud Build Complete"
    }
}  
