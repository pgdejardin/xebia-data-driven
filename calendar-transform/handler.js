import AWS from "aws-sdk";
import {sanitizeFile, getFileFromBucket} from './services/file.service';

const dynamodb = new AWS.DynamoDB();


export const handler = async (event) => {
    console.log(event);
    const record = event.Records[0];
    const s3data = record.s3;
    const bucket = s3data.bucket.name;
    const inputFilename = decodeURIComponent(s3data.object.key);

    const [, year, month] = inputFilename.match(/^xke-calendar\/(\d{4})-(\d{2})\.json/);

    const xkeFile = await getFileFromBucket(bucket, inputFilename);

    const slots = sanitizeFile(xkeFile);

    const xke = {year, month, slots};

    console.log('xke :', xke);
    await createXke(xke);
};

async function createXke(xke) {
    const params = {
        TableName: process.env.XKE_TABLE,
        Item: {
            "year": {S: xke.year},
            "month": {S: xke.month},
            "slots": {S: JSON.stringify(xke.slots)}
        }
    };

    await dynamodb.putItem(params).promise();
}




