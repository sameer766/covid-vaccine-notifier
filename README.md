# covid-vaccine-notifier
This is a application which notifies user about vaccine via sms by taking pincode and phone number as input primarily. 
Scheduler microservice is entry point where a person schedules sms whenver slot is available by giving pincode and phone number as input primarily.
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
