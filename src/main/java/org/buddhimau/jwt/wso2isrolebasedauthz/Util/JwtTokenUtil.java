package org.buddhimau.jwt.wso2isrolebasedauthz.Util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {
    private static final long serialVersionUID = -2550185165626007488L;
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
    @Value("${jwt.secret}")
    private String secret;

    private String jsonbody1 = "{\n" + "    \"Request\": {\n" + "        \"Action\": {\n"
            + "            \"Attribute\": [\n" + "                {\n"
            + "                    \"AttributeId\": \"urn:oasis:names:tc:xacml:1.0:action:action-id\",\n"
            + "                    \"Value\": \"token_validation\"\n" + "                }\n" + "            ]\n"
            + "        },\n" + "        \"Resource\": {\n" + "            \"Attribute\": [\n" + "                {\n"
            + "                    \"AttributeId\": \"urn:oasis:names:tc:xacml:1.0:resource:resource-id\",\n"
            + "                    \"Value\": ";

    private String jsonbody2 = " }\n" + "            ]\n" + "        },\n"
            + "                \"urn:oasis:names:tc:xacml:1.0:subject:access-subject\": {\n"
            + "            \"Attribute\": [\n" + "                {\n"
            + "                    \"AttributeId\": \"http://wso2.org/claims/role\",\n"
            + "                    \"Value\":";

    private String jsonbody3 = "}\n" + "            ]\n" + "        }\n" + "    }\n" + "}";

    //retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    //for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        System.out.println("Came here");
        System.out.println("......................................."+Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody());
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
    //check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    //generate token for user
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }
    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }
    //validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean validateTokenRoleBasedAuthz(String token, String requestURI){
        Map<String,String> params = new HashMap<>();
        JSONParser parser = new JSONParser();
        params.put("Authorization","Bearer "+token);


        try {
            String responce = RESTHandler.sendGETRequest("https://localhost:9443/oauth2/userinfo",params);
            JSONObject json = (JSONObject) parser.parse(responce);
            System.out.println(json.get("groups").toString());

            String[] roles = json.get("groups").toString().split(",");

            Map<String,String> postParams = new HashMap<>();

            postParams.put("Authorization","Basic YWRtaW46YWRtaW4=");
            postParams.put("Content-Type","application/json");

            for (String i : roles) {

                String postResponse = RESTHandler
                        .sendPOSTRequest("https://localhost:9443/api/identity/entitlement/decision/pdp", postParams,
                                jsonbody1 +"\""+requestURI+"\""+jsonbody2+ "\""+i +"\""+ jsonbody3);

                System.out.println("post response: " + postResponse);
                if(postResponse.contains("Permit")){
                    return true;

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        //call user info endpoint
        //call introspect endpoint
        //call decision pdp
        //if permit return true

        return false;
    }
}