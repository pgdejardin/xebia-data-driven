'use strict';

const AWS = require('aws-sdk');
const s3 = new AWS.S3({apiVersion: '2014-10-01'});

const peopleBucketName = process.env['PEOPLE_API_BUCKET_NAME'];
const peopleBucketKey = process.env['PEOPLE_API_BUCKET_KEY'];

module.exports = {
    findPeopleList,
    savePeopleList,
};

async function findPeopleList() {

    const params = {
        Bucket: peopleBucketName,
        Key: peopleBucketKey
    };

    const content = await s3.getObject(params).promise();

    return JSON.parse(content.Body.toString());
}

async function savePeopleList(peopleList) {

    const content = JSON.stringify(peopleList);

    const params = {
        Bucket: peopleBucketName,
        Key: peopleBucketKey,
        Body: content,
        ContentType: 'application/json'
    };

    await s3.putObject(params).promise();
}
