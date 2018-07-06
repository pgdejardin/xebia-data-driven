'use strict';

const jwksClient = require('jwks-rsa');
const jwt = require('jsonwebtoken');

const signature = {
    audience: process.env.AUDIENCE,
    issuer: process.env.TOKEN_ISSUER
};

const client = jwksClient({
    cache: true,
    rateLimit: true,
    jwksRequestsPerMinute: 1,
    jwksUri: process.env.JWKS_URI
});

module.exports.authenticate = (event, context, callback) => {

    console.log("Received event", event);

    let tokenMatch = event.authorizationToken.match(/^Bearer (.*)$/);
    if (!tokenMatch || tokenMatch.length < 2) {
        console.error("Token has no bearer prefix");
        return callback("Unauthorized");
    }

    const [, token] = tokenMatch;
    const decoded = jwt.decode(token, {complete: true});
    if (!decoded || !decoded.header || !decoded.header.kid) {
        console.error("Token not properly formatted", token);
        return callback("Unauthorized");
    }

    const kid = decoded.header.kid;

    client.getSigningKey(kid, (err, key) => {

        if (err) {
            console.error("Signing key not found", err);
            return callback("Unauthorized");
        }

        const signingKey = key.publicKey || key.rsaPublicKey;

        jwt.verify(token, signingKey, signature, (err, decoded) => {

            if (err) {
                console.error("Token verification failed", err);
                return callback("Unauthorized");
            }

            callback(null, {
                principalId: decoded.sub,
                policyDocument: {
                    Version: '2012-10-17',
                    Statement: [{
                        Action: 'execute-api:Invoke',
                        Effect: 'Allow',
                        Resource: event.methodArn
                    }]
                },
                context: {
                    scope: decoded.scope
                }
            });

        });

    });

};
