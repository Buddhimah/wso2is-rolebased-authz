# wso2is-rolebased-authz
Role Based authorization For Spring BOOT using Wso2 Identity server 

Related Blog : https://medium.com/@buddhimau/role-based-authorization-for-spring-boot-using-wso2-identity-server-3d74ab307fb9

This project is created with the help of 

https://medium.com/@balaajanthan/securing-spring-micro-service-using-wso2-identity-server-dbecf90c402

POSTMAN Requests

curl -X GET \
  http://localhost:8080/hello \
  -H 'Authorization: Bearer eyJ4NXQiOiJOVEF4Wm1NeE5ETXlaRGczTVRVMVpHTTBNekV6T0RKaFpXSTRORE5sWkRVMU9HRmtOakZpTVEiLCJraWQiOiJOVEF4Wm1NeE5ETXlaRGczTVRVMVpHTTBNekV6T0RKaFpXSTRORE5sWkRVMU9HRmtOakZpTVEiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImF1ZCI6IlJzRjJERTh5SVlvalFIdk1yd0xDR2tMUHhhWWEiLCJuYmYiOjE1NjkxNTM1MjYsImF6cCI6IlJzRjJERTh5SVlvalFIdk1yd0xDR2tMUHhhWWEiLCJzY29wZSI6Im9wZW5pZCBzZ2MiLCJpc3MiOiJodHRwczpcL1wvbG9jYWxob3N0Ojk0NDNcL29hdXRoMlwvdG9rZW4iLCJncm91cHMiOlsiSW50ZXJuYWxcL2V2ZXJ5b25lIiwiYWRtaW4iLCJBcHBsaWNhdGlvblwvcGxheWdyb3VuZDIiXSwiZXhwIjoxNTY5MTU3MTI2LCJpYXQiOjE1NjkxNTM1MjYsImp0aSI6Ijg5MmQ2ZmFmLTY1ZDgtNDEwOC04NTI2LWVhNjM2ZmJiYWZkNSJ9.H2yhj7p1j4_E5cBhMLHFSa01LqP9Z150jbKZ-QvR2NoGo8tClwPZjlu4ZfVb1XqVjYxRIeDmECl6s_q4C4oZa-I-TbYZdsic-V5KN56bS7gxSPi5Nfku0NqNQlDxMWhj17QeW8fj-oh6mkpA0WHwbKg1AUe-GydEjw_liwpK041VV-2fG1UiggDA7RPFLFQFqGuwxoWKNZjVmOQeSu-4O8ZNYnyPVd0JrKzz1XhLj9QodqhTobJH3ZDBwpkmFI4hq4-T2V8BTmB8lWPgUFobtdNpp7cfSwUC9IIfly6pkT-eQd9hV2N9fc_Y7pEwTrUym5r_BwFH0G3Mi2R7qyAVqA' \
  -H 'Postman-Token: 419ad87a-34c0-4fbb-97ad-42d8c32c0408' \
  -H 'cache-control: no-cache'
  
  curl -X POST \
  https://localhost:9443/oauth2/token \
  -H 'Authorization: Basic UnNGMkRFOHlJWW9qUUh2TXJ3TENHa0xQeGFZYToxREpuank5WE92TVp0Z1NQZnRsSWxKMGZtbFFh' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -H 'Postman-Token: 5e3e0b62-9ae4-47f7-b293-d203c1ad560d' \
  -H 'cache-control: no-cache' \
  -d 'grant_type=password&username=admin&password=admin&scope=openid%20sgc&undefined='
  
  
  
  
  XACML Policy
  
 

```xml
<Policy xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"  PolicyId="sample" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable" Version="1.0">
   <Target>
      <AnyOf>
         <AllOf>
            <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-regexp-match">
               <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">^/[a-z,A-Z,0-9]{3,100}$</AttributeValue>
               <AttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:resource:resource-id" Category="urn:oasis:names:tc:xacml:3.0:attribute-category:resource" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true"></AttributeDesignator>
            </Match>
         </AllOf>
      </AnyOf>
      <AnyOf>
         <AllOf>
            <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
               <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">token_validation</AttributeValue>
               <AttributeDesignator AttributeId="urn:oasis:names:tc:xacml:1.0:action:action-id" Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true"></AttributeDesignator>
            </Match>
         </AllOf>
      </AnyOf>
      <AnyOf>
         <AllOf>
            <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
               <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">admin</AttributeValue>
               <AttributeDesignator AttributeId="http://wso2.org/claims/role" Category="urn:oasis:names:tc:xacml:1.0:subject:access-subject" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="true"></AttributeDesignator>
            </Match>
         </AllOf>
      </AnyOf>
   </Target>
   <Rule Effect="Permit" RuleId="permit_by_roles"></Rule>
</Policy> 
```
