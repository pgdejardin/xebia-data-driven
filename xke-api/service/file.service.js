import AWS from 'aws-sdk';

const s3 = new AWS.S3();

export function sanitizeFile(xkeFile, year, month) {
    const slots = JSON.parse(xkeFile.Body.toString());

    const excludesSlots = process.env.SLOTS_TO_EXCLUDE.split(",")
        .map(val => val.trim())
        .map(val => val.toUpperCase());

    return slots
        .filter(slot => !excludesSlots.includes(slot.summary.toUpperCase()))
        .map(extractFields)
        .map(enrich(year, month));
}

function extractFields(slot) {
    const description = slot.description;
    const newSlot = {};
    newSlot.summary = slot.summary;
    newSlot.speakers = slot.attendees;
    newSlot.description = slot.description;
    newSlot.pitch = extractDataForField(description, 'Pitch**', '**');
    newSlot.level = extractDataForField(description, 'Niveau**', '**');
    newSlot.requirement = extractDataForField(description, 'Pré-requis**', '**');
    newSlot.logistics = extractDataForField(description, 'Logistique**');

    return newSlot;
}

function enrich(year, month) {
    return slot => {
        slot.year = year;
        slot.month = month;
        return slot;
    };
}

function extractDataForField(description, startMatch, endMatch) {
    const startIndex = description.indexOf(startMatch);
    if (startIndex === -1) {
        return null;
    }
    let endIndex = description.length;
    if (endMatch) {
        endIndex = description.indexOf('**', startIndex + startMatch.length);
    }

    let value = description.substring(startIndex + startMatch.length, endIndex);
    if (value) {
        value = value.trim();
    }
    return value;
}

export async function getFileFromBucket(bucket, filename, checkExist) {
    try {
        const params = {
            Bucket: bucket,
            Key: filename
        };

        if(checkExist) {
            await s3.waitFor('objectExists', params).promise();
        }

        return await s3.getObject(params).promise();
    } catch (e) {
        console.error('object not found ', e);
        return null;
    }
}

export async function saveXkeInBucket(bucket, xke) {
    const params = {
        Body: JSON.stringify(xke.slots),
        Bucket: bucket,
        Key: getFilename(xke.year, xke.month),
        ContentType: 'application/json'
    };
    return await s3.putObject(params).promise();
}

export function getFilename(year, month) {
    return `${year}/${month}/xke-${year}-${month}.json`;
}
