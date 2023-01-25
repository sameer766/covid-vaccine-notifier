# covid-vaccine-notifier
This is a scalable application which notifies user about vaccine via sms by taking pincode and phone number as input primarily. 
Scheduler microservice is entry point where a person schedules sms  by giving pincode and phone number as input primarily.
This microservice then call covid data fetcher service which fetch list of covid centre for that pincode
The covid data fetcher calls twilio microservice to alert user about the vaccines if it is available.
The configuration will come from spring-vault.

sample input 

{
  "callbackData": "string",
  "cronExpression": "0/55 * * * * ? *",
  "initalOffset": 0,
  "remainingFireCount": 0,
  "repeatIntervalMS": 0,
  "runForever": true,
  "totalFireCount": 0,
  "vaccineRequest":{
 "pincode":"",
 "userName":"sameer pande",
 "userPhoneNumber":"",
 "userEmail":""
  }
}

<img width="1680" alt="Screenshot 2022-07-05 at 11 17 16 PM" src="https://user-images.githubusercontent.com/20887384/177386277-ded506e4-7bf1-4ced-bebf-a9f1c3322710.png">
