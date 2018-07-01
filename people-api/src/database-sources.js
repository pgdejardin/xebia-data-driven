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

    const beginDate = moment();
    const endDate = beginDate.clone().add(-4, 'd');

    return await findRecentExport(keyPrefix, keySuffix, beginDate, endDate);
}

async function findRecentExport(keyPrefix, keySuffix, beginDate, endDate) {

    let key = keyPrefix + '/' + beginDate.format("YYYY/MM/DD") + '/' + keySuffix;

    try {

        console.log('Searching content at', key);

        const data = await s3.getObject({Bucket: datalakeBucket, Key: key}).promise();

        console.log('Found content at', key);

        return JSON.parse(data.Body.toString());

    } catch (error) {

        if (beginDate.isBefore(endDate)) {
            console.log('No content found at', key);
            throw error;
        }

        const nextBeginDate = beginDate.clone().add(-1, 'd');

        return await findRecentExport(keyPrefix, keySuffix, nextBeginDate, endDate);
    }

}
