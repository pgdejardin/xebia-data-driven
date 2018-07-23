'use strict';

const AWS = require('aws-sdk');
const moment = require('moment');

const s3 = new AWS.S3({apiVersion: '2014-10-01'});

const datalakeBucket = process.env['DATALAKE_BUCKET_NAME'];

const userDirectoryPrefix = process.env['USER_DIRECTORY_PREFIX'];
const userDirectorySuffix = process.env['USER_DIRECTORY_SUFFIX'];

const hrSheetPrefix = process.env['HR_SHEET_PREFIX'];
const hrSheetSuffix = process.env['HR_SHEET_SUFFIX'];

module.exports = {
    findUserExport,
    findHrInfoExport,
};

async function findUserExport() {

    return await findExport(userDirectoryPrefix, userDirectorySuffix);
}

async function findHrInfoExport() {

    return await findExport(hrSheetPrefix, hrSheetSuffix);
}

async function findExport(keyPrefix, keySuffix) {

    const date = moment();

    let key = keyPrefix + '/' + date.format("YYYY/MM/DD") + '/' + keySuffix;

    const data = await s3.getObject({Bucket: datalakeBucket, Key: key}).promise();

    return JSON.parse(data.Body.toString());
}
