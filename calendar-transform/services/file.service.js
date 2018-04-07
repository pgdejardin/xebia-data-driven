import AWS from 'aws-sdk';

const s3 = new AWS.S3();

export function sanitizeFile(xkeFile) {
    // console.log(xkeFile.Body.toString());
    const slots = JSON.parse(xkeFile.Body.toString());
    // const slots = xkeFile.Body;
    const excludesSlots = process.env.SLOTS_TO_EXCLUDE.split(",")
        .map(val => val.trim())
        .map(val => val.toUpperCase());

    return slots
        .filter(slot => !excludesSlots.includes(slot.summary.toUpperCase()))
        .map(extractFields);
}

function extractFields(slot) {
    const description = slot.description;
    const newSlot = {};
    newSlot.pitch = extractDataForField(description, 'Pitch**','**');
    newSlot.level = extractDataForField(description, 'Niveau**', '**');
    newSlot.requirement = extractDataForField(description, 'Pré-requis**', '**');
    newSlot.logistics = extractDataForField(description, 'Logistique**');

    return newSlot;
}

function extractDataForField(description, startMatch, endMatch) {
    const startIndex = description.indexOf(startMatch);
    let endIndex = description.length;
    if(endMatch) {
        endIndex = description.indexOf('**', startIndex + startMatch.length);
    }

    let value = description.substring(startIndex + startMatch.length, endIndex);
    if (value) {
        value = value.trim();
    }
    return value;
}

export async function getFileFromBucket(bucket, filename) {
    const params = {
        Bucket: bucket,
        Key: filename
    };

    await s3.waitFor('objectExists', params).promise();

    return await s3.getObject(params).promise();
}
