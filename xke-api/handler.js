import {pushXkeInDB, findByKey, findXkeByYear} from './service/xke.repository';

import {sanitizeFile, getFileFromBucket, saveXkeInBucket, getFilename} from './service/file.service';

export const transform = async (event) => {
    const record = event.Records[0];
    const s3data = record.s3;
    const bucket = s3data.bucket.name;
    const inputFilename = decodeURIComponent(s3data.object.key);

    const [, year, month] = inputFilename.match(/^raw\/xke-calendar\/.*\/(\d{4})-(\d{2})\.json/);

    const xkeFile = await getFileFromBucket(bucket, inputFilename, true);

    const slots = sanitizeFile(xkeFile, year, month);

    const xke = {year, month, slots};

    await saveXkeInBucket(process.env.XKE_BUCKET_API, xke);

    await pushXkeInDB(xke);
};

export const findXke = async (event, context, callback) => {
    const year = event.pathParameters.year;
    const month = event.pathParameters.month;
    const xkeFilename = getFilename(year, month);
    const file = await getFileFromBucket(process.env.XKE_BUCKET_API, xkeFilename);

    const headers = {
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Credentials': true,
    };

    if (file) {
        callback(null, {
            statusCode: 200,
            headers: headers,
            body: file.Body.toString(),
        });
    } else {
        callback(null, {
            statusCode: 404,
            headers: headers,
            body: "Xke not found"
        });
    }
};
