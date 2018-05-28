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

    console.log(event);

    let tokenMatch = event.authorizationToken.match(/^Bearer (.*)$/);
    if (!tokenMatch || tokenMatch.length < 2) {
        return callback("Unauthorized");
    }

    const [, token] = tokenMatch;
    const decoded = jwt.decode(token, {complete: true});
    if (!decoded || !decoded.header || !decoded.header.kid) {
        return callback("Unauthorized");
    }

    const kid = decoded.header.kid;

    client.getSigningKey(kid, (err, key) => {

        if (err) {
            return callback("Unauthorized");
        }

        const signingKey = key.publicKey || key.rsaPublicKey;

        jwt.verify(token, signingKey, signature, (err, decoded) => {

            if (err) {
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
